package org.dsgroup.journeycraft.navigation.vo.reqvo;

import lombok.Data;

/**
 * 多目标路径规划请求
 */
@Data
public class MultiRouteRequest {
    // 起点（四选一，同 RouteRequest）
    private Long startScenicAreaId;
    private Long startNodeId;
    private Long startEdgeId;
    private Double startSnapPos;
    private Double startLat;
    private Double startLng;

    private java.util.List<RouteTarget> targets;
    private String transportMode = "walk";
    private String strategy = "shortest_distance";
    private Boolean needReturn = false;

    public String detectStartType() {
        if (startScenicAreaId != null) return "scenicArea";
        if (startNodeId != null) return "node";
        if (startEdgeId != null) return "edge";
        if (startLat != null && startLng != null) return "coordinate";
        return null;
    }
}
