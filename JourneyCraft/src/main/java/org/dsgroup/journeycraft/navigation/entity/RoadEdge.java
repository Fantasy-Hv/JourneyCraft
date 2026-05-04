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
 * 路径段实体类
 * <p>
 * 对应数据库表: t_navigation_road_edge
 * 存储OSM路径段，连接两个路网节点，支持多种交通方式
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_navigation_road_edge")
public class RoadEdge implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路径ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * OSM路径ID
     */
    @TableField("osm_way_id")
    private Long osmWayId;

    /**
     * OSM标签（JSON格式）
     */
    @TableField("osm_tags")
    private String osmTags;

    /**
     * 起始节点ID
     */
    @TableField("from_node_id")
    private Long fromNodeId;

    /**
     * 目标节点ID
     */
    @TableField("to_node_id")
    private Long toNodeId;

    /**
     * 路径名称
     */
    @TableField("name")
    private String name;

    /**
     * 几何距离（米）
     */
    @TableField("distance")
    private BigDecimal distance;

    /**
     * 调整后距离（考虑坡度、障碍等）
     */
    @TableField("adjusted_distance")
    private BigDecimal adjustedDistance;

    /**
     * 步行时间（秒）
     */
    @TableField("walk_time")
    private Integer walkTime;

    /**
     * 自行车时间（秒）
     */
    @TableField("bike_time")
    private Integer bikeTime;

    /**
     * 电瓶车时间（秒）
     */
    @TableField("shuttle_time")
    private Integer shuttleTime;

    /**
     * 通行方式
     * 0=未知, 1=仅步行, 2=仅自行车, 3=仅车辆, 4=步行+自行车, 5=全部
     */
    @TableField("transport_type")
    private Integer transportType;

    /**
     * 是否双向通行
     * 0=否, 1=是
     */
    @TableField("is_bidirectional")
    private Boolean bidirectional;

    /**
     * OSM道路类型（highway标签值）
     */
    @TableField("highway_type")
    private String highwayType;

    /**
     * 路面类型
     */
    @TableField("surface")
    private String surface;

    /**
     * 坡度（%）
     */
    @TableField("incline")
    private BigDecimal incline;

    /**
     * 基础拥挤度（0-1）
     */
    @TableField("base_congestion")
    private BigDecimal baseCongestion;

    /**
     * 实时拥挤度（0-1）
     */
    @TableField("current_congestion")
    private BigDecimal currentCongestion;

    /**
     * 是否启用
     * 0=禁用, 1=启用
     */
    @TableField("is_enabled")
    private Boolean enabled;

    /**
     * 是否删除（逻辑删除字段）
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Boolean deleted;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /**
     * 获取通行方式名称
     */
    public String getTransportTypeName() {
        if (transportType == null) return "未知";
        switch (transportType) {
            case 0: return "未知";
            case 1: return "仅步行";
            case 2: return "仅自行车";
            case 3: return "仅车辆";
            case 4: return "步行+自行车";
            case 5: return "全部";
            default: return "未知";
        }
    }

    /**
     * 判断是否支持步行
     */
    public boolean supportsWalking() {
        return transportType != null && (transportType == 1 || transportType == 4 || transportType == 5);
    }

    /**
     * 判断是否支持自行车
     */
    public boolean supportsBike() {
        return transportType != null && (transportType == 2 || transportType == 4 || transportType == 5);
    }

    /**
     * 获取实际距离（优先使用调整后距离）
     */
    public BigDecimal getEffectiveDistance() {
        return adjustedDistance != null ? adjustedDistance : distance;
    }

    /**
     * 获取步行实际时间（秒）
     * 如果walk_time为空，根据距离估算（假设步行速度1.4米/秒）
     */
    public Integer getEffectiveWalkTime() {
        if (walkTime != null && walkTime > 0) {
            return walkTime;
        }
        BigDecimal dist = getEffectiveDistance();
        if (dist != null) {
            return dist.divide(new BigDecimal("1.4"), 0, BigDecimal.ROUND_HALF_UP).intValue();
        }
        return 0;
    }
}