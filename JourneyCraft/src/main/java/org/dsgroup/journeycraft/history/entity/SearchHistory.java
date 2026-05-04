package org.dsgroup.journeycraft.history.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索历史实体，对应用户搜索景点、校园、日记、美食的历史记录。
 */
@Data
@TableName("t_search_history")
public class SearchHistory {

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
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索类型：0-景点 1-校园 2-日记 3-美食
     */
    private Integer searchType;

    /**
     * 结果数量
     */
    private Integer resultCount;

    /**
     * 点击的目标 ID（用户点击了哪个结果）
     */
    private Long clickedId;

    /**
     * 搜索时间
     */
    private LocalDateTime searchedAt;

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
