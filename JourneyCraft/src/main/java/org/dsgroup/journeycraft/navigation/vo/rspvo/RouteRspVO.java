package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;
import java.util.List;

/**
 * 导航路线响应 — 统一 route/multi-route 返回格式
 */
@Data
public class RouteRspVO {
    private Long routeId;
    private Double totalDistance;
    private Integer estimatedTime;
    private String transportMode;
    private String strategy;
    private List<SegmentVO> segments;
    private EndpointInfoVO startInfo;
    private EndpointInfoVO endInfo;
    private List<PathPointVO> path;
    private List<Long> visitOrder;

    @Data
    public static class SegmentVO {
        private Integer order;         // 段序号 (1-based)
        private String type;          // walk/bike/push_bike/shuttle/walk_access/walk_egress
        private Double distance;
        private Integer time;
        private List<Long> nodeIds;
        private EndpointInfoVO from;   // 此段起点
        private EndpointInfoVO to;     // 此段终点
        private List<PathPointVO> path; // 此段路径点 (GCJ-02)
    }

    @Data
    public static class EndpointInfoVO {
        private Long nodeId;           // 节点ID
        private String name;           // 节点名称
        private String inputType;     // scenicArea/node/edge/coordinate
        private Long scenicAreaId;
        private String scenicAreaName;
        private Long resolvedNodeId;
        private String accessType;
        private Double latitude;
        private Double longitude;
    }

    @Data
    public static class PathPointVO {
        private Double lat;
        private Double lng;
        private String segmentType;
    }
}
