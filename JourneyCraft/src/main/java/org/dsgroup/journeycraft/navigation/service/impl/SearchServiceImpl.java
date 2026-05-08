package org.dsgroup.journeycraft.navigation.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.mapper.RoadNodeMapper;
import org.dsgroup.journeycraft.navigation.service.SearchService;
import org.dsgroup.journeycraft.navigation.utils.CoordinateTransformUtil;
import org.dsgroup.journeycraft.navigation.vo.rspvo.NodeSearchItemVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.ScenicSearchItemVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.SearchResultVO;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicSearchReqVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicItemRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicListRspVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ScenicService scenicService;

    @Autowired
    private RoadNodeMapper roadNodeMapper;

    @Override
    public SearchResultVO search(String keyword, String types, int limit, int offset) {
        boolean searchScenic = false;
        boolean searchNode = false;

        if (types != null && !types.isBlank()) {
            for (String t : types.split(",")) {
                String trimmed = t.trim().toLowerCase();
                if ("scenic".equals(trimmed)) {
                    searchScenic = true;
                } else if ("node".equals(trimmed) || "road_node".equals(trimmed)) {
                    searchNode = true;
                }
            }
        }

        if (keyword == null || keyword.isBlank()) {
            searchNode = false;
        }

        List<ScenicSearchItemVO> scenicResults = new ArrayList<>();
        List<NodeSearchItemVO> nodeResults = new ArrayList<>();

        if (searchScenic) {
            ScenicSearchReqVO reqVO = new ScenicSearchReqVO();
            reqVO.setKeyword(keyword != null ? keyword : "");
            reqVO.setPage(1);
            reqVO.setSize(Math.max(limit, 10));
            ScenicListRspVO scenicListRsp = scenicService.searchScenic(reqVO);
            if (scenicListRsp != null && scenicListRsp.getList() != null) {
                for (ScenicItemRspVO item : scenicListRsp.getList()) {
                    ScenicSearchItemVO vo = new ScenicSearchItemVO();
                    vo.setId(item.getId());
                    vo.setName(item.getName());
                    vo.setScenicType(item.getType());
                    vo.setCity(item.getCity());
                    vo.setRating(item.getRating());
                    vo.setHeatScore(item.getHeatScore());

                    if (item.getLatitude() != null && item.getLongitude() != null) {
                        CoordinateTransformUtil.Gcj02Coord gcj02 = CoordinateTransformUtil.wgs84ToGcj02(
                                item.getLatitude().doubleValue(), item.getLongitude().doubleValue());
                        vo.setLatitude(gcj02.lat());
                        vo.setLongitude(gcj02.lng());
                    }

                    scenicResults.add(vo);
                }
            }
        }

        if (searchNode) {
            List<Map<String, Object>> nodeRows = roadNodeMapper.selectByNameWithTransport(keyword, limit, offset);
            if (nodeRows != null) {
                for (Map<String, Object> row : nodeRows) {
                    NodeSearchItemVO vo = new NodeSearchItemVO();
                    vo.setId((Long) row.get("id"));
                    vo.setOsmId((Long) row.get("osm_id"));
                    vo.setName((String) row.get("name"));
                    vo.setNodeType((Integer) row.get("node_type"));
                    vo.setType("road_node");

                    BigDecimal lat = (BigDecimal) row.get("latitude");
                    BigDecimal lng = (BigDecimal) row.get("longitude");
                    if (lat != null && lng != null) {
                        CoordinateTransformUtil.Gcj02Coord gcj02 = CoordinateTransformUtil.wgs84ToGcj02(
                                lat.doubleValue(), lng.doubleValue());
                        vo.setLatitude(gcj02.lat());
                        vo.setLongitude(gcj02.lng());
                    }

                    String transportStr = (String) row.get("transport_types");
                    List<Integer> transportTypes = new ArrayList<>();
                    if (transportStr != null) {
                        for (String s : transportStr.split(",")) {
                            transportTypes.add(Integer.parseInt(s.trim()));
                        }
                    }
                    vo.setTransportTypes(transportTypes);

                    nodeResults.add(vo);
                }
            }
        }

        SearchResultVO result = new SearchResultVO();
        result.setScenicResults(scenicResults);
        result.setNodeResults(nodeResults);
        result.setTotal(scenicResults.size() + nodeResults.size());
        result.setPage(offset / limit + 1);
        result.setSize(limit);

        return result;
    }
}
