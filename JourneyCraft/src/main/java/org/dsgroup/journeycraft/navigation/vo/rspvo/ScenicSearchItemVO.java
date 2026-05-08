package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScenicSearchItemVO {

    private Long id;

    private String name;

    private String type = "scenic";

    private Integer scenicType;

    private String city;

    private Double latitude;

    private Double longitude;

    private BigDecimal rating;

    private Integer heatScore;
}
