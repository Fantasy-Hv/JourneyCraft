package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 景点详情响应数据。
 */
@Data
public class ScenicItemRspVO {

    private Long id;

    private String name;

    private Integer type;

    private String city;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String description;

    private BigDecimal rating;

    private Integer heatScore;

    private Integer visitCount;

    private BigDecimal ticketPrice;

    private Map<String, Map<String, String>> openingHours;

    private List<String> images;
}
