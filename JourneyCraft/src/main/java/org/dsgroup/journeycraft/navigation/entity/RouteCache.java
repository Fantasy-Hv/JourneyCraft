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
 * 路径规划缓存实体类
 * <p>
 * 对应数据库表: t_navigation_route_cache
 * 缓存路径规划结果，提高重复查询性能
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_navigation_route_cache")
public class RouteCache implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 缓存ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 起始节点ID
     */
    @TableField("start_node_id")
    private Long startNodeId;

    /**
     * 目标节点ID
     */
    @TableField("end_node_id")
    private Long endNodeId;

    /**
     * 通行方式
     * 0=未知, 1=仅步行, 2=仅自行车, 3=仅车辆, 4=步行+自行车, 5=全部
     */
    @TableField("transport_type")
    private Integer transportType;

    /**
     * 规划策略
     * shortest_distance/shortest_time/avoid_crowd
     */
    @TableField("strategy")
    private String strategy;

    /**
     * 路径节点ID列表（JSON数组）
     * 例如：[1, 5, 10, 15, 20]
     */
    @TableField("route_nodes")
    private String routeNodes;

    /**
     * 总距离（米）
     */
    @TableField("total_distance")
    private BigDecimal totalDistance;

    /**
     * 总时间（秒）
     */
    @TableField("total_time")
    private Integer totalTime;

    /**
     * 计算时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("calculated_at")
    private Date calculatedAt;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("expires_at")
    private Date expiresAt;

    /**
     * 命中次数
     */
    @TableField("hit_count")
    private Integer hitCount;

    /**
     * 计算耗时（毫秒）
     */
    @TableField("calculation_time_ms")
    private Integer calculationTimeMs;

    /**
     * 路径节点数
     */
    @TableField("node_count")
    private Integer nodeCount;

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
     * 获取规划策略名称
     */
    public String getStrategyName() {
        if (strategy == null || strategy.isEmpty()) return "默认";
        switch (strategy) {
            case "shortest_distance": return "最短距离";
            case "shortest_time": return "最短时间";
            case "avoid_crowd": return "避开拥挤";
            default: return strategy;
        }
    }

    /**
     * 判断缓存是否过期
     */
    public boolean isExpired() {
        if (expiresAt == null) return false;
        return expiresAt.before(new Date());
    }

    /**
     * 检查缓存是否有效（未删除且未过期）
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(deleted) && !isExpired();
    }

    /**
     * 增加命中次数
     */
    public void incrementHitCount() {
        this.hitCount = (this.hitCount == null ? 1 : this.hitCount + 1);
    }

    /**
     * 获取缓存的唯一查询键
     */
    public String getCacheKey() {
        return String.format("%d-%d-%d-%s", 
            startNodeId, endNodeId, transportType, 
            strategy != null ? strategy : "default");
    }

    /**
     * 计算缓存效率（命中次数/天数）
     */
    public double getHitEfficiency() {
        if (calculatedAt == null || hitCount == null || hitCount == 0) {
            return 0.0;
        }
        long days = Math.max(1, (System.currentTimeMillis() - calculatedAt.getTime()) / (1000 * 3600 * 24));
        return hitCount.doubleValue() / days;
    }

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
}