package org.dsgroup.journeycraft.navigation.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.api.NavigationService;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.service.PathPlanningService;
import org.dsgroup.journeycraft.navigation.service.RoadNodeService;
import org.dsgroup.journeycraft.navigation.vo.rspvo.NearbyFacilityRspVO;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.dsgroup.journeycraft.scenic.vo.reqvo.FacilityListReqVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.FacilityRspVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 导航模块对外服务接口实现。
 * <p>
 * 封装内部 Service 调用，为其他模块提供简洁的导航功能入口。
 * 其他模块通过 {@code @Resource NavigationService} 注入即可使用。
 *
 * @author 刘方正
 * @since 2026-04-25
 */
@Slf4j
@Service
public class NavigationApiServiceImpl implements NavigationService {

    @Autowired
    private PathPlanningService pathPlanningService;

    @Autowired
    private ScenicService scenicService;

    @Autowired
    private RoadNodeService roadNodeService;

    @Override
    public PathPlanningService.PathPlanningResult planSingleRoute(
            Long scenicAreaId, Long startNodeId, Long endNodeId,
            String strategy, String transportMode) {
        log.info("[API] 单目标路径规划(Dijkstra): 景区={}, {} -> {}", scenicAreaId, startNodeId, endNodeId);
        Integer mode = parseTransportMode(transportMode);
        return pathPlanningService.calculateShortestPath(startNodeId, endNodeId, mode, strategy);
    }

    @Override
    public PathPlanningService.PathPlanningResult planSingleRouteAStar(
            Long scenicAreaId, Long startNodeId, Long endNodeId,
            String strategy, String transportMode) {
        log.info("[API] 单目标路径规划(A*): 景区={}, {} -> {}", scenicAreaId, startNodeId, endNodeId);
        Integer mode = parseTransportMode(transportMode);
        return pathPlanningService.calculateAStarPath(startNodeId, endNodeId, mode, strategy);
    }

    @Override
    public PathPlanningService.MultiTargetRouteResult planMultiRoute(
            Long scenicAreaId, Long startNodeId, List<Long> targetNodeIds,
            boolean needReturn, String transportMode) {
        log.info("[API] 多目标路线规划: 景区={}, 起点={}, 目标数={}, 返回起点={}",
                scenicAreaId, startNodeId, targetNodeIds != null ? targetNodeIds.size() : 0, needReturn);
        Integer mode = parseTransportMode(transportMode);
        return pathPlanningService.calculateMultiTargetRoute(startNodeId, targetNodeIds, mode, needReturn);
    }

    @Override
    public BigDecimal getDistanceBetweenNodes(Long fromNodeId, Long toNodeId) {
        return pathPlanningService.getDistanceBetweenNodes(fromNodeId, toNodeId);
    }

    @Override
    public Integer getTimeBetweenNodes(Long fromNodeId, Long toNodeId, Integer transportMode) {
        return pathPlanningService.getTimeBetweenNodes(fromNodeId, toNodeId, transportMode);
    }

    @Override
    public List<NearbyFacilityRspVO> getNearbyFacilities(
            Long scenicAreaId, Long nodeId, Integer facilityType,
            Integer radiusMeters, Integer limit) {
        log.info("[API] 附近设施查询: 景区={}, 节点={}, 类型={}, 半径={}m, 限制={}",
                scenicAreaId, nodeId, facilityType, radiusMeters, limit);

        // ★ 通过 Scenic 模块 API 获取设施列表
        FacilityListReqVO reqVO = new FacilityListReqVO();
        reqVO.setType(facilityType);
        List<FacilityRspVO> facilities = scenicService.listFacilities(scenicAreaId, reqVO);

        if (facilities == null || facilities.isEmpty()) {
            return Collections.emptyList();
        }

        List<NearbyFacilityRspVO> results = new ArrayList<>();
        for (FacilityRspVO facility : facilities) {
            Long facilityNodeId = pathPlanningService.getFacilityNodeId(facility.getId());
            if (facilityNodeId == null) {
                continue;
            }

            BigDecimal distance;
            if (nodeId.equals(facilityNodeId)) {
                distance = BigDecimal.ZERO;
            } else {
                PathPlanningService.PathPlanningResult pathResult =
                        pathPlanningService.calculateShortestPath(nodeId, facilityNodeId, 1, "shortest_distance");
                distance = pathResult != null ? pathResult.getTotalDistance() : null;
            }

            if (distance == null || distance.doubleValue() > radiusMeters) {
                continue;
            }

            NearbyFacilityRspVO vo = new NearbyFacilityRspVO();
            vo.setId(facility.getId());
            vo.setName(facility.getName());
            vo.setType(facility.getType());
            vo.setLatitude(facility.getLatitude());
            vo.setLongitude(facility.getLongitude());
            vo.setDistance(distance);
            results.add(vo);
        }

        results.sort((a, b) -> a.getDistance().compareTo(b.getDistance()));
        if (results.size() > limit) {
            results = results.subList(0, limit);
        }
        return results;
    }

    @Override
    public BigDecimal getDistanceToTarget(Integer targetType, Long targetId,
                                           BigDecimal userLat, BigDecimal userLng) {
        log.info("[API] 计算用户到目标距离: type={}, targetId={}, userLoc=({},{})",
                targetType, userLat, userLng);

        if (targetType == null || targetId == null || userLat == null || userLng == null) {
            log.warn("[API] getDistanceToTarget 参数不完整");
            return null;
        }

        // 1. 找到目标路网节点
        Long targetNodeId;
        if (targetType == 0) {
            // 景区入口节点: nodeType=0 (入口), scenicAreaId=targetId
            RoadNode entrance = roadNodeService.lambdaQuery()
                    .eq(RoadNode::getScenicAreaId, targetId)
                    .eq(RoadNode::getNodeType, 0)
                    .eq(RoadNode::getEnabled, true)
                    .last("LIMIT 1")
                    .one();
            targetNodeId = entrance != null ? entrance.getId() : null;
            log.info("[API] 景区入口节点: scenicAreaId={}, nodeId={}", targetId, targetNodeId);
        } else if (targetType == 1) {
            // 建筑物关联节点: buildingId=targetId
            RoadNode first = roadNodeService.lambdaQuery()
                    .eq(RoadNode::getBuildingId, targetId)
                    .eq(RoadNode::getEnabled, true)
                    .last("LIMIT 1")
                    .one();
            targetNodeId = first != null ? first.getId() : null;
            log.info("[API] 建筑物关联节点: buildingId={}, nodeId={}", targetId, targetNodeId);
        } else {
            log.warn("[API] 不支持的目标类型: {}", targetType);
            return null;
        }

        if (targetNodeId == null) {
            log.warn("[API] 未找到目标的关联路网节点");
            return null;
        }

        // 2. 找用户坐标最近的路网节点
        Long userNearestNodeId = pathPlanningService.findNearestNodeByCoords(userLat, userLng);
        if (userNearestNodeId == null) {
            log.warn("[API] 未找到用户坐标附近的路网节点");
            return null;
        }
        log.info("[API] 用户最近节点: nodeId={}", userNearestNodeId);

        // 3. Dijkstra 计算两节点间的路网距离
        PathPlanningService.PathPlanningResult result =
                pathPlanningService.calculateShortestPath(
                        userNearestNodeId, targetNodeId, 1, "shortest_distance");

        if (result == null) {
            log.warn("[API] 无法计算从节点 {} 到节点 {} 的路径",
                    userNearestNodeId, targetNodeId);
            return null;
        }

        log.info("[API] 路网距离计算完成: {} -> {} = {}m",
                userNearestNodeId, targetNodeId, result.getTotalDistance());
        return result.getTotalDistance();
    }

    private Integer parseTransportMode(String transportMode) {
        if (transportMode == null) return 1;
        return switch (transportMode.toLowerCase()) {
            case "walk" -> 1;
            case "bike" -> 2;
            case "shuttle" -> 3;
            default -> 1;
        };
    }
}
