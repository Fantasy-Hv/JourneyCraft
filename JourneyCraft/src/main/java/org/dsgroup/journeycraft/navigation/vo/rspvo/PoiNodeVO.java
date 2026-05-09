package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

/**
 * 景区POI展示节点
 */
@Data
public class PoiNodeVO {
    private Long nodeId;
    private String name;
    private Integer nodeType;
    private String poiType;
    private Double latitude;
    private Double longitude;
    private String description;
    private Long facilityId;
}
