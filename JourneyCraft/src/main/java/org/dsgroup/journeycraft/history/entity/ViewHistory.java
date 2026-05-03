package org.dsgroup.journeycraft.history.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 浏览历史实体，对应用户浏览景点、校园、日记、设施的历史记录。
 */
@Data
@TableName("t_view_history")
public class ViewHistory {

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
     * 目标类型：0-景点 1-校园 2-日记 3-设施
     */
    private Integer targetType;

    /**
     * 目标 ID
     */
    private Long targetId;

    /**
     * 目标名称
     */
    private String targetName;

    /**
     * 浏览时长 (秒)
     */
    private Integer viewDuration;

    /**
     * 浏览时间
     */
    private LocalDateTime viewTime;

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
