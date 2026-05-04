package org.dsgroup.journeycraft.scenic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 建筑物实体。
 */
@Data
@TableName("t_building")
public class Building {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long scenicAreaId;

    private Long nodeId;

    private String name;

    private Integer type;

    private Integer floorCount;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String description;

    private String indoorMap;

    private String images;

    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
