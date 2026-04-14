package org.dsgroup.journeycraft.diary.vo.rspvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 点赞响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点赞响应")
public class DiaryLikeRspVO {

    @Schema(description = "点赞数", example = "129")
    private Long likeCount;

    @Schema(description = "是否已点赞", example = "true")
    private Boolean isLiked;
}
