package org.dsgroup.journeycraft.scenic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点设施实体。
 */
@Data
@TableName("t_facility")
public class Facility {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long scenicAreaId;

    private Long buildingId;

    private Long nodeId;

    private String name;

    private Integer type;

    private String subtype;

    private Integer facilityClass;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer floorNumber;

    private String description;

    private BigDecimal rating;

    private Integer reviewCount;

    private Integer heatScore;

    private String priceRange;

    private String openingHours;

    private String images;

    private String contactInfo;

    private String tags;

    private Integer status;

    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
