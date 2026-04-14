package org.dsgroup.journeycraft.history.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导航历史实体，对应用户在景区内的导航记录。
 */
@Data
@TableName("t_navigation_history")
public class NavigationHistory {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 景区 ID
     */
    private Long scenicAreaId;

    /**
     * 起点节点 ID
     */
    private Long startNodeId;

    /**
     * 终点节点 ID
     */
    private Long endNodeId;

    /**
     * 路径节点序列（存储节点 id，关联导航路线表）
     */
    private String pathNodes;

    /**
     * 交通方式：1-步行 2-自行车 3-电瓶车 4-公共交通
     */
    private Integer transportMode;

    /**
     * 实际用时 (秒)
     */
    private Integer actualTime;

    /**
     * 是否完成：0-否 1-是
     */
    private Integer isCompleted;

    /**
     * 导航时间
     */
    private LocalDateTime navigatedAt;

    /**
     * 逻辑删除：0-否 1-是
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
