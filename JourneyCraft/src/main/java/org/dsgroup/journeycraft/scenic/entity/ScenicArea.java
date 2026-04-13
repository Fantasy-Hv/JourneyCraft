package org.dsgroup.journeycraft.scenic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点/校园实体。
 */
@Data
@TableName("t_scenic_area")
public class ScenicArea {

    @TableId(type = IdType.AUTO)
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

    private String openingHours;

    private String images;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
