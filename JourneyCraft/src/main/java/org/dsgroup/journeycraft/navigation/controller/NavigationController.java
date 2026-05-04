package org.dsgroup.journeycraft.navigation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.NavigationRoute;
import org.dsgroup.journeycraft.navigation.service.NavigationRouteService;
import org.dsgroup.journeycraft.navigation.service.PathPlanningService;
import org.dsgroup.journeycraft.navigation.service.impl.NavigationApiServiceImpl;
import org.dsgroup.journeycraft.navigation.vo.rspvo.CongestionRspVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.NearbyFacilityRspVO;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.dsgroup.journeycraft.scenic.vo.reqvo.FacilityListReqVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.FacilityRspVO;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导航核心控制器
 * <p>
 * 提供路径规划、室内导航、附近设施查询等核心导航功能
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/navigation")
@Tag(name = "导航模块-核心功能", description = "路径规划、室内导航等核心导航功能接口")
public class NavigationController {

    @Autowired
    private PathPlanningService pathPlanningService;
    
    @Autowired
    private NavigationRouteService navigationRouteService;
    
    @Autowired
    private ScenicService scenicService;

    @Autowired
    private NavigationApiServiceImpl navigationApiServiceImpl;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/route")
    @Operation(summary = "单目标路径规划", description = "计算从起点到终点的最优路径，支持 Dijkstra 和 A* 两种算法")
    public Response<PathPlanningService.PathPlanningResult> calculateRoute(
            @Parameter(description = "景区ID", required = true) @RequestParam Long scenicAreaId,
            @Parameter(description = "起点节点ID", required = true) @RequestParam Long startNodeId,
            @Parameter(description = "终点节点ID", required = true) @RequestParam Long endNodeId,
            @Parameter(description = "规划策略") @RequestParam(required = false, defaultValue = "shortest_distance") String strategy,
            @Parameter(description = "交通方式: walk/bike/shuttle") @RequestParam(required = false, defaultValue = "walk") String transportMode,
            @Parameter(description = "算法: dijkstra/astar") @RequestParam(required = false, defaultValue = "dijkstra") String algorithm) {
        try {
            log.info("单目标路径规划: 景区={}, 起点={}, 终点={}, 策略={}, 交通方式={}, 算法={}", 
                     scenicAreaId, startNodeId, endNodeId, strategy, transportMode, algorithm);
            
            Integer mode = parseTransportMode(transportMode);
            PathPlanningService.PathPlanningResult result;
            
            // 根据算法参数选择 Dijkstra 或 A*
            if ("astar".equalsIgnoreCase(algorithm)) {
                result = pathPlanningService.calculateAStarPath(startNodeId, endNodeId, mode, strategy);
            } else {
                result = pathPlanningService.calculateShortestPath(startNodeId, endNodeId, mode, strategy);
            }
            
            if (result == null) {
                return Response.error("未找到可行路径");
            }
            
            // 保存路线到数据库
            NavigationRoute route = saveRoute(null, scenicAreaId, startNodeId, endNodeId, result);
            result.setRouteId(route.getId());
            
            return Response.ok(result);
        } catch (Exception e) {
            log.error("路径规划失败", e);
            return Response.error("路径规划失败: " + e.getMessage());
        }
    }

    @PostMapping("/multi-route")
    @Operation(summary = "多目标路线规划", description = "计算访问多个目标点的最优路线")
    public Response<PathPlanningService.MultiTargetRouteResult> calculateMultiRoute(
            @Parameter(description = "景区ID", required = true) @RequestParam Long scenicAreaId,
            @Parameter(description = "起点节点ID", required = true) @RequestParam Long startNodeId,
            @Parameter(description = "终点节点ID列表", required = true) @RequestParam String endNodeIds,
            @Parameter(description = "规划策略") @RequestParam(required = false, defaultValue = "shortest_distance") String strategy,
            @Parameter(description = "交通方式: walk/bike/shuttle") @RequestParam(required = false, defaultValue = "walk") String transportMode,
            @Parameter(description = "是否需要返回起点") @RequestParam(required = false, defaultValue = "false") Boolean needReturn) {
        try {
            log.info("多目标路线规划: 景区={}, 起点={}, 目标={}, 返回={}", 
                     scenicAreaId, startNodeId, endNodeIds, needReturn);
            
            // 解析节点ID列表
            List<Long> targetIds = parseNodeIdList(endNodeIds);
            if (targetIds.isEmpty()) {
                return Response.error("目标节点列表不能为空");
            }
            
            Integer mode = parseTransportMode(transportMode);
            PathPlanningService.MultiTargetRouteResult result = 
                pathPlanningService.calculateMultiTargetRoute(startNodeId, targetIds, mode, needReturn);
            
            if (result == null) {
                return Response.error("未找到可行路线");
            }
            
            return Response.ok(result);
        } catch (Exception e) {
            log.error("多目标路线规划失败", e);
            return Response.error("多目标路线规划失败: " + e.getMessage());
        }
    }

    @GetMapping("/facilities/nearby")
    @Operation(summary = "获取附近设施", description = "根据节点位置查询附近的设施（基于实际路径距离）")
    public Response<List<NearbyFacilityRspVO>> getNearbyFacilities(
            @Parameter(description = "景区ID", required = true) @RequestParam Long scenicAreaId,
            @Parameter(description = "节点ID", required = true) @RequestParam Long nodeId,
            @Parameter(description = "设施类型") @RequestParam(required = false) Integer type,
            @Parameter(description = "搜索半径（米）") @RequestParam(required = false, defaultValue = "500") Integer radius,
            @Parameter(description = "返回数量限制") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            log.info("查询附近设施: 景区={}, 节点={}, 类型={}, 半径={}米, 限制={}", 
                     scenicAreaId, nodeId, type, radius, limit);
            
            // ★ 通过 Scenic 模块 API 获取设施列表（替代临时 TempFacilityService）
            FacilityListReqVO reqVO = new FacilityListReqVO();
            reqVO.setType(type);
            List<FacilityRspVO> facilities = scenicService.listFacilities(scenicAreaId, reqVO);
            
            if (facilities == null || facilities.isEmpty()) {
                log.info("景区{}没有找到设施", scenicAreaId);
                return Response.ok(java.util.Collections.emptyList());
            }
            
            // 计算每个设施到当前节点的实际路径距离
            List<NearbyFacilityRspVO> results = new java.util.ArrayList<>();
            for (FacilityRspVO facility : facilities) {
                // 通过设施ID找到关联的路网节点
                Long facilityNodeId = pathPlanningService.getFacilityNodeId(facility.getId());
                if (facilityNodeId == null) {
                    log.debug("设施{}没有关联的路网节点，跳过", facility.getId());
                    continue;
                }
                
                // 计算从当前节点到设施节点的实际路径距离
                java.math.BigDecimal distance = null;
                if (nodeId.equals(facilityNodeId)) {
                    distance = java.math.BigDecimal.ZERO;
                } else {
                    PathPlanningService.PathPlanningResult pathResult = 
                        pathPlanningService.calculateShortestPath(nodeId, facilityNodeId, 1, "shortest_distance");
                    if (pathResult != null) {
                        distance = pathResult.getTotalDistance();
                    }
                }
                
                if (distance == null) {
                    log.debug("无法计算设施{}到节点{}的路径，跳过", facility.getId(), nodeId);
                    continue;
                }
                
                // 过滤超出半径的设施
                if (distance.doubleValue() > radius) {
                    continue;
                }
                
                // 构建返回结果
                NearbyFacilityRspVO rspVO = new NearbyFacilityRspVO();
                rspVO.setId(facility.getId());
                rspVO.setName(facility.getName());
                rspVO.setType(facility.getType());
                rspVO.setLatitude(facility.getLatitude());
                rspVO.setLongitude(facility.getLongitude());
                rspVO.setDistance(distance);
                results.add(rspVO);
            }
            
            // 3. 按距离排序
            results.sort((a, b) -> a.getDistance().compareTo(b.getDistance()));
            
            // 4. 限制返回数量
            if (results.size() > limit) {
                results = results.subList(0, limit);
            }
            
            log.info("找到{}个附近设施", results.size());
            return Response.ok(results);
            
        } catch (Exception e) {
            log.error("查询附近设施失败", e);
            return Response.error("查询附近设施失败: " + e.getMessage());
        }
    }

    @PostMapping("/indoor/route")
    @Operation(summary = "室内导航", description = "计算室内楼层间的导航路径")
    public Response<IndoorRouteResult> calculateIndoorRoute(
            @Parameter(description = "建筑ID", required = true) @RequestParam Long buildingId,
            @Parameter(description = "起始楼层", required = true) @RequestParam Integer startFloor,
            @Parameter(description = "起始位置标识", required = true) @RequestParam String startNode,
            @Parameter(description = "目标楼层", required = true) @RequestParam Integer endFloor,
            @Parameter(description = "目标位置标识", required = true) @RequestParam String endNode) {
        try {
            log.info("室内导航: 建筑={}, 从{}层{}到{}层{}", buildingId, startFloor, startNode, endFloor, endNode);
            
            // TODO: 实现室内导航算法
            IndoorRouteResult result = new IndoorRouteResult();
            result.setBuildingId(buildingId);
            result.setStartFloor(startFloor);
            result.setEndFloor(endFloor);
            result.setTotalDistance(new java.math.BigDecimal("50.0"));
            result.setEstimatedTime(120);
            result.setSegments(java.util.Collections.emptyList());
            
            return Response.ok(result);
        } catch (Exception e) {
            log.error("室内导航失败", e);
            return Response.error("室内导航失败: " + e.getMessage());
        }
    }

    @GetMapping("/congestion/{scenicId}")
    @Operation(summary = "获取实时拥挤度", description = "获取指定景区的实时拥挤度（调用 NavigationApiServiceImpl）")
    public Response<CongestionRspVO> getCongestion(
            @Parameter(description = "景区ID", required = true) @PathVariable Long scenicId) {
        try {
            log.info("获取景区 {} 的实时拥挤度", scenicId);
            CongestionRspVO result = navigationApiServiceImpl.fetchCrowdLevelData(scenicId);
            return Response.ok(result);
        } catch (Exception e) {
            log.error("获取拥挤度失败", e);
            return Response.error("获取拥挤度失败: " + e.getMessage());
        }
    }

    @GetMapping("/alternative-route")
    @Operation(summary = "获取反向游览建议", description = "获取当前路线的反向游览建议")
    public Response<AlternativeRouteResult> getAlternativeRoute(
            @Parameter(description = "景区ID", required = true) @RequestParam Long scenicAreaId,
            @Parameter(description = "当前路线节点ID列表", required = true) @RequestParam String currentRoute) {
        try {
            log.info("获取反向游览建议: 景区={}, 当前路线={}", scenicAreaId, currentRoute);
            // TODO: 实现反向游览建议算法
            AlternativeRouteResult result = new AlternativeRouteResult();
            result.setAlternativeNodes(java.util.Collections.emptyList());
            result.setReason("反向游览可以避开人流高峰");
            result.setSavedTime(300);
            return Response.ok(result);
        } catch (Exception e) {
            log.error("获取反向游览建议失败", e);
            return Response.error("获取反向游览建议失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Response<String> healthCheck() {
        try {
            long routeCount = navigationRouteService.count();
            return Response.ok("导航核心服务正常，路线总数: " + routeCount);
        } catch (Exception e) {
            log.error("导航服务异常", e);
            return Response.error("导航服务异常: " + e.getMessage());
        }
    }

    // ============ 私有辅助方法 ============

    private Integer parseTransportMode(String transportMode) {
        if (transportMode == null) return 1;
        return switch (transportMode.toLowerCase()) {
            case "walk" -> 1;
            case "bike" -> 2;
            case "shuttle" -> 3;
            default -> 1;
        };
    }

    private List<Long> parseNodeIdList(String nodeIds) {
        List<Long> result = new java.util.ArrayList<>();
        if (nodeIds == null || nodeIds.isEmpty()) {
            return result;
        }
        for (String id : nodeIds.split(",")) {
            try {
                result.add(Long.parseLong(id.trim()));
            } catch (NumberFormatException e) {
                log.warn("无效的节点ID: {}", id);
            }
        }
        return result;
    }

    private NavigationRoute saveRoute(Long userId, Long scenicAreaId, Long startNodeId, 
                                        Long endNodeId, PathPlanningService.PathPlanningResult result) {
        NavigationRoute route = new NavigationRoute();
        route.setUserId(userId);
        route.setScenicAreaId(scenicAreaId);
        route.setStartNodeId(startNodeId);
        route.setPathNodes(toJson(result.getNodes()));
        route.setTotalDistance(result.getTotalDistance());
        route.setEstimatedTime(result.getEstimatedTime());
        route.setStrategy(result.getStrategy());
        navigationRouteService.save(route);
        return route;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return "[]";
        }
    }

    // ============ 内部类 ============

    public static class IndoorRouteResult {
        private Long buildingId;
        private Integer startFloor;
        private Integer endFloor;
        private java.math.BigDecimal totalDistance;
        private Integer estimatedTime;
        private List<Segment> segments;

        public static class Segment {
            private String type;
            private Integer fromFloor;
            private Integer toFloor;
            private java.math.BigDecimal distance;
            private String instructions;

            public String getType() { return type; }
            public void setType(String type) { this.type = type; }
            public Integer getFromFloor() { return fromFloor; }
            public void setFromFloor(Integer fromFloor) { this.fromFloor = fromFloor; }
            public Integer getToFloor() { return toFloor; }
            public void setToFloor(Integer toFloor) { this.toFloor = toFloor; }
            public java.math.BigDecimal getDistance() { return distance; }
            public void setDistance(java.math.BigDecimal distance) { this.distance = distance; }
            public String getInstructions() { return instructions; }
            public void setInstructions(String instructions) { this.instructions = instructions; }
        }

        public Long getBuildingId() { return buildingId; }
        public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
        public Integer getStartFloor() { return startFloor; }
        public void setStartFloor(Integer startFloor) { this.startFloor = startFloor; }
        public Integer getEndFloor() { return endFloor; }
        public void setEndFloor(Integer endFloor) { this.endFloor = endFloor; }
        public java.math.BigDecimal getTotalDistance() { return totalDistance; }
        public void setTotalDistance(java.math.BigDecimal totalDistance) { this.totalDistance = totalDistance; }
        public Integer getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
        public List<Segment> getSegments() { return segments; }
        public void setSegments(List<Segment> segments) { this.segments = segments; }
    }

    public static class AlternativeRouteResult {
        private List<Long> alternativeNodes;
        private String reason;
        private Integer savedTime;

        public List<Long> getAlternativeNodes() { return alternativeNodes; }
        public void setAlternativeNodes(List<Long> alternativeNodes) { this.alternativeNodes = alternativeNodes; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Integer getSavedTime() { return savedTime; }
        public void setSavedTime(Integer savedTime) { this.savedTime = savedTime; }
    }
}
