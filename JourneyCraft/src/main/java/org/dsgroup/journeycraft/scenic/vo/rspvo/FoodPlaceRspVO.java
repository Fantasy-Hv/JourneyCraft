package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 美食信息返回数据。
 */
@Data
public class FoodPlaceRspVO {

    private Long id;

    private Long scenicAreaId;

    private Long buildingId;

    private Long nodeId;

    private Long sourceFacilityId;

    private String name;

    private String category;

    private String cuisineType;

    private Integer priceLevel;

    private BigDecimal avgPrice;

    private String priceRange;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer floorNumber;

    private String description;

    private Map<String, Map<String, String>> openingHours;

    private List<String> images;

    private List<String> tags;

    private String contactInfo;

    private BigDecimal rating;

    private Integer reviewCount;

    private Integer heatScore;

    private Integer recommendScore;

    private Integer status;
}
