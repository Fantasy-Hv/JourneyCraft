package org.dsgroup.journeycraft.navigation.vo.reqvo;

import lombok.Data;

/**
 * 路径规划请求 — 起点/终点支持四种输入
 */
@Data
public class RouteRequest {
    // 起点（四选一）
    private Long startScenicAreaId;
    private Long startNodeId;
    private Long startEdgeId;
    private Double startSnapPos;
    private Double startLat;
    private Double startLng;

    // 终点（四选一）
    private Long endScenicAreaId;
    private Long endNodeId;
    private Long endEdgeId;
    private Double endSnapPos;
    private Double endLat;
    private Double endLng;

    private String transportMode = "walk";
    private String strategy = "shortest_distance";
    private String algorithm = "astar";

    public String detectStartType() {
        if (startScenicAreaId != null) return "scenicArea";
        if (startNodeId != null) return "node";
        if (startEdgeId != null) return "edge";
        if (startLat != null && startLng != null) return "coordinate";
        return null;
    }

    public String detectEndType() {
        if (endScenicAreaId != null) return "scenicArea";
        if (endNodeId != null) return "node";
        if (endEdgeId != null) return "edge";
        if (endLat != null && endLng != null) return "coordinate";
        return null;
    }
}
