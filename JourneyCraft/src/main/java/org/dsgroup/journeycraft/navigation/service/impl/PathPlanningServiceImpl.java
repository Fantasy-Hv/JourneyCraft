package org.dsgroup.journeycraft.navigation.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RoadEdge;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.service.PathPlanningService;
import org.dsgroup.journeycraft.navigation.service.RoadEdgeService;
import org.dsgroup.journeycraft.navigation.service.RoadNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 路径规划算法服务实现
 * <p>
 * 实现Dijkstra单目标路径规划和TSP变种多目标路线规划
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class PathPlanningServiceImpl implements PathPlanningService {

    @Autowired
    private RoadNodeService roadNodeService;
    
    @Autowired
    private RoadEdgeService roadEdgeService;

    @Override
    public PathPlanningResult calculateShortestPath(Long startNodeId, Long endNodeId,
                                                      Integer transportMode, String strategy) {
        log.info("计算最短路径: 起点={}, 终点={}, 交通方式={}, 策略={}", 
                 startNodeId, endNodeId, transportMode, strategy);
        
        // 验证起点和终点
        RoadNode startNode = roadNodeService.getById(startNodeId);
        RoadNode endNode = roadNodeService.getById(endNodeId);
        if (startNode == null || endNode == null) {
            log.warn("起点或终点不存在");
            return null;
        }
        
        // Dijkstra算法实现
        List<PathNode> pathNodes = dijkstra(startNodeId, endNodeId, transportMode, strategy);
        if (pathNodes == null || pathNodes.isEmpty()) {
            log.warn("未找到可行路径");
            return null;
        }
        
        // 计算总距离和总时间
        BigDecimal totalDistance = BigDecimal.ZERO;
        int totalTime = 0;
        for (int i = 0; i < pathNodes.size() - 1; i++) {
            Long fromId = pathNodes.get(i).getNodeId();
            Long toId = pathNodes.get(i + 1).getNodeId();
            BigDecimal dist = getDistanceBetweenNodes(fromId, toId);
            if (dist != null) {
                totalDistance = totalDistance.add(dist);
            }
            Integer time = getTimeBetweenNodes(fromId, toId, transportMode);
            if (time != null) {
                totalTime += time;
            }
        }
        
        PathPlanningResult result = new PathPlanningResult();
        result.setTotalDistance(totalDistance);
        result.setEstimatedTime(totalTime);
        result.setTransportMode(getTransportModeName(transportMode));
        result.setStrategy(strategy != null ? strategy : "shortest_distance");
        result.setNodes(pathNodes);
        
        return result;
    }

    @Override
    public MultiTargetRouteResult calculateMultiTargetRoute(Long startNodeId, List<Long> endNodeIds,
                                                             Integer transportMode, boolean needReturn) {
        log.info("计算多目标路线: 起点={}, 目标数={}, 返回={}", 
                 startNodeId, endNodeIds != null ? endNodeIds.size() : 0, needReturn);
        
        if (endNodeIds == null || endNodeIds.isEmpty()) {
            return null;
        }
        
        MultiTargetRouteResult result = new MultiTargetRouteResult();
        List<PathPlanningResult> segments = new ArrayList<>();
        List<Long> visitOrder = new ArrayList<>();
        BigDecimal totalDistance = BigDecimal.ZERO;
        int totalTime = 0;
        
        // 按最近邻算法求解TSP变种
        Long currentNode = startNodeId;
        List<Long> remainingTargets = new ArrayList<>(endNodeIds);
        
        while (!remainingTargets.isEmpty()) {
            Long nearestNode = findNearestNode(currentNode, remainingTargets, transportMode);
            if (nearestNode == null) {
                break;
            }
            
            PathPlanningResult segment = calculateShortestPath(currentNode, nearestNode, transportMode, null);
            if (segment != null) {
                segments.add(segment);
                totalDistance = totalDistance.add(segment.getTotalDistance());
                totalTime += segment.getEstimatedTime();
                visitOrder.add(nearestNode);
            }
            
            currentNode = nearestNode;
            remainingTargets.remove(nearestNode);
        }
        
        // 如果需要返回起点
        if (needReturn && !visitOrder.isEmpty()) {
            PathPlanningResult returnSegment = calculateShortestPath(currentNode, startNodeId, transportMode, null);
            if (returnSegment != null) {
                segments.add(returnSegment);
                totalDistance = totalDistance.add(returnSegment.getTotalDistance());
                totalTime += returnSegment.getEstimatedTime();
            }
            result.setReturnedToStart(true);
        } else {
            result.setReturnedToStart(false);
        }
        
        result.setTotalDistance(totalDistance);
        result.setTotalTime(totalTime);
        result.setVisitOrder(visitOrder);
        result.setSegments(segments);
        
        return result;
    }

    @Override
    public BigDecimal getDistanceBetweenNodes(Long fromNodeId, Long toNodeId) {
        RoadEdge edge = roadEdgeService.lambdaQuery()
                .eq(RoadEdge::getFromNodeId, fromNodeId)
                .eq(RoadEdge::getToNodeId, toNodeId)
                .one();
        if (edge == null) {
            // 尝试反向
            edge = roadEdgeService.lambdaQuery()
                    .eq(RoadEdge::getFromNodeId, toNodeId)
                    .eq(RoadEdge::getToNodeId, fromNodeId)
                    .one();
        }
        return edge != null ? edge.getDistance() : null;
    }

    @Override
    public Integer getTimeBetweenNodes(Long fromNodeId, Long toNodeId, Integer transportMode) {
        RoadEdge edge = roadEdgeService.lambdaQuery()
                .eq(RoadEdge::getFromNodeId, fromNodeId)
                .eq(RoadEdge::getToNodeId, toNodeId)
                .one();
        if (edge == null) {
            edge = roadEdgeService.lambdaQuery()
                    .eq(RoadEdge::getFromNodeId, toNodeId)
                    .eq(RoadEdge::getToNodeId, fromNodeId)
                    .one();
        }
        if (edge == null) {
            return null;
        }
        
        return switch (transportMode != null ? transportMode : 1) {
            case 1 -> edge.getWalkTime();       // 步行
            case 2 -> edge.getBikeTime();       // 骑行
            case 3 -> edge.getShuttleTime();   // 驾驶
            default -> edge.getWalkTime();
        };
    }

    /**
     * Dijkstra算法实现
     */
    private List<PathNode> dijkstra(Long startNodeId, Long endNodeId, Integer transportMode, String strategy) {
        Map<Long, Long> predecessor = new HashMap<>();
        Map<Long, BigDecimal> distance = new HashMap<>();
        Set<Long> settled = new HashSet<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparing(nd -> nd.distance));
        
        // 初始化
        distance.put(startNodeId, BigDecimal.ZERO);
        pq.add(new NodeDistance(startNodeId, BigDecimal.ZERO));
        
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Long currentNodeId = current.nodeId;
            
            if (settled.contains(currentNodeId)) {
                continue;
            }
            settled.add(currentNodeId);
            
            if (currentNodeId.equals(endNodeId)) {
                break;
            }
            
            // 获取相邻节点
            List<RoadEdge> edges = roadEdgeService.lambdaQuery()
                    .eq(RoadEdge::getFromNodeId, currentNodeId)
                    .list();
            
            for (RoadEdge edge : edges) {
                Long neighborId = edge.getToNodeId();
                if (settled.contains(neighborId)) {
                    continue;
                }
                
                // 检查是否可通行
                if (!isTransportable(edge, transportMode)) {
                    continue;
                }
                
                BigDecimal edgeDistance = edge.getDistance();
                BigDecimal newDist = distance.get(currentNodeId).add(edgeDistance);
                
                if (newDist.compareTo(distance.getOrDefault(neighborId, BigDecimal.valueOf(Long.MAX_VALUE))) < 0) {
                    distance.put(neighborId, newDist);
                    predecessor.put(neighborId, currentNodeId);
                    pq.add(new NodeDistance(neighborId, newDist));
                }
            }
        }
        
        // 重建路径
        if (!predecessor.containsKey(endNodeId) && !endNodeId.equals(startNodeId)) {
            log.warn("无法找到从 {} 到 {} 的路径", startNodeId, endNodeId);
            return null;
        }
        
        List<Long> nodeIds = new ArrayList<>();
        Long current = endNodeId;
        while (current != null) {
            nodeIds.add(current);
            current = predecessor.get(current);
        }
        Collections.reverse(nodeIds);
        
        // 转换为PathNode列表
        List<PathNode> pathNodes = new ArrayList<>();
        int sequence = 0;
        for (Long nodeId : nodeIds) {
            RoadNode node = roadNodeService.getById(nodeId);
            PathNode pn = new PathNode();
            pn.setNodeId(nodeId);
            pn.setSequence(sequence++);
            if (node != null) {
                pn.setName(node.getName());
                pn.setLatitude(node.getLatitude());
                pn.setLongitude(node.getLongitude());
            }
            // 设置动作
            if (nodeId.equals(startNodeId)) {
                pn.setAction("start");
            } else if (nodeId.equals(endNodeId)) {
                pn.setAction("end");
            } else {
                pn.setAction("visit");
            }
            pathNodes.add(pn);
        }
        
        return pathNodes;
    }

    /**
     * 找到最近的节点（使用Dijkstra计算最短距离）
     */
    private Long findNearestNode(Long fromNodeId, List<Long> targetNodes, Integer transportMode) {
        Long nearest = null;
        BigDecimal minDistance = BigDecimal.valueOf(Long.MAX_VALUE);
        
        for (Long targetId : targetNodes) {
            // 使用Dijkstra计算实际最短距离，而不是只查直接边
            List<PathNode> path = dijkstra(fromNodeId, targetId, transportMode, null);
            if (path != null && !path.isEmpty()) {
                BigDecimal dist = calculatePathDistance(path);
                if (dist != null && dist.compareTo(minDistance) < 0) {
                    minDistance = dist;
                    nearest = targetId;
                }
            }
        }
        
        return nearest;
    }

    /**
     * 计算路径的总距离
     */
    private BigDecimal calculatePathDistance(List<PathNode> path) {
        if (path == null || path.size() < 2) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < path.size() - 1; i++) {
            BigDecimal dist = getDistanceBetweenNodes(path.get(i).getNodeId(), path.get(i + 1).getNodeId());
            if (dist != null) {
                total = total.add(dist);
            }
        }
        return total;
    }

    /**
     * 检查边是否可通过指定交通方式通行
     */
    private boolean isTransportable(RoadEdge edge, Integer transportMode) {
        if (edge == null) {
            return false;
        }
        
        Integer transportType = edge.getTransportType();
        if (transportType == null || transportType == 0 || transportType == 5) {
            // 0=全部, 5=全部通行
            return true;
        }
        
        return switch (transportMode != null ? transportMode : 1) {
            case 1 -> transportType == 1 || transportType == 4 || transportType == 5; // 步行
            case 2 -> transportType == 2 || transportType == 4 || transportType == 5; // 骑行
            case 3 -> transportType == 3 || transportType == 5; // 驾驶
            default -> true;
        };
    }

    /**
     * 获取交通方式名称
     */
    private String getTransportModeName(Integer transportMode) {
        return switch (transportMode != null ? transportMode : 1) {
            case 1 -> "walk";
            case 2 -> "bike";
            case 3 -> "shuttle";
            default -> "walk";
        };
    }

    /**
     * 节点距离对
     */
    private static class NodeDistance {
        Long nodeId;
        BigDecimal distance;
        
        NodeDistance(Long nodeId, BigDecimal distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
}
