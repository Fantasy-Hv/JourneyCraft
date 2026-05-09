package org.dsgroup.journeycraft.navigation.vo.reqvo;

import lombok.Data;

/**
 * 多目标路径规划 — 单个目标（支持三种类型）
 */
@Data
public class RouteTarget {
    private Long scenicAreaId;
    private Long nodeId;
    private Double lat;
    private Double lng;

    public String detectType() {
        if (scenicAreaId != null) return "scenicArea";
        if (nodeId != null) return "node";
        if (lat != null && lng != null) return "coordinate";
        return null;
    }
}
