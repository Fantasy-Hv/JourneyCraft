package org.dsgroup.journeycraft.navigation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.navigation.entity.NavigationRoute;
import org.dsgroup.journeycraft.navigation.entity.RoadEdge;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.entity.ScenicAccess;
import org.dsgroup.journeycraft.navigation.mapper.RoadEdgeMapper;
import org.dsgroup.journeycraft.navigation.mapper.RoadNodeMapper;
import org.dsgroup.journeycraft.navigation.mapper.ScenicAccessMapper;
import org.dsgroup.journeycraft.navigation.mapper.SpatialRoadNodeMapper;
import org.dsgroup.journeycraft.navigation.service.NavigationRouteService;
import org.dsgroup.journeycraft.navigation.service.PathPlanningService;
import org.dsgroup.journeycraft.navigation.service.ScenicAccessService;
import org.dsgroup.journeycraft.navigation.service.impl.NavigationApiServiceImpl;
import org.dsgroup.journeycraft.navigation.utils.CoordinateTransformUtil;
import org.dsgroup.journeycraft.navigation.vo.reqvo.MultiRouteRequest;
import org.dsgroup.journeycraft.navigation.vo.reqvo.RouteRequest;
import org.dsgroup.journeycraft.navigation.vo.reqvo.RouteTarget;
import org.dsgroup.journeycraft.navigation.vo.rspvo.AccessNodeVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.CongestionRspVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.NearestEdgeRspVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.NearbyFacilityRspVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.NearbyRspVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.PoiNodeVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.RouteRspVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.ScenicAccessRspVO;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.dsgroup.journeycraft.scenic.vo.reqvo.FacilityListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicListReqVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.FacilityRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicItemRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicListRspVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private SpatialRoadNodeMapper spatialRoadNodeMapper;

    @Autowired
    private ScenicAccessService scenicAccessService;

    @Autowired
    private RoadNodeMapper roadNodeMapper;

    @Autowired
    private RoadEdgeMapper roadEdgeMapper;

    @Autowired
    private ScenicAccessMapper scenicAccessMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/route")
    @Operation(summary = "单目标路径规划", description = "起点/终点支持景区ID/节点ID/边ID/坐标四种输入")
    public Response<RouteRspVO> calculateRoute(@RequestBody RouteRequest request) {
        try {
            String startType = request.detectStartType();
            String endType = request.detectEndType();
            if (startType == null || endType == null) {
                return Response.error("起点或终点输入无效");
            }

            log.info("路径规划: startType={}, endType={}, mode={}, strategy={}",
                     startType, endType, request.getTransportMode(), request.getStrategy());

            Integer mode = parseTransportMode(request.getTransportMode());

            Long startNodeId = resolveInput(startType, request.getStartScenicAreaId(),
                request.getStartNodeId(), request.getStartEdgeId(), request.getStartSnapPos(),
                request.getStartLat(), request.getStartLng(), request.getTransportMode());
            Long endNodeId = resolveInput(endType, request.getEndScenicAreaId(),
                request.getEndNodeId(), request.getEndEdgeId(), request.getEndSnapPos(),
                request.getEndLat(), request.getEndLng(), request.getTransportMode());

            if (startNodeId == null || endNodeId == null) {
                return Response.error("无法解析起点或终点到路网节点");
            }

            // Build in-memory adjacency list for performance (avoid N+1 DB queries)
            Long scenicAreaId = request.getStartScenicAreaId() != null 
                ? request.getStartScenicAreaId() : request.getEndScenicAreaId();
            Map<Long, List<RoadEdge>> adjacencyList = new HashMap<>();
            if (scenicAreaId != null) {
                List<RoadNode> areaNodes = roadNodeMapper.selectByScenicAreaId(scenicAreaId);
                if (areaNodes != null && !areaNodes.isEmpty()) {
                    List<Long> nodeIds = areaNodes.stream().map(RoadNode::getId).collect(Collectors.toList());
                    nodeIds.add(startNodeId);
                    nodeIds.add(endNodeId);
                    for (Long nid : nodeIds) {
                        List<RoadEdge> edges = roadEdgeMapper.selectOutgoingEdges(nid);
                        if (edges != null) adjacencyList.put(nid, edges);
                    }
                    log.info("构建邻接表: {} 节点, 从 scenicArea={}", adjacencyList.size(), scenicAreaId);
                }
            }

            PathPlanningService.PathPlanningResult result;
            if ("astar".equalsIgnoreCase(request.getAlgorithm())) {
                result = pathPlanningService.calculateAStarPath(startNodeId, endNodeId, mode, request.getStrategy());
            } else if (!adjacencyList.isEmpty()) {
                result = pathPlanningService.calculateShortestPath(startNodeId, endNodeId, mode, request.getStrategy(), adjacencyList);
            } else {
                result = pathPlanningService.calculateShortestPath(startNodeId, endNodeId, mode, request.getStrategy());
            }

            if (result == null) {
                return Response.error("未找到可行路径");
            }

            // Derive scenicAreaId from resolved nodes (may be null for coordinate-only inputs)
            Long routeScenicId = scenicAreaId;
            if (routeScenicId == null) {
                RoadNode startNode = roadNodeMapper.selectById(startNodeId);
                routeScenicId = startNode != null ? startNode.getScenicAreaId() : null;
            }
            if (routeScenicId == null) {
                RoadNode endNode = roadNodeMapper.selectById(endNodeId);
                routeScenicId = endNode != null ? endNode.getScenicAreaId() : null;
            }

            // Save route only if scenic area is known
            if (routeScenicId != null) {
                NavigationRoute route = saveRoute(null, routeScenicId, startNodeId, endNodeId, result);
                result.setRouteId(route.getId());
            }

            RouteRspVO rsp = buildRouteRspVO(result, startType, endType, request, startNodeId, endNodeId);
            rsp.setRouteId(result.getRouteId());
            return Response.ok(rsp);
        } catch (Exception e) {
            log.error("路径规划失败", e);
            return Response.error("路径规划失败: " + e.getMessage());
        }
    }

    @PostMapping("/multi-route")
    @Operation(summary = "多目标路线规划", description = "起点/目标支持景区ID/节点ID/坐标混合输入")
    public Response<RouteRspVO> calculateMultiRoute(@RequestBody MultiRouteRequest request) {
        try {
            String startType = request.detectStartType();
            if (startType == null || request.getTargets() == null || request.getTargets().isEmpty()) {
                return Response.error("起点或目标列表无效");
            }

            Integer mode = parseTransportMode(request.getTransportMode());

            Long startNodeId = resolveInput(startType, request.getStartScenicAreaId(),
                request.getStartNodeId(), request.getStartEdgeId(), request.getStartSnapPos(),
                request.getStartLat(), request.getStartLng(), request.getTransportMode());

            List<Long> targetIds = new ArrayList<>();
            for (RouteTarget target : request.getTargets()) {
                String targetType = target.detectType();
                Long resolved = resolveInput(targetType, target.getScenicAreaId(),
                    target.getNodeId(), null, null, target.getLat(), target.getLng(), request.getTransportMode());
                if (resolved != null) targetIds.add(resolved);
            }

            if (startNodeId == null || targetIds.isEmpty()) {
                return Response.error("无法解析起点或目标");
            }

            // Build adjacency list from scenic area if available
            Long scenicAreaId = request.getStartScenicAreaId();
            Map<Long, List<RoadEdge>> adjList = null;
            if (scenicAreaId != null) {
                List<RoadNode> areaNodes = roadNodeMapper.selectByScenicAreaId(scenicAreaId);
                if (areaNodes != null && !areaNodes.isEmpty()) {
                    adjList = new HashMap<>();
                    for (RoadNode rn : areaNodes) {
                        List<RoadEdge> edges = roadEdgeMapper.selectOutgoingEdges(rn.getId());
                        if (edges != null && !edges.isEmpty()) {
                            adjList.put(rn.getId(), edges);
                        }
                    }
                    log.info("多目标邻接表: scenicArea={}, {} 个节点", scenicAreaId, adjList.size());
                }
            }

            PathPlanningService.MultiTargetRouteResult result;
            if (adjList != null) {
                result = pathPlanningService.calculateMultiTargetRoute(startNodeId, targetIds, mode, request.getStrategy(), 
                    request.getNeedReturn() != null && request.getNeedReturn(), adjList);
            } else {
                result = pathPlanningService.calculateMultiTargetRoute(startNodeId, targetIds, mode, request.getStrategy(), 
                    request.getNeedReturn() != null && request.getNeedReturn());
            }

            if (result == null) return Response.error("未找到可行路线");

            RouteRspVO rsp = new RouteRspVO();
            rsp.setTotalDistance(result.getTotalDistance() != null ? result.getTotalDistance().doubleValue() : 0);
            rsp.setEstimatedTime(result.getTotalTime());
            rsp.setVisitOrder(result.getVisitOrder());
            rsp.setTransportMode(request.getTransportMode());
            rsp.setStrategy(request.getStrategy());

            // Build segments with order/from/to/per-segment path
            List<RouteRspVO.SegmentVO> multiSegments = new ArrayList<>();
            if (result.getSegments() != null) {
                int segOrder = 1;
                for (PathPlanningService.PathPlanningResult segResult : result.getSegments()) {
                    RouteRspVO.SegmentVO seg = new RouteRspVO.SegmentVO();
                    seg.setOrder(segOrder++);
                    seg.setType(segResult.getTransportMode());
                    seg.setDistance(segResult.getTotalDistance() != null ? segResult.getTotalDistance().doubleValue() : 0);
                    seg.setTime(segResult.getEstimatedTime());
                    seg.setNodeIds(segResult.getNodes() != null ?
                        segResult.getNodes().stream().map(PathPlanningService.PathNode::getNodeId).collect(Collectors.toList()) : new ArrayList<>());

                    // Set from/to using first and last nodes of each segment
                    if (segResult.getNodes() != null && !segResult.getNodes().isEmpty()) {
                        PathPlanningService.PathNode firstNode = segResult.getNodes().get(0);
                        PathPlanningService.PathNode lastNode = segResult.getNodes().get(segResult.getNodes().size() - 1);

                        RouteRspVO.EndpointInfoVO from = new RouteRspVO.EndpointInfoVO();
                        from.setNodeId(firstNode.getNodeId());
                        from.setName(firstNode.getName());
                        if (firstNode.getLatitude() != null && firstNode.getLongitude() != null) {
                            CoordinateTransformUtil.Gcj02Coord fgcj = CoordinateTransformUtil.wgs84ToGcj02(
                                firstNode.getLatitude().doubleValue(), firstNode.getLongitude().doubleValue());
                            from.setLatitude(fgcj.lat());
                            from.setLongitude(fgcj.lng());
                        }
                        seg.setFrom(from);

                        RouteRspVO.EndpointInfoVO to = new RouteRspVO.EndpointInfoVO();
                        to.setNodeId(lastNode.getNodeId());
                        to.setName(lastNode.getName());
                        if (lastNode.getLatitude() != null && lastNode.getLongitude() != null) {
                            CoordinateTransformUtil.Gcj02Coord tgcj = CoordinateTransformUtil.wgs84ToGcj02(
                                lastNode.getLatitude().doubleValue(), lastNode.getLongitude().doubleValue());
                            to.setLatitude(tgcj.lat());
                            to.setLongitude(tgcj.lng());
                        }
                        seg.setTo(to);
                    }

                    // Per-segment path points (GCJ-02)
                    List<RouteRspVO.PathPointVO> segPath = new ArrayList<>();
                    if (segResult.getNodes() != null) {
                        for (PathPlanningService.PathNode pn : segResult.getNodes()) {
                            if (pn.getLatitude() != null && pn.getLongitude() != null) {
                                CoordinateTransformUtil.Gcj02Coord gcj = CoordinateTransformUtil.wgs84ToGcj02(
                                    pn.getLatitude().doubleValue(), pn.getLongitude().doubleValue());
                                RouteRspVO.PathPointVO pt = new RouteRspVO.PathPointVO();
                                pt.setLat(gcj.lat());
                                pt.setLng(gcj.lng());
                                pt.setSegmentType(segResult.getTransportMode());
                                segPath.add(pt);
                            }
                        }
                    }
                    seg.setPath(segPath);
                    multiSegments.add(seg);
                }
            }
            rsp.setSegments(multiSegments);

            // Build path points from segments
            List<RouteRspVO.PathPointVO> multiPath = new ArrayList<>();
            if (result.getSegments() != null) {
                for (PathPlanningService.PathPlanningResult segResult : result.getSegments()) {
                    if (segResult.getNodes() != null) {
                        for (PathPlanningService.PathNode node : segResult.getNodes()) {
                            if (node.getLatitude() != null && node.getLongitude() != null) {
                                CoordinateTransformUtil.Gcj02Coord gcj = CoordinateTransformUtil.wgs84ToGcj02(
                                    node.getLatitude().doubleValue(), node.getLongitude().doubleValue());
                                RouteRspVO.PathPointVO pt = new RouteRspVO.PathPointVO();
                                pt.setLat(gcj.lat());
                                pt.setLng(gcj.lng());
                                pt.setSegmentType(segResult.getTransportMode());
                                multiPath.add(pt);
                            }
                        }
                    }
                }
            }
            rsp.setPath(multiPath);
            return Response.ok(rsp);
        } catch (Exception e) {
            log.error("多目标路线规划失败", e);
            return Response.error("多目标路线规划失败: " + e.getMessage());
        }
    }

    @GetMapping("/facilities/nearby")
    @Operation(summary = "获取附近设施", description = "根据节点位置或坐标查询附近的设施")
    public Response<NearbyRspVO> getNearbyFacilities(
            @Parameter(description = "景区ID", required = true) @RequestParam Long scenicAreaId,
            @Parameter(description = "节点ID") @RequestParam(required = false) Long nodeId,
            @Parameter(description = "纬度（GCJ-02）") @RequestParam(required = false) Double lat,
            @Parameter(description = "经度（GCJ-02）") @RequestParam(required = false) Double lng,
            @Parameter(description = "设施类型（逗号分隔）") @RequestParam(required = false) String facilityTypes,
            @Parameter(description = "搜索半径（米）") @RequestParam(required = false, defaultValue = "2000") Integer radius,
            @Parameter(description = "返回数量限制") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            if (nodeId == null && lat != null && lng != null) {
                nodeId = resolveInput("coordinate", null, null, null, null, lat, lng, "walk");
                if (nodeId == null) return Response.error("无法解析坐标到路网节点");
            }
            if (nodeId == null) {
                return Response.error("必须提供节点ID或坐标(lat/lng)");
            }
            Integer typeFilter = null;
            if (facilityTypes != null && !facilityTypes.isBlank()) {
                String[] parts = facilityTypes.split(",");
                if (parts.length > 0) {
                    try {
                        typeFilter = Integer.parseInt(parts[0].trim());
                    } catch (NumberFormatException e) {
                        log.warn("无效的设施类型: {}", parts[0]);
                    }
                }
            }
            log.info("查询附近设施: 景区={}, 节点={}, 设施类型={}, 半径={}米, 限制={}",
                     scenicAreaId, nodeId, facilityTypes, radius, limit);

            RoadNode currentNode = roadNodeMapper.selectById(nodeId);
            if (currentNode == null || currentNode.getLatitude() == null) {
                return Response.error("未找到当前节点");
            }
            BigDecimal currentLat = currentNode.getLatitude();
            BigDecimal currentLng = currentNode.getLongitude();

            FacilityListReqVO reqVO = new FacilityListReqVO();
            reqVO.setType(typeFilter);
            List<FacilityRspVO> facilities = scenicService.listFacilities(scenicAreaId, reqVO);

            if (facilities == null || facilities.isEmpty()) {
                log.info("景区{}没有找到设施", scenicAreaId);
                return Response.ok(new NearbyRspVO());
            }

            List<NearbyRspVO.NearbyFacilityItemVO> items = new ArrayList<>();
            for (FacilityRspVO facility : facilities) {
                Long facilityNodeId = pathPlanningService.getFacilityNodeId(facility.getId());
                if (facilityNodeId == null) {
                    log.debug("设施{}没有关联的路网节点，跳过", facility.getId());
                    continue;
                }

                BigDecimal distance = pathPlanningService.getDistanceBetweenNodes(nodeId, facilityNodeId);
                if (distance == null) {
                    distance = BigDecimal.valueOf(haversineDistance(
                        currentLat.doubleValue(), currentLng.doubleValue(),
                        facility.getLatitude().doubleValue(), facility.getLongitude().doubleValue()));
                    distance = distance.multiply(BigDecimal.valueOf(1.3));
                }

                double roadDist = distance.doubleValue();
                if (roadDist > radius) {
                    continue;
                }

                double straightDist = haversineDistance(
                    currentLat.doubleValue(), currentLng.doubleValue(),
                    facility.getLatitude().doubleValue(), facility.getLongitude().doubleValue());
                int walkTimeSec = (int) (roadDist / 1.2);

                CoordinateTransformUtil.Gcj02Coord gcj = CoordinateTransformUtil.wgs84ToGcj02(
                    facility.getLatitude().doubleValue(), facility.getLongitude().doubleValue());

                NearbyRspVO.NearbyFacilityItemVO item = new NearbyRspVO.NearbyFacilityItemVO();
                item.setFacilityId(facility.getId());
                item.setName(facility.getName());
                item.setFacilityType(facility.getType());
                item.setSubtype(facility.getSubtype());
                item.setLatitude(gcj.lat());
                item.setLongitude(gcj.lng());
                item.setRoadDistance(roadDist);
                item.setStraightDistance(straightDist);
                item.setWalkTime(walkTimeSec);
                item.setRating(facility.getRating() != null ? facility.getRating().doubleValue() : null);
                items.add(item);
            }

            items.sort((a, b) -> Double.compare(a.getRoadDistance(), b.getRoadDistance()));

            if (items.size() > limit) {
                items = items.subList(0, limit);
            }

            NearbyRspVO rsp = new NearbyRspVO();
            rsp.setSourceNodeId(nodeId);
            rsp.setFacilities(items);

            log.info("找到{}个附近设施", items.size());
            return Response.ok(rsp);

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
    public Response<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("nodeCount", roadNodeMapper.selectCount(null));
        result.put("edgeCount", roadEdgeMapper.selectCount(null));
        try {
            ScenicListReqVO reqVO = new ScenicListReqVO();
            reqVO.setPage(1);
            reqVO.setSize(1);
            ScenicListRspVO list = scenicService.listScenic(reqVO);
            result.put("scenicCount", list != null ? list.getTotal() : 0);
        } catch (Exception e) {
            result.put("scenicCount", -1);
        }
        result.put("spatialIndexReady", true);
        return Response.ok(result);
    }

    @GetMapping("/nearest-edge")
    @Operation(summary = "找最近道路边", description = "根据用户坐标找最近的可通行道路边，返回吸附点和置信度")
    public Response<NearestEdgeRspVO> nearestEdge(
            @Parameter(description = "纬度（GCJ-02）", required = true) @RequestParam Double lat,
            @Parameter(description = "经度（GCJ-02）", required = true) @RequestParam Double lng,
            @Parameter(description = "交通方式: walk/bike/shuttle") @RequestParam(defaultValue = "walk") String transportMode,
            @Parameter(description = "搜索半径（米）") @RequestParam(defaultValue = "1000") Integer radius) {
        try {
            log.info("查找最近边: lat={}, lng={}, transportMode={}, radius={}", lat, lng, transportMode, radius);

            CoordinateTransformUtil.Wgs84Coord wgs = CoordinateTransformUtil.gcj02ToWgs84(lat, lng);
            int mode = parseTransportMode(transportMode);

            double radiusDeg = radius / (111320.0 * Math.cos(Math.toRadians(wgs.lat())));
            List<SpatialRoadNodeMapper.NearestEdgeCandidate> candidates =
                    spatialRoadNodeMapper.findNearestEdges(wgs.lat(), wgs.lng(),
                        wgs.lat() - radiusDeg, wgs.lat() + radiusDeg,
                        wgs.lng() - radiusDeg, wgs.lng() + radiusDeg, 20);

            if (candidates == null || candidates.isEmpty()) {
                return Response.error("未找到附近的道路边");
            }

            SpatialRoadNodeMapper.NearestEdgeCandidate bestCandidate = null;
            double bestDistance = Double.MAX_VALUE;
            double bestSnapLat = 0, bestSnapLng = 0, bestSnapPos = 0;

            for (SpatialRoadNodeMapper.NearestEdgeCandidate c : candidates) {
                if (!isTransportModeCompatible(c.getTransportType(), mode)) {
                    continue;
                }
                double[] proj = computeEdgeProjection(
                        wgs.lat(), wgs.lng(),
                        c.getFromLat().doubleValue(), c.getFromLng().doubleValue(),
                        c.getToLat().doubleValue(), c.getToLng().doubleValue());
                double snapLat = proj[0];
                double snapLng = proj[1];
                double snapPos = proj[2];
                double dist = haversineDistance(wgs.lat(), wgs.lng(), snapLat, snapLng);

                if (dist < bestDistance) {
                    int candidateSccSize = computeConnectedComponentSize(c.getFromNodeId());
                    if (candidateSccSize < 100) {
                        continue;
                    }
                    bestDistance = dist;
                    bestCandidate = c;
                    bestSnapLat = snapLat;
                    bestSnapLng = snapLng;
                    bestSnapPos = snapPos;
                }
            }

            if (bestCandidate == null) {
                return Response.error("未找到支持该交通方式的道路边");
            }

            int sccSize = computeConnectedComponentSize(bestCandidate.getFromNodeId());

            String confidence;
            if (sccSize > 1000 && bestDistance < 50) {
                confidence = "high";
            } else if (sccSize > 100 && bestDistance < 200) {
                confidence = "medium";
            } else {
                confidence = "low";
            }

            NearestEdgeRspVO rspVO = new NearestEdgeRspVO();
            rspVO.setEdgeId(bestCandidate.getEdgeId());

            CoordinateTransformUtil.Gcj02Coord snapGcj = CoordinateTransformUtil.wgs84ToGcj02(bestSnapLat, bestSnapLng);
            rspVO.setSnapLat(snapGcj.lat());
            rspVO.setSnapLng(snapGcj.lng());
            rspVO.setSnapPosition(bestSnapPos);
            rspVO.setDistance(bestDistance);

            NearestEdgeRspVO.EdgeInfoVO edgeInfo = new NearestEdgeRspVO.EdgeInfoVO();
            edgeInfo.setFromNodeId(bestCandidate.getFromNodeId());
            edgeInfo.setToNodeId(bestCandidate.getToNodeId());
            edgeInfo.setHighwayType(bestCandidate.getHighwayType());
            edgeInfo.setName(bestCandidate.getEdgeName());
            edgeInfo.setTransportModes(transportTypeToList(bestCandidate.getTransportType()));
            rspVO.setEdgeInfo(edgeInfo);

            rspVO.setConnectedComponentSize(sccSize);
            rspVO.setConfidence(confidence);

            if (!"high".equals(confidence)) {
                List<NearestEdgeRspVO.AlternativeVO> alternatives = new ArrayList<>();
                RoadNode nearestNode = findNearestWalkableNode(
                        BigDecimal.valueOf(wgs.lat()), BigDecimal.valueOf(wgs.lng()), 500.0);
                if (nearestNode != null) {
                    NearestEdgeRspVO.AlternativeVO alt = new NearestEdgeRspVO.AlternativeVO();
                    alt.setType("nearest_walkable");
                    alt.setNodeId(nearestNode.getId());
                    double nodeDist = haversineDistance(wgs.lat(), wgs.lng(),
                            nearestNode.getLatitude().doubleValue(),
                            nearestNode.getLongitude().doubleValue());
                    alt.setDistance(nodeDist);
                    alternatives.add(alt);
                }
                rspVO.setAlternatives(alternatives);
            }

            return Response.ok(rspVO);
        } catch (Exception e) {
            log.error("查找最近边失败", e);
            return Response.error("查找最近边失败: " + e.getMessage());
        }
    }

    @GetMapping("/scenic/{scenicAreaId}/access-nodes")
    @Operation(summary = "获取景区接入节点", description = "返回景区在各交通方式下的路网接入节点")
    public Response<ScenicAccessRspVO> accessNodes(
            @Parameter(description = "景区ID", required = true) @PathVariable Long scenicAreaId,
            @Parameter(description = "最大距离限制（米）") @RequestParam(defaultValue = "1500") Integer maxDistance) {
        try {
            log.info("查询景区接入节点: scenicAreaId={}, maxDistance={}", scenicAreaId, maxDistance);

            ScenicItemRspVO scenic = scenicService.getScenicDetail(scenicAreaId);
            if (scenic == null) {
                return Response.error("景区不存在");
            }

            ScenicAccessRspVO rspVO = scenicAccessService.getAccessNodes(scenicAreaId, maxDistance);
            if (rspVO == null) {
                return Response.error("未找到景区接入节点");
            }

            rspVO.setScenicAreaName(scenic.getName());

            if (scenic.getLatitude() != null && scenic.getLongitude() != null) {
                CoordinateTransformUtil.Gcj02Coord centerGcj = CoordinateTransformUtil.wgs84ToGcj02(
                        scenic.getLatitude().doubleValue(), scenic.getLongitude().doubleValue());
                ScenicAccessRspVO.CoordinateVO center = new ScenicAccessRspVO.CoordinateVO();
                center.setLatitude(centerGcj.lat());
                center.setLongitude(centerGcj.lng());
                rspVO.setScenicCenter(center);
            }

            if (rspVO.getAccessNodes() != null && rspVO.getAccessNodes().getBike() != null) {
                List<AccessNodeVO> bikeNodes = rspVO.getAccessNodes().getBike();
                List<AccessNodeVO> walkNodes = rspVO.getAccessNodes().getWalk();
                List<AccessNodeVO> enrichedBike = new ArrayList<>();

                for (AccessNodeVO bikeNode : bikeNodes) {
                    enrichedBike.add(bikeNode);
                    if (bikeNode.getDistance() != null && bikeNode.getDistance() > 500
                            && walkNodes != null && !walkNodes.isEmpty()) {
                        AccessNodeVO nearestWalk = findNearestWalkAccessNode(bikeNode, walkNodes);
                        if (nearestWalk != null) {
                            AccessNodeVO viaWalkNode = new AccessNodeVO();
                            viaWalkNode.setNodeId(nearestWalk.getNodeId());
                            viaWalkNode.setOsmId(nearestWalk.getOsmId());
                            viaWalkNode.setName(nearestWalk.getName());
                            viaWalkNode.setNodeType(nearestWalk.getNodeType());
                            viaWalkNode.setLatitude(nearestWalk.getLatitude());
                            viaWalkNode.setLongitude(nearestWalk.getLongitude());
                            viaWalkNode.setDistance(nearestWalk.getDistance());
                            viaWalkNode.setIsPrimary(nearestWalk.getIsPrimary());
                             viaWalkNode.setViaWalk(true);
                             viaWalkNode.setConnectedComponentSize(nearestWalk.getConnectedComponentSize());
                             viaWalkNode.setWalkDistance(findNearestWalkAccessNodeDistance(bikeNode, nearestWalk));
                             enrichedBike.add(viaWalkNode);
                        }
                    }
                }
                rspVO.getAccessNodes().setBike(enrichedBike);
            }

            return Response.ok(rspVO);
        } catch (Exception e) {
            log.error("查询景区接入节点失败", e);
            return Response.error("查询景区接入节点失败: " + e.getMessage());
        }
    }

    @GetMapping("/scenic/{scenicAreaId}/poi-nodes")
    public Response<List<PoiNodeVO>> poiNodes(
            @PathVariable Long scenicAreaId,
            @RequestParam(required = false) String types,
            @RequestParam(defaultValue = "50") Integer limit) {
        try {
            log.info("查询景区POI: scenicAreaId={}, types={}, limit={}", scenicAreaId, types, limit);
            
            // Query road nodes marked as POI for this scenic area
            List<RoadNode> nodes = roadNodeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RoadNode>()
                    .eq(RoadNode::getDisplayForScenicId, scenicAreaId)
                    .eq(RoadNode::getNodeType, 2)
                    .eq(RoadNode::getDeleted, false)
                    .eq(RoadNode::getEnabled, true)
                    .orderByAsc(RoadNode::getId)
                    .last("LIMIT " + limit)
            );
            
            // Also include node_type=2 within scenic area if display_for_scenic_id not set
            if (nodes == null || nodes.isEmpty()) {
                nodes = roadNodeMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RoadNode>()
                        .eq(RoadNode::getScenicAreaId, scenicAreaId)
                        .eq(RoadNode::getNodeType, 2)
                        .eq(RoadNode::getDeleted, false)
                        .eq(RoadNode::getEnabled, true)
                        .orderByAsc(RoadNode::getId)
                        .last("LIMIT " + limit)
                );
            }
            
            Set<String> typeFilter = null;
            if (types != null && !types.isBlank()) {
                typeFilter = new HashSet<>();
                for (String t : types.split(",")) {
                    typeFilter.add(t.trim());
                }
            }
            
            List<PoiNodeVO> result = new ArrayList<>();
            if (nodes != null) {
                for (RoadNode node : nodes) {
                    String poiType = extractPoiType(node);
                    if (typeFilter != null && !typeFilter.contains(poiType)) continue;
                    
                    PoiNodeVO vo = new PoiNodeVO();
                    vo.setNodeId(node.getId());
                    vo.setName(node.getName());
                    vo.setNodeType(node.getNodeType());
                    vo.setPoiType(poiType);
                    vo.setDescription(null);
                    vo.setFacilityId(node.getFacilityId());
                    
                    if (node.getLatitude() != null && node.getLongitude() != null) {
                        CoordinateTransformUtil.Gcj02Coord gcj = CoordinateTransformUtil.wgs84ToGcj02(
                            node.getLatitude().doubleValue(), node.getLongitude().doubleValue());
                        vo.setLatitude(gcj.lat());
                        vo.setLongitude(gcj.lng());
                    }
                    result.add(vo);
                }
            }
            
            return Response.ok(result);
        } catch (Exception e) {
            log.error("查询景区POI失败", e);
            return Response.error("查询景区POI失败: " + e.getMessage());
        }
    }

    private String extractPoiType(RoadNode node) {
        if (node.getOsmTags() == null) return "unknown";
        String tags = node.getOsmTags();
        // Extract amenity, shop, tourism, leisure from osm_tags JSON
        String[] keys = {"amenity", "shop", "tourism", "leisure"};
        for (String key : keys) {
            String searchKey = "\"" + key + "\":";
            int idx = tags.indexOf(searchKey);
            if (idx >= 0) {
                int start = idx + searchKey.length();
                while (start < tags.length() && (tags.charAt(start) == ' ' || tags.charAt(start) == '"')) start++;
                int end = start;
                while (end < tags.length() && tags.charAt(end) != '"' && tags.charAt(end) != ',' && tags.charAt(end) != '}') end++;
                return tags.substring(start, end).trim();
            }
        }
        return "unknown";
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

    /**
     * 统一解析导航输入: scenicAreaId/nodeId/edgeId/坐标 → 路网节点ID
     */
    private Long resolveInput(String type, Long scenicAreaId, Long nodeId,
            Long edgeId, Double snapPos, Double lat, Double lng, String transportMode) {
        if (type == null) return null;
        switch (type) {
            case "node":
                return nodeId;
            case "scenicArea": {
                int accessType = transportModeToAccessType(transportMode);
                List<ScenicAccess> accesses = scenicAccessMapper.selectByScenicAndType(scenicAreaId, accessType);
                if (accesses != null && !accesses.isEmpty()) {
                    return accesses.stream()
                        .min(java.util.Comparator.comparingInt(ScenicAccess::getRankOrder))
                        .map(ScenicAccess::getRoadNodeId).orElse(null);
                }
                org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicItemRspVO scenic = scenicService.getScenicDetail(scenicAreaId);
                if (scenic != null && scenic.getLatitude() != null) {
                    return resolveFromCoord(scenic.getLatitude().doubleValue(), scenic.getLongitude().doubleValue(), transportMode, true);
                }
                return null;
            }
            case "edge": {
                RoadEdge edge = roadEdgeMapper.selectById(edgeId);
                if (edge == null) return null;
                return (snapPos != null && snapPos < 0.5) ? edge.getFromNodeId() : edge.getToNodeId();
            }
            case "coordinate":
                return resolveFromCoord(lat, lng, transportMode, false);
            default:
                return null;
        }
    }

    private Long resolveFromCoord(Double lat, Double lng, String transportMode, boolean inputIsWgs84) {
        if (lat == null || lng == null) return null;
        CoordinateTransformUtil.Wgs84Coord wgs = inputIsWgs84
            ? new CoordinateTransformUtil.Wgs84Coord(lat, lng)
            : CoordinateTransformUtil.gcj02ToWgs84(lat, lng);
        double radiusDeg = 2000.0 / (111320.0 * Math.cos(Math.toRadians(wgs.lat())));
        List<SpatialRoadNodeMapper.NearestEdgeCandidate> candidates =
            spatialRoadNodeMapper.findNearestEdges(wgs.lat(), wgs.lng(),
                wgs.lat() - radiusDeg, wgs.lat() + radiusDeg,
                wgs.lng() - radiusDeg, wgs.lng() + radiusDeg, 5);
        if (candidates == null || candidates.isEmpty()) return null;
        int mode = parseTransportMode(transportMode);
        for (SpatialRoadNodeMapper.NearestEdgeCandidate c : candidates) {
            if (isTransportModeCompatible(c.getTransportType(), mode)) {
                return c.getFromNodeId();
            }
        }
        RoadNode nearest = findNearestWalkableNode(
            java.math.BigDecimal.valueOf(wgs.lat()), java.math.BigDecimal.valueOf(wgs.lng()), 2000.0);
        return nearest != null ? nearest.getId() : null;
    }

    private int transportModeToAccessType(String transportMode) {
        return switch (transportMode != null ? transportMode.toLowerCase() : "walk") {
            case "bike" -> 2;
            case "shuttle" -> 3;
            default -> 1;
        };
    }

    private RouteRspVO buildRouteRspVO(PathPlanningService.PathPlanningResult result,
            String startType, String endType, RouteRequest request, Long startNodeId, Long endNodeId) {
        RouteRspVO rsp = new RouteRspVO();
        rsp.setRouteId(result.getRouteId());
        rsp.setTotalDistance(result.getTotalDistance() != null ? result.getTotalDistance().doubleValue() : 0);
        rsp.setEstimatedTime(result.getEstimatedTime());
        rsp.setTransportMode(request.getTransportMode());
        rsp.setStrategy(request.getStrategy());

        List<RouteRspVO.SegmentVO> segments = new ArrayList<>();
        if (result.getNodes() != null) {
            RouteRspVO.SegmentVO seg = new RouteRspVO.SegmentVO();
            seg.setType(request.getTransportMode());
            seg.setDistance(rsp.getTotalDistance());
            seg.setTime(result.getEstimatedTime());
            seg.setNodeIds(result.getNodes().stream().map(PathPlanningService.PathNode::getNodeId).collect(Collectors.toList()));
            segments.add(seg);
        }
        rsp.setSegments(segments);

        rsp.setStartInfo(buildEndpointInfo(startType, request.getStartScenicAreaId(), startNodeId, request.getTransportMode()));
        rsp.setEndInfo(buildEndpointInfo(endType, request.getEndScenicAreaId(), endNodeId, request.getTransportMode()));

        List<RouteRspVO.PathPointVO> path = new ArrayList<>();
        if (result.getNodes() != null) {
            for (PathPlanningService.PathNode node : result.getNodes()) {
                if (node.getLatitude() != null && node.getLongitude() != null) {
                    CoordinateTransformUtil.Gcj02Coord gcj = CoordinateTransformUtil.wgs84ToGcj02(
                        node.getLatitude().doubleValue(), node.getLongitude().doubleValue());
                    RouteRspVO.PathPointVO pt = new RouteRspVO.PathPointVO();
                    pt.setLat(gcj.lat());
                    pt.setLng(gcj.lng());
                    pt.setSegmentType(request.getTransportMode());
                    path.add(pt);
                }
            }
        }
        rsp.setPath(path);
        return rsp;
    }

    private RouteRspVO.EndpointInfoVO buildEndpointInfo(String type, Long scenicAreaId, Long resolvedNodeId, String transportMode) {
        RouteRspVO.EndpointInfoVO info = new RouteRspVO.EndpointInfoVO();
        info.setInputType(type);
        info.setResolvedNodeId(resolvedNodeId);
        if (scenicAreaId != null) {
            info.setScenicAreaId(scenicAreaId);
            try {
                ScenicItemRspVO scenic = scenicService.getScenicDetail(scenicAreaId);
                if (scenic != null) {
                    info.setScenicAreaName(scenic.getName());
                }
            } catch (Exception ignored) {}
        }
        // Set lat/lng from resolved node (GCJ-02)
        if (resolvedNodeId != null) {
            RoadNode node = roadNodeMapper.selectById(resolvedNodeId);
            if (node != null && node.getLatitude() != null) {
                CoordinateTransformUtil.Gcj02Coord gcj = CoordinateTransformUtil.wgs84ToGcj02(
                    node.getLatitude().doubleValue(), node.getLongitude().doubleValue());
                info.setLatitude(gcj.lat());
                info.setLongitude(gcj.lng());
            }
        }
        return info;
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

    private boolean isTransportModeCompatible(Integer edgeTransportType, int mode) {
        if (edgeTransportType == null) return false;
        if (mode == 1) return edgeTransportType == 1 || edgeTransportType == 4 || edgeTransportType == 5;
        if (mode == 2) return edgeTransportType == 2 || edgeTransportType == 4 || edgeTransportType == 5;
        if (mode == 3) return edgeTransportType == 3 || edgeTransportType == 5;
        return false;
    }

    private List<String> transportTypeToList(Integer transportType) {
        if (transportType == null) return Collections.emptyList();
        switch (transportType) {
            case 1: return Arrays.asList("walk");
            case 2: return Arrays.asList("bike");
            case 3: return Arrays.asList("shuttle");
            case 4: return Arrays.asList("walk", "bike");
            case 5: return Arrays.asList("walk", "bike", "shuttle");
            default: return Collections.emptyList();
        }
    }

    private static final double EARTH_RADIUS = 6371000.0;

    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private double[] computeEdgeProjection(double qlat, double qlng,
                                            double lat1, double lng1,
                                            double lat2, double lng2) {
        double avgLat = Math.toRadians((lat1 + lat2) / 2.0);
        double meterPerDegLat = 111320.0;
        double meterPerDegLng = 111320.0 * Math.cos(avgLat);

        double dx = (lng2 - lng1) * meterPerDegLng;
        double dy = (lat2 - lat1) * meterPerDegLat;
        double edgeLenSq = dx * dx + dy * dy;

        double snapLat, snapLng, snapPos;
        if (edgeLenSq < 1e-6) {
            snapLat = lat1;
            snapLng = lng1;
            snapPos = 0;
        } else {
            double px = (qlng - lng1) * meterPerDegLng;
            double py = (qlat - lat1) * meterPerDegLat;
            double t = (px * dx + py * dy) / edgeLenSq;
            if (t < 0) t = 0;
            if (t > 1) t = 1;
            snapLat = lat1 + t * (lat2 - lat1);
            snapLng = lng1 + t * (lng2 - lng1);
            snapPos = t;
        }
        return new double[]{snapLat, snapLng, snapPos};
    }

    private int computeConnectedComponentSize(Long startNodeId) {
        Set<Long> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        Map<Long, List<RoadEdge>> adjacency = new HashMap<>();
        visited.add(startNodeId);
        queue.add(startNodeId);
        
        while (!queue.isEmpty() && visited.size() < 10000) {
            Long nodeId = queue.poll();
            // Batch load edges for this node
            List<RoadEdge> outgoing = roadEdgeMapper.selectOutgoingEdges(nodeId);
            List<RoadEdge> incoming = roadEdgeMapper.selectIncomingEdges(nodeId);
            // Build adjacency on-the-fly
            List<RoadEdge> allEdges = new ArrayList<>();
            if (outgoing != null) allEdges.addAll(outgoing);
            if (incoming != null) allEdges.addAll(incoming);
            adjacency.putIfAbsent(nodeId, allEdges);
            
            for (RoadEdge e : allEdges) {
                Long neighbor = e.getFromNodeId().equals(nodeId) ? e.getToNodeId() : e.getFromNodeId();
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return visited.size();
    }

    private RoadNode findNearestWalkableNode(BigDecimal lat, BigDecimal lng, Double radius) {
        List<RoadNode> nodes = roadNodeMapper.selectNearbyNodes(lat, lng, radius);
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        RoadNode best = null;
        double bestDist = Double.MAX_VALUE;
        double dlat = lat.doubleValue();
        double dlng = lng.doubleValue();
        for (RoadNode node : nodes) {
            double dist = haversineDistance(dlat, dlng,
                    node.getLatitude().doubleValue(), node.getLongitude().doubleValue());
            if (dist < bestDist) {
                bestDist = dist;
                best = node;
            }
        }
        return best;
    }

    private AccessNodeVO findNearestWalkAccessNode(AccessNodeVO bikeNode, List<AccessNodeVO> walkNodes) {
        if (bikeNode.getLatitude() == null || bikeNode.getLongitude() == null) {
            return walkNodes.isEmpty() ? null : walkNodes.get(0);
        }
        AccessNodeVO best = null;
        double bestDist = Double.MAX_VALUE;
        for (AccessNodeVO w : walkNodes) {
            if (w.getLatitude() == null || w.getLongitude() == null) continue;
            double dist = haversineDistance(
                    bikeNode.getLatitude(), bikeNode.getLongitude(),
                    w.getLatitude(), w.getLongitude());
            if (dist < bestDist) {
                bestDist = dist;
                best = w;
            }
        }
        return best;
    }

    private double findNearestWalkAccessNodeDistance(AccessNodeVO bikeNode, AccessNodeVO walkNode) {
        return haversineDistance(
            bikeNode.getLatitude(), bikeNode.getLongitude(),
            walkNode.getLatitude(), walkNode.getLongitude());
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
