package org.dsgroup.journeycraft.scenic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 独立美食实体。
 */
@Data
@TableName("t_food_place")
public class FoodPlace {

    @TableId(type = IdType.AUTO)
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

    private String openingHours;

    private String images;

    private String tags;

    private String contactInfo;

    private BigDecimal rating;

    private Integer reviewCount;

    private Integer heatScore;

    private Integer recommendScore;

    private Integer status;

    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
