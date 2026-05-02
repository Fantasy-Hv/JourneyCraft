package org.dsgroup.journeycraft.navigation.service;

import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.entity.RoadEdge;

import java.math.BigDecimal;
import java.util.List;

/**
 * 路径规划算法服务
 * <p>
 * 实现Dijkstra/A*单目标路径规划和TSP变种多目标路线规划
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
public interface PathPlanningService {

    /**
     * 单目标路径规划（Dijkstra算法）
     * <p>
     * 计算从起点到终点的最短路径
     *
     * @param startNodeId 起点节点ID
     * @param endNodeId 终点节点ID
     * @param transportMode 交通方式: 1=步行, 2=骑行, 3=驾驶
     * @param strategy 规划策略: shortest_distance/shortest_time/avoid_crowd
     * @return 路径规划结果
     */
    PathPlanningResult calculateShortestPath(Long startNodeId, Long endNodeId, 
                                            Integer transportMode, String strategy);

    /**
     * 单目标路径规划（A*算法）
     * <p>
     * 使用 Haversine 直线距离作为启发式函数，在大多数场景下比 Dijkstra 探索更少节点。
     * 启发式函数具有可采纳性（admissible），保证找到最优解。
     *
     * @param startNodeId 起点节点ID
     * @param endNodeId 终点节点ID
     * @param transportMode 交通方式: 1=步行, 2=骑行, 3=驾驶
     * @param strategy 规划策略: shortest_distance/shortest_time/avoid_crowd
     * @return 路径规划结果
     */
    PathPlanningResult calculateAStarPath(Long startNodeId, Long endNodeId,
                                          Integer transportMode, String strategy);

    /**
     * 多目标路线规划（TSP变种算法）
     * <p>
     * 计算从起点出发，访问所有目标节点的最优路线
     *
     * @param startNodeId 起点节点ID
     * @param endNodeIds 目标节点ID列表
     * @param transportMode 交通方式
     * @param needReturn 是否需要返回起点
     * @return 多目标路线规划结果
     */
    MultiTargetRouteResult calculateMultiTargetRoute(Long startNodeId, List<Long> endNodeIds,
                                                     Integer transportMode, boolean needReturn);

    /**
     * 获取两个节点之间的实际距离
     *
     * @param fromNodeId 起始节点ID
     * @param toNodeId 目标节点ID
     * @return 距离（米），如果路径不存在返回null
     */
    BigDecimal getDistanceBetweenNodes(Long fromNodeId, Long toNodeId);

    /**
     * 获取两个节点之间的预计时间
     *
     * @param fromNodeId 起始节点ID
     * @param toNodeId 目标节点ID
     * @param transportMode 交通方式
     * @return 时间（秒），如果路径不存在返回null
     */
    Integer getTimeBetweenNodes(Long fromNodeId, Long toNodeId, Integer transportMode);

    /**
     * 获取设施关联的节点ID
     * <p>
     * 通过设施ID查找关联的路网节点
     *
     * @param facilityId 设施ID
     * @return 关联的节点ID，如果不存在返回null
     */
    Long getFacilityNodeId(Long facilityId);

    /**
     * 查找距离给定坐标最近的路网节点。
     * <p>
     * 使用 Haversine 公式计算各节点到 (lat, lng) 的直线距离，返回最近的节点ID。
     * 当前实现为全表扫描，节点数量 < 1000 时性能可接受。
     *
     * @param lat 目标纬度
     * @param lng 目标经度
     * @return 最近节点的ID，无可用节点返回 null
     */
    Long findNearestNodeByCoords(BigDecimal lat, BigDecimal lng);

    /**
     * 路径规划结果
     */
    class PathPlanningResult {
        /** 路线ID */
        private Long routeId;
        /** 总距离（米） */
        private BigDecimal totalDistance;
        /** 预计时间（秒） */
        private Integer estimatedTime;
        /** 交通方式 */
        private String transportMode;
        /** 路径节点列表 */
        private List<PathNode> nodes;
        /** 规划策略 */
        private String strategy;

        // getters and setters
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public BigDecimal getTotalDistance() { return totalDistance; }
        public void setTotalDistance(BigDecimal totalDistance) { this.totalDistance = totalDistance; }
        public Integer getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
        public String getTransportMode() { return transportMode; }
        public void setTransportMode(String transportMode) { this.transportMode = transportMode; }
        public List<PathNode> getNodes() { return nodes; }
        public void setNodes(List<PathNode> nodes) { this.nodes = nodes; }
        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }
    }

    /**
     * 多目标路线规划结果
     */
    class MultiTargetRouteResult {
        /** 路线ID */
        private Long routeId;
        /** 总距离（米） */
        private BigDecimal totalDistance;
        /** 总时间（秒） */
        private Integer totalTime;
        /** 访问顺序 */
        private List<Long> visitOrder;
        /** 各段路径详情 */
        private List<PathPlanningResult> segments;
        /** 是否返回起点 */
        private boolean returnedToStart;

        // getters and setters
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public BigDecimal getTotalDistance() { return totalDistance; }
        public void setTotalDistance(BigDecimal totalDistance) { this.totalDistance = totalDistance; }
        public Integer getTotalTime() { return totalTime; }
        public void setTotalTime(Integer totalTime) { this.totalTime = totalTime; }
        public List<Long> getVisitOrder() { return visitOrder; }
        public void setVisitOrder(List<Long> visitOrder) { this.visitOrder = visitOrder; }
        public List<PathPlanningResult> getSegments() { return segments; }
        public void setSegments(List<PathPlanningResult> segments) { this.segments = segments; }
        public boolean isReturnedToStart() { return returnedToStart; }
        public void setReturnedToStart(boolean returnedToStart) { this.returnedToStart = returnedToStart; }
    }

    /**
     * 路径节点
     */
    class PathNode {
        /** 节点ID */
        private Long nodeId;
        /** 节点名称 */
        private String name;
        /** 顺序 */
        private Integer sequence;
        /** 纬度 */
        private BigDecimal latitude;
        /** 经度 */
        private BigDecimal longitude;
        /** 动作: start/visit/end */
        private String action;
        /** 到达时间 */
        private String arrivalTime;

        // getters and setters
        public Long getNodeId() { return nodeId; }
        public void setNodeId(Long nodeId) { this.nodeId = nodeId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getArrivalTime() { return arrivalTime; }
        public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    }
}
