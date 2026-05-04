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
    public PathPlanningResult calculateAStarPath(Long startNodeId, Long endNodeId,
                                                   Integer transportMode, String strategy) {
        log.info("A*路径规划: 起点={}, 终点={}, 交通方式={}, 策略={}", 
                 startNodeId, endNodeId, transportMode, strategy);
        
        // 验证起点和终点
        RoadNode startNode = roadNodeService.getById(startNodeId);
        RoadNode endNode = roadNodeService.getById(endNodeId);
        if (startNode == null || endNode == null) {
            log.warn("起点或终点不存在");
            return null;
        }
        
        // A* 算法实现（Haversine 直线距离作启发函数）
        List<PathNode> pathNodes = aStar(startNodeId, endNodeId, transportMode, strategy);
        if (pathNodes == null || pathNodes.isEmpty()) {
            log.warn("A*未找到可行路径");
            return null;
        }
        
        // 计算总距离和总时间（使用实际距离，非启发式权重）
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
        
        // 多目标路线默认使用 shortest_distance 策略逐段计算
        String strategy = "shortest_distance";
        
        MultiTargetRouteResult result = new MultiTargetRouteResult();
        List<PathPlanningResult> segments = new ArrayList<>();
        List<Long> visitOrder = new ArrayList<>();
        BigDecimal totalDistance = BigDecimal.ZERO;
        int totalTime = 0;
        
        // 按最近邻算法求解TSP变种
        Long currentNode = startNodeId;
        List<Long> remainingTargets = new ArrayList<>(endNodeIds);
        
        while (!remainingTargets.isEmpty()) {
            Long nearestNode = findNearestNode(currentNode, remainingTargets, transportMode, strategy);
            if (nearestNode == null) {
                break;
            }
            
            PathPlanningResult segment = calculateShortestPath(currentNode, nearestNode, transportMode, strategy);
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
            PathPlanningResult returnSegment = calculateShortestPath(currentNode, startNodeId, transportMode, strategy);
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

    @Override
    public Long getFacilityNodeId(Long facilityId) {
        if (facilityId == null) {
            return null;
        }
        RoadNode node = roadNodeService.lambdaQuery()
                .eq(RoadNode::getFacilityId, facilityId)
                .one();
        return node != null ? node.getId() : null;
    }

    @Override
    public Long findNearestNodeByCoords(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null) {
            return null;
        }
        
        // 查询所有启用的节点
        List<RoadNode> allNodes = roadNodeService.lambdaQuery()
                .eq(RoadNode::getEnabled, true)
                .list();
        
        if (allNodes == null || allNodes.isEmpty()) {
            return null;
        }
        
        Long nearestId = null;
        double minDist = Double.MAX_VALUE;
        
        for (RoadNode node : allNodes) {
            if (node.getLatitude() == null || node.getLongitude() == null) {
                continue;
            }
            double d = haversineMeters(
                lat.doubleValue(), lng.doubleValue(),
                node.getLatitude().doubleValue(), node.getLongitude().doubleValue()
            );
            if (d < minDist) {
                minDist = d;
                nearestId = node.getId();
            }
        }
        
        return nearestId;
    }

    /**
     * Dijkstra算法实现
     * <p>
     * 根据策略使用不同的边权：
     * <ul>
     *   <li>shortest_distance — 以几何距离为边权</li>
     *   <li>shortest_time — 以通行时间为边权（根据交通方式取 walk_time/bike_time/shuttle_time）</li>
     *   <li>avoid_crowd — 以距离 × (1 + 拥挤度) 为边权，引导绕开拥挤路段</li>
     * </ul>
     */
    private List<PathNode> dijkstra(Long startNodeId, Long endNodeId, Integer transportMode, String strategy) {
        Map<Long, Long> predecessor = new HashMap<>();
        Map<Long, BigDecimal> weight = new HashMap<>();
        Set<Long> settled = new HashSet<>();
        PriorityQueue<NodeWeight> pq = new PriorityQueue<>(Comparator.comparing(nw -> nw.weight));
        
        // 初始化
        weight.put(startNodeId, BigDecimal.ZERO);
        pq.add(new NodeWeight(startNodeId, BigDecimal.ZERO));
        
        while (!pq.isEmpty()) {
            NodeWeight current = pq.poll();
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
                
                // ★ 根据策略计算边权
                BigDecimal edgeWeight = getEdgeWeight(edge, strategy, transportMode);
                BigDecimal newWeight = weight.get(currentNodeId).add(edgeWeight);
                
                BigDecimal defaultMax = BigDecimal.valueOf(Double.MAX_VALUE);
                if (newWeight.compareTo(weight.getOrDefault(neighborId, defaultMax)) < 0) {
                    weight.put(neighborId, newWeight);
                    predecessor.put(neighborId, currentNodeId);
                    pq.add(new NodeWeight(neighborId, newWeight));
                }
            }
        }
        
        // 重建路径
        if (!predecessor.containsKey(endNodeId) && !endNodeId.equals(startNodeId)) {
            log.warn("无法找到从 {} 到 {} 的路径", startNodeId, endNodeId);
            return null;
        }
        
        return rebuildPath(startNodeId, endNodeId, predecessor);
    }

    /**
     * A* 路径规划算法
     * <p>
     * 与 Dijkstra 的唯一区别：优先队列排序键为 f(n) = g(n) + h(n)，
     * 其中 g(n) 为从起点到节点 n 的实际代价（边权），h(n) 为节点 n 到终点的
     * Haversine 直线距离启发式估计。
     * <p>
     * 启发式 h(n) 具有可采纳性（admissible）——直线距离 ≤ 实际路径距离，
     * 因此 A* 保证找到最优解，且通常比 Dijkstra 探索更少节点。
     *
     * @see #dijkstra(Long, Long, Integer, String)
     */
    private List<PathNode> aStar(Long startNodeId, Long endNodeId, Integer transportMode, String strategy) {
        // 获取终点坐标用于启发式计算
        RoadNode endNode = roadNodeService.getById(endNodeId);
        if (endNode == null) {
            return null;
        }
        
        Map<Long, Long> predecessor = new HashMap<>();
        Map<Long, BigDecimal> gScore = new HashMap<>();  // 实际代价 g(n)
        Set<Long> settled = new HashSet<>();
        
        // 优先队列按 f(n) = g(n) + h(n) 排序
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(
            Comparator.comparing(an -> an.fScore));
        
        gScore.put(startNodeId, BigDecimal.ZERO);
        BigDecimal hStart = calculateHaversine(startNodeId, endNode);
        pq.add(new AStarNode(startNodeId, BigDecimal.ZERO, hStart));
        
        while (!pq.isEmpty()) {
            AStarNode current = pq.poll();
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
                
                if (!isTransportable(edge, transportMode)) {
                    continue;
                }
                
                // g 值：实际代价（边权）
                BigDecimal edgeWeight = getEdgeWeight(edge, strategy, transportMode);
                BigDecimal tentativeG = gScore.get(currentNodeId).add(edgeWeight);
                
                BigDecimal defaultMax = BigDecimal.valueOf(Double.MAX_VALUE);
                if (tentativeG.compareTo(gScore.getOrDefault(neighborId, defaultMax)) < 0) {
                    gScore.put(neighborId, tentativeG);
                    predecessor.put(neighborId, currentNodeId);
                    // h 值：启发式估计（Haversine 直线距离）
                    BigDecimal hValue = calculateHaversine(neighborId, endNode);
                    pq.add(new AStarNode(neighborId, tentativeG, hValue));
                }
            }
        }
        
        // 重建路径（与 Dijkstra 相同）
        if (!predecessor.containsKey(endNodeId) && !endNodeId.equals(startNodeId)) {
            log.warn("A*无法找到从 {} 到 {} 的路径", startNodeId, endNodeId);
            return null;
        }
        
        return rebuildPath(startNodeId, endNodeId, predecessor);
    }

    /**
     * 计算 Haversine 直线距离（米），用作 A* 启发式函数
     * <p>
     * 公式: a = sin²(Δlat/2) + cos(lat1)·cos(lat2)·sin²(Δlon/2)
     *       c = 2·atan2(√a, √(1-a))
     *       d = R·c  (R = 6371000m 地球半径)
     *
     * @param nodeId 当前节点ID
     * @param targetNode 目标节点（已含坐标）
     * @return 直线距离（米）
     */
    private BigDecimal calculateHaversine(Long nodeId, RoadNode targetNode) {
        RoadNode node = roadNodeService.getById(nodeId);
        if (node == null || node.getLatitude() == null || node.getLongitude() == null
                || targetNode.getLatitude() == null || targetNode.getLongitude() == null) {
            return BigDecimal.ZERO;
        }
        
        double d = haversineMeters(
            node.getLatitude().doubleValue(), node.getLongitude().doubleValue(),
            targetNode.getLatitude().doubleValue(), targetNode.getLongitude().doubleValue()
        );
        return BigDecimal.valueOf(d);
    }

    /**
     * 纯数学 Haversine 公式，不涉及数据库查询。
     *
     * @param lat1 点1纬度
     * @param lon1 点1经度
     * @param lat2 点2纬度
     * @param lon2 点2经度
     * @return 直线距离（米）
     */
    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double dlat = Math.toRadians(lat2 - lat1);
        double dlon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371000.0 * c;
    }

    /**
     * 从 predecessor 映射重建路径节点列表
     */
    private List<PathNode> rebuildPath(Long startNodeId, Long endNodeId, Map<Long, Long> predecessor) {
        List<Long> nodeIds = new ArrayList<>();
        Long current = endNodeId;
        while (current != null) {
            nodeIds.add(current);
            current = predecessor.get(current);
        }
        Collections.reverse(nodeIds);
        
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
    private Long findNearestNode(Long fromNodeId, List<Long> targetNodes, Integer transportMode, String strategy) {
        Long nearest = null;
        BigDecimal minDistance = BigDecimal.valueOf(Double.MAX_VALUE);
        
        for (Long targetId : targetNodes) {
            // 使用Dijkstra计算实际最短距离，而不是只查直接边
            List<PathNode> path = dijkstra(fromNodeId, targetId, transportMode, strategy);
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
     * 根据策略和交通方式计算边的权重（Dijkstra/A* 的边权）
     * <p>
     * 三种策略的边权公式：
     * <ul>
     *   <li><b>shortest_distance</b>: 直接使用几何距离（米）</li>
     *   <li><b>shortest_time</b>: 使用通行时间（秒），根据交通方式取对应时间字段</li>
     *   <li><b>avoid_crowd</b>: 距离 × (1 + 实时拥挤度)，拥挤度越高权重越大，算法自动绕行</li>
     * </ul>
     *
     * @param edge 路径段
     * @param strategy 规划策略 (shortest_distance / shortest_time / avoid_crowd)
     * @param transportMode 交通方式 (1=步行, 2=骑行, 3=驾驶)
     * @return 边权值
     */
    private BigDecimal getEdgeWeight(RoadEdge edge, String strategy, Integer transportMode) {
        if (edge == null) {
            return BigDecimal.valueOf(Double.MAX_VALUE);
        }
        
        String effectiveStrategy = (strategy != null) ? strategy : "shortest_distance";
        
        return switch (effectiveStrategy) {
            case "shortest_time" -> {
                // 根据交通方式选择对应的时间字段
                int time = switch (transportMode != null ? transportMode : 1) {
                    case 1 -> edge.getWalkTime() != null ? edge.getWalkTime() : Integer.MAX_VALUE / 2;
                    case 2 -> edge.getBikeTime() != null ? edge.getBikeTime() : Integer.MAX_VALUE / 2;
                    case 3 -> edge.getShuttleTime() != null ? edge.getShuttleTime() : Integer.MAX_VALUE / 2;
                    default -> edge.getWalkTime() != null ? edge.getWalkTime() : Integer.MAX_VALUE / 2;
                };
                yield BigDecimal.valueOf(time);
            }
            case "avoid_crowd" -> {
                // 距离 × (1 + 拥挤度系数)
                double congestion = edge.getCurrentCongestion() != null 
                        ? edge.getCurrentCongestion().doubleValue() : 0.0;
                double penalty = 1.0 + congestion;  // 拥挤度为0时不惩罚，拥挤度1.0时权重翻倍
                yield edge.getDistance().multiply(BigDecimal.valueOf(penalty));
            }
            default -> edge.getDistance();  // shortest_distance
        };
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
     * 节点权重对（用于 Dijkstra 优先队列排序）
     */
    private static class NodeWeight {
        Long nodeId;
        BigDecimal weight;
        
        NodeWeight(Long nodeId, BigDecimal weight) {
            this.nodeId = nodeId;
            this.weight = weight;
        }
    }

    /**
     * A* 节点（用于 A* 优先队列排序，按 f(n) = g(n) + h(n)）
     */
    private static class AStarNode {
        Long nodeId;
        BigDecimal fScore;   // f(n) = g(n) + h(n)

        AStarNode(Long nodeId, BigDecimal gScore, BigDecimal hScore) {
            this.nodeId = nodeId;
            this.fScore = gScore.add(hScore);
        }
    }
}
