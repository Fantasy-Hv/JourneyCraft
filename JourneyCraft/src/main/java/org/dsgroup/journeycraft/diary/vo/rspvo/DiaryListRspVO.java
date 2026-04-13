package org.dsgroup.journeycraft.diary.vo.rspvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 日记列表项响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "日记列表项响应")
public class DiaryListRspVO {

    @Schema(description = "日记 ID", example = "65f8a1b2c3d4e5f6a7b8c9d0")
    private String id;

    @Schema(description = "作者 ID", example = "1001")
    private Long userId;

    @Schema(description = "作者昵称", example = "旅行达人")
    private String userNickname;

    @Schema(description = "作者头像", example = "http://localhost:9000/journeycraft/avatar/user_1001.jpg")
    private String userAvatar;

    @Schema(description = "标题", example = "黄山三日游 - 云海奇观")
    private String title;

    @Schema(description = "摘要", example = "这次黄山之行看到了壮丽的云海和日出，非常震撼...")
    private String summary;

    @Schema(description = "封面图", example = "http://localhost:9000/journeycraft/diary/cover_001.jpg")
    private String coverImage;

    @Schema(description = "标签", example = "[\"自然风光\", \"登山\", \"云海\"]")
    private List<String> tags;

    @Schema(description = "评分 0-5", example = "4.8")
    private Double rating;

    @Schema(description = "点赞数", example = "128")
    private Long likeCount;

    @Schema(description = "图片列表", example = "[\"http://...\"]")
    private List<String> images;

    @Schema(description = "视频列表", example = "[\"http://...\"]")
    private List<String> videos;

    @Schema(description = "浏览数", example = "1523")
    private Long viewCount;

    @Schema(description = "评论数", example = "32")
    private Long commentCount;

    @Schema(description = "创建时间", example = "2024-03-15T10:30:00Z")
    private java.time.LocalDateTime createdAt;
}
