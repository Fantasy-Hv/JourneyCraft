package org.dsgroup.journeycraft.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 导航历史实体类
 * <p>
 * 对应表: t_navigation_history
 * 用途: 记录用户的历史导航记录，供history模块查询
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Data
@TableName("t_navigation_history")
public class NavigationHistory {

    /**
     * 历史ID（自增主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
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
     * 终点节点ID
     */
    private Long endNodeId;

    /**
     * 路径节点序列（JSON数组格式）
     * 格式: [10, 15, 18, 20]
     */
    private String pathNodes;

    /**
     * 交通方式: 1=步行, 2=自行车, 3=电瓶车
     */
    private Integer transportMode;

    /**
     * 实际用时（秒）
     */
    private Integer actualTime;

    /**
     * 是否完成: 0=未完成, 1=已完成
     */
    private Integer isCompleted;

    /**
     * 导航时间
     */
    private LocalDateTime navigatedAt;

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
