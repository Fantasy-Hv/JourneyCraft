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

    private String name;

    private Integer type;

    private String subtype;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal rating;

    private String priceRange;

    private String images;

    private Integer status;

    private LocalDateTime createdAt;
}
