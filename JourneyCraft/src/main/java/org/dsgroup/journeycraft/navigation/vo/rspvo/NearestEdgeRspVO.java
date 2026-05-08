package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

import java.util.List;

@Data
public class NearestEdgeRspVO {

    private Long edgeId;
    private Double snapLat;
    private Double snapLng;
    private Double snapPosition;
    private Double distance;
    private EdgeInfoVO edgeInfo;
    private Integer connectedComponentSize;
    private String confidence;
    private List<AlternativeVO> alternatives;

    @Data
    public static class EdgeInfoVO {
        private Long fromNodeId;
        private Long toNodeId;
        private String highwayType;
        private List<String> transportModes;
        private String name;
    }

    @Data
    public static class AlternativeVO {
        private String type;
        private Long nodeId;
        private Double distance;
    }
}
