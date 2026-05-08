package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

import java.util.List;

@Data
public class NodeSearchItemVO {

    private Long id;

    private Long osmId;

    private String name;

    private String type = "road_node";

    private Integer nodeType;

    private Double latitude;

    private Double longitude;

    private List<Integer> transportTypes;
}
