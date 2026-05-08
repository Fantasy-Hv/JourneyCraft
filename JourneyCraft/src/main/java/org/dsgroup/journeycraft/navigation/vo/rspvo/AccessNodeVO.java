package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

@Data
public class AccessNodeVO {

    private Long nodeId;
    private Long osmId;
    private String name;
    private Integer nodeType;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private Boolean isPrimary;
    private Boolean viaWalk;
    private Integer connectedComponentSize;
    private Double walkDistance;
}
