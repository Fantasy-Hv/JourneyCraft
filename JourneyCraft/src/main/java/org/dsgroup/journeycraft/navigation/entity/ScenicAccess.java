package org.dsgroup.journeycraft.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 景区路网接入点实体类
 * <p>
 * 对应数据库表: t_navigation_scenic_access
 * 存储景区在各交通方式下的路网接入节点，支持导航起终点解析
 * 
 * @author 后端智能体
 * @since 2026-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_navigation_scenic_access")
public class ScenicAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("scenic_area_id")
    private Long scenicAreaId;

    /**
     * 接入点类型: 1=walk_entry, 2=bike_entry, 3=vehicle_entry, 4=poi_display, 5=main_entrance
     */
    @TableField("access_type")
    private Integer accessType;

    @TableField("road_node_id")
    private Long roadNodeId;

    @TableField("transport_types")
    private Integer transportTypes;

    @TableField("rank_order")
    private Integer rankOrder;

    @TableField("distance_to_scenic")
    private BigDecimal distanceToScenic;

    @TableField("is_primary")
    private Integer isPrimary;

    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
