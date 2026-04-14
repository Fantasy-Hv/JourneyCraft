package org.dsgroup.journeycraft.diary.vo.rspvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论响应")
public class CommentRspVO {

    @Schema(description = "评论 ID", example = "65f8a1b2c3d4e5f6a7b8c9e1")
    private String id;

    @Schema(description = "评论者 ID", example = "1002")
    private Long userId;

    @Schema(description = "评论者昵称", example = "摄影师小王")
    private String userNickname;

    @Schema(description = "评论者头像", example = "http://localhost:9000/journeycraft/avatar/user_1002.jpg")
    private String userAvatar;

    @Schema(description = "评论内容", example = "写得真好！")
    private String content;

    @Schema(description = "评论图片", example = "[\"http://...\"]")
    private List<String> images;

    @Schema(description = "点赞数", example = "15")
    private Long likeCount;

    @Schema(description = "回复数", example = "2")
    private Long replyCount;

    @Schema(description = "创建时间", example = "2024-03-15T11:20:00Z")
    private LocalDateTime createdAt;

    @Schema(description = "回复列表", example = "[]")
    private List<CommentRspVO> replies;
}
