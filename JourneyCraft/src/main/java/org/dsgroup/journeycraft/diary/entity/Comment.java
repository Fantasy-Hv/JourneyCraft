package org.dsgroup.journeycraft.diary.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论文档实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comment")
public class Comment {

    @Id
    private String id;

    /**
     * 关联日记 ID
     */
    @Indexed
    private String diaryId;

    /**
     * 评论者 ID
     */
    @Indexed
    private Long userId;

    /**
     * 评论者昵称
     */
    private String userNickname;

    /**
     * 评论者头像
     */
    private String userAvatar;

    /**
     * 父评论 ID (回复时填写)
     */
    private String parentId;

    /**
     * 回复的目标用户 ID
     */
    private Long replyToUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论图片
     */
    private List<String> images;

    /**
     * 点赞数
     */
    @Builder.Default
    private Long likeCount = 0L;

    /**
     * 回复数
     */
    @Builder.Default
    private Long replyCount = 0L;

    /**
     * 状态：0-隐藏 1-显示
     */
    @Builder.Default
    private Integer status = 1;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
