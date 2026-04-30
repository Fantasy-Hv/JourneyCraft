package org.dsgroup.journeycraft.scenic.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.dsgroup.journeycraft.scenic.entity.Building;
import org.dsgroup.journeycraft.scenic.entity.CrowdLevel;
import org.dsgroup.journeycraft.scenic.entity.Facility;
import org.dsgroup.journeycraft.scenic.entity.ScenicArea;
import org.dsgroup.journeycraft.scenic.mapper.BuildingMapper;
import org.dsgroup.journeycraft.scenic.mapper.CrowdLevelMapper;
import org.dsgroup.journeycraft.scenic.mapper.FacilityMapper;
import org.dsgroup.journeycraft.scenic.mapper.ScenicAreaMapper;
import org.dsgroup.journeycraft.scenic.vo.reqvo.BuildingListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.FacilityListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ReportCrowdReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicSearchReqVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.BuildingRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.FacilityRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicItemRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicListRspVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

/**
 * 景点服务实现，负责景点查询与拥挤度上报。
 */
@Service
@RequiredArgsConstructor
public class ScenicServiceImpl implements ScenicService {

    private final ScenicAreaMapper scenicAreaMapper;
    private final BuildingMapper buildingMapper;
    private final FacilityMapper facilityMapper;
    private final CrowdLevelMapper crowdLevelMapper;
    private final ObjectMapper objectMapper;

    /**
     * 查询景点列表。
     */
    @Override
    public ScenicListRspVO listScenic(ScenicListReqVO reqVO) {
        return queryScenicList(reqVO, null);
    }

    /**
     * 按关键词搜索景点。
     */
    @Override
    public ScenicListRspVO searchScenic(ScenicSearchReqVO reqVO) {
        ScenicListReqVO listReqVO = new ScenicListReqVO();
        listReqVO.setType(reqVO.getType());
        listReqVO.setCity(reqVO.getCity());
        listReqVO.setPage(reqVO.getPage());
        listReqVO.setSize(reqVO.getSize());
        return queryScenicList(listReqVO, reqVO.getKeyword());
    }

    /**
     * 统一列表查询逻辑，包含分页与排序。
     */
    private ScenicListRspVO queryScenicList(ScenicListReqVO reqVO, String keyword) {
        int page = reqVO.getPage() == null || reqVO.getPage() < 1 ? 1 : reqVO.getPage();
        int size = reqVO.getSize() == null || reqVO.getSize() < 1 ? 10 : reqVO.getSize();
        boolean sortByDistance = "distance".equalsIgnoreCase(reqVO.getSortBy())
                && reqVO.getLatitude() != null
                && reqVO.getLongitude() != null;

        var queryWrapper = lambdaQuery(ScenicArea.class)
                .eq(reqVO.getType() != null, ScenicArea::getType, reqVO.getType())
                .eq(reqVO.getCity() != null && !reqVO.getCity().isBlank(), ScenicArea::getCity, reqVO.getCity())
                .like(keyword != null && !keyword.isBlank(), ScenicArea::getName, keyword)
                .orderByDesc("desc".equalsIgnoreCase(reqVO.getSortOrder()) && "heatScore".equals(reqVO.getSortBy()), ScenicArea::getHeatScore)
                .orderByDesc("desc".equalsIgnoreCase(reqVO.getSortOrder()) && "rating".equals(reqVO.getSortBy()), ScenicArea::getRating)
                .orderByAsc("asc".equalsIgnoreCase(reqVO.getSortOrder()) && "heatScore".equals(reqVO.getSortBy()), ScenicArea::getHeatScore)
                .orderByAsc("asc".equalsIgnoreCase(reqVO.getSortOrder()) && "rating".equals(reqVO.getSortBy()), ScenicArea::getRating)
                .orderByDesc(!sortByDistance, ScenicArea::getHeatScore);

        List<ScenicArea> scenicAreas;
        int total;
        if (sortByDistance) {
            List<ScenicArea> allMatched = scenicAreaMapper.selectList(queryWrapper);
            total = allMatched.size();
            int needCount = Math.min(page * size, total);
            boolean desc = "desc".equalsIgnoreCase(reqVO.getSortOrder());
            scenicAreas = selectTopKByDistance(allMatched, reqVO.getLatitude(), reqVO.getLongitude(), needCount, desc);
            int from = Math.min((page - 1) * size, scenicAreas.size());
            int to = Math.min(from + size, scenicAreas.size());
            scenicAreas = scenicAreas.subList(from, to);
        } else {
            Page<ScenicArea> pageParam = new Page<>(page, size);
            Page<ScenicArea> pageResult = scenicAreaMapper.selectPage(pageParam, queryWrapper);
            scenicAreas = pageResult.getRecords();
            total = (int) pageResult.getTotal();
        }

        List<ScenicItemRspVO> items = new ArrayList<>();
        for (ScenicArea scenicArea : scenicAreas) {
            items.add(toScenicItemRsp(scenicArea));
        }

        ScenicListRspVO rspVO = new ScenicListRspVO();
        rspVO.setList(items);
        rspVO.setTotal(total);
        rspVO.setPage(page);
        rspVO.setSize(size);
        return rspVO;
    }

    /**
     * 查询景点详情。
     */
    @Override
    public ScenicItemRspVO getScenicDetail(Long scenicId) {
        ScenicArea scenicArea = scenicAreaMapper.selectById(scenicId);
        if (scenicArea == null) {
            throw new BusinessException(ResponseCodeEnum.SCENIC_NOT_FOUND, "景点不存在");
        }
        return toScenicItemRsp(scenicArea);
    }

    /**
     * 查询建筑物列表。
     */
    @Override
    public List<BuildingRspVO> listBuildings(Long scenicId, BuildingListReqVO reqVO) {
        List<Building> buildings = buildingMapper.selectList(lambdaQuery(Building.class)
                .eq(Building::getScenicAreaId, scenicId)
                .eq(reqVO.getType() != null, Building::getType, reqVO.getType()));
        List<BuildingRspVO> result = new ArrayList<>();
        for (Building building : buildings) {
            BuildingRspVO rspVO = new BuildingRspVO();
            rspVO.setId(building.getId());
            rspVO.setName(building.getName());
            rspVO.setType(building.getType());
            rspVO.setFloorCount(building.getFloorCount());
            rspVO.setLatitude(building.getLatitude());
            rspVO.setLongitude(building.getLongitude());
            rspVO.setDescription(building.getDescription());
            rspVO.setImages(parseStringList(building.getImages()));
            result.add(rspVO);
        }
        return result;
    }

    /**
     * 查询设施列表。
     */
    @Override
    public List<FacilityRspVO> listFacilities(Long scenicId, FacilityListReqVO reqVO) {
        List<Facility> facilities = facilityMapper.selectList(lambdaQuery(Facility.class)
                .eq(Facility::getScenicAreaId, scenicId)
                .eq(reqVO.getType() != null, Facility::getType, reqVO.getType())
                .eq(reqVO.getBuildingId() != null, Facility::getBuildingId, reqVO.getBuildingId()));
        List<FacilityRspVO> result = new ArrayList<>();
        for (Facility facility : facilities) {
            FacilityRspVO rspVO = new FacilityRspVO();
            rspVO.setId(facility.getId());
            rspVO.setName(facility.getName());
            rspVO.setType(facility.getType());
            rspVO.setSubtype(facility.getSubtype());
            rspVO.setLatitude(facility.getLatitude());
            rspVO.setLongitude(facility.getLongitude());
            rspVO.setRating(facility.getRating());
            rspVO.setPriceRange(facility.getPriceRange());
            rspVO.setImages(parseStringList(facility.getImages()));
            result.add(rspVO);
        }
        return result;
    }

    /**
     * 写入拥挤度上报记录。
     */
    @Override
    public void reportCrowd(Long scenicId, ReportCrowdReqVO reqVO) {
        ScenicArea scenicArea = scenicAreaMapper.selectById(scenicId);
        if (scenicArea == null) {
            throw new BusinessException(ResponseCodeEnum.SCENIC_NOT_FOUND, "景点不存在");
        }
        CrowdLevel crowdLevel = new CrowdLevel();
        crowdLevel.setScenicAreaId(scenicId);
        crowdLevel.setNodeId(reqVO.getNodeId());
        crowdLevel.setLevel(reqVO.getLevel());
        crowdLevel.setCrowdCount(reqVO.getCrowdCount());
        crowdLevel.setSource(1);
        crowdLevel.setRecordedAt(java.util.Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        crowdLevel.setCreatedAt(java.util.Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        crowdLevelMapper.insert(crowdLevel);
    }

    /**
     * 获取景区的最新拥挤度数据（供 Navigation 4.5 接口调用）
     * <p>
     * 返回每个节点最新的一条记录
     */
    @Override
    public List<CrowdLevel> getCrowdLevelsByScenicArea(Long scenicAreaId) {
        // 查询该景区的所有拥挤度记录
        List<CrowdLevel> allRecords = crowdLevelMapper.selectList(lambdaQuery(CrowdLevel.class)
                .eq(CrowdLevel::getScenicAreaId, scenicAreaId)
                .orderByDesc(CrowdLevel::getRecordedAt));
        
        // 按节点分组，每组取最新一条
        return allRecords.stream()
                .collect(java.util.stream.Collectors.groupingBy(CrowdLevel::getNodeId))
                .values().stream()
                .map(list -> list.get(0))  // 每组取第一条（已按时间排序）
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取校园列表（复用景点列表逻辑）。
     */
    @Override
    public ScenicListRspVO listCampus(ScenicListReqVO reqVO) {
        return listScenic(reqVO);
    }

    /**
     * 景点实体转响应对象。
     */
    private ScenicItemRspVO toScenicItemRsp(ScenicArea scenicArea) {
        ScenicItemRspVO rspVO = new ScenicItemRspVO();
        rspVO.setId(scenicArea.getId());
        rspVO.setName(scenicArea.getName());
        rspVO.setType(scenicArea.getType());
        rspVO.setCity(scenicArea.getCity());
        rspVO.setAddress(scenicArea.getAddress());
        rspVO.setLatitude(scenicArea.getLatitude());
        rspVO.setLongitude(scenicArea.getLongitude());
        rspVO.setDescription(scenicArea.getDescription());
        rspVO.setRating(scenicArea.getRating());
        rspVO.setHeatScore(scenicArea.getHeatScore());
        rspVO.setVisitCount(scenicArea.getVisitCount());
        rspVO.setTicketPrice(scenicArea.getTicketPrice());
        rspVO.setOpeningHours(parseOpeningHours(scenicArea.getOpeningHours()));
        rspVO.setImages(parseStringList(scenicArea.getImages()));
        return rspVO;
    }

    /**
     * 解析图片 JSON 数组字段。
     */
    private List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_ERROR, "图片字段解析失败");
        }
    }

    /**
     * 解析开放时间 JSON 字段。
     */
    private Map<String, Map<String, String>> parseOpeningHours(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_ERROR, "开放时间解析失败");
        }
    }

    /**
     * 计算两点之间球面距离（米）。
     */
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    /**
     * 计算景点到当前位置的距离。
     */
    private double scenicDistance(double lat, double lon, ScenicArea scenicArea) {
        if (scenicArea.getLatitude() == null || scenicArea.getLongitude() == null) {
            return Double.MAX_VALUE;
        }
        return distance(lat, lon, scenicArea.getLatitude().doubleValue(), scenicArea.getLongitude().doubleValue());
    }

    /**
     * 用 Top-K 选择替代全量排序，降低距离排序开销。
     */
    private List<ScenicArea> selectTopKByDistance(List<ScenicArea> source,
                                                  double lat,
                                                  double lon,
                                                  int k,
                                                  boolean desc) {
        if (k <= 0 || source.isEmpty()) {
            return List.of();
        }
        Comparator<ScenicArea> byDistanceAsc = Comparator.comparingDouble(s -> scenicDistance(lat, lon, s));
        // `desc=true` 取距离最大的K个，用最小堆；`desc=false` 取最小K个，用最大堆。
        Comparator<ScenicArea> heapComparator = desc ? byDistanceAsc : byDistanceAsc.reversed();
        PriorityQueue<ScenicArea> heap = new PriorityQueue<>(heapComparator);
        for (ScenicArea scenicArea : source) {
            if (heap.size() < k) {
                heap.offer(scenicArea);
                continue;
            }
            ScenicArea top = heap.peek();
            if (top == null) {
                continue;
            }
            int compare = byDistanceAsc.compare(scenicArea, top);
            boolean shouldReplace = desc ? compare > 0 : compare < 0;
            if (shouldReplace) {
                heap.poll();
                heap.offer(scenicArea);
            }
        }
        List<ScenicArea> selected = new ArrayList<>(heap);
        selected.sort(desc ? byDistanceAsc.reversed() : byDistanceAsc);
        return selected;
    }
}
