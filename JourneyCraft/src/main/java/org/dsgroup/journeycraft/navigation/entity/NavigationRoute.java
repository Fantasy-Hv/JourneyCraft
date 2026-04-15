package org.dsgroup.journeycraft.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 导航路线实体类
 * <p>
 * 对应表: t_navigation_route
 * 用途: 存储用户规划的路线，供接口4.1/4.2返回routeId
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Data
@TableName("t_navigation_route")
public class NavigationRoute {

    /**
     * 路线ID（自增主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（可为空，匿名规划）
     */
    private Long userId;

    /**
     * 景区ID
     */
    private Long scenicAreaId;

    /**
     * 起点节点ID
     */
    private Long startNodeId;

    /**
     * 终点列表（多目标规划时使用，JSON数组格式）
     * 例如: [20, 30, 40]
     */
    private String endNodeIds;

    /**
     * 路径节点序列（JSON数组格式）
     * 格式: [{"nodeId":10,"sequence":0,"name":"起点","action":"start"},...]
     */
    private String pathNodes;

    /**
     * 总距离（米）
     */
    private BigDecimal totalDistance;

    /**
     * 预计时间（秒）
     */
    private Integer estimatedTime;

    /**
     * 交通方式组合（JSON数组格式）
     * 格式: [{"segment":0,"mode":"walk","distance":500,"duration":400}]
     */
    private String transportModes;

    /**
     * 规划策略
     * 可选值: shortest_distance / shortest_time / avoid_crowd / scenic_route
     */
    private String strategy;

    /**
     * 是否删除: 0=否, 1=是
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
