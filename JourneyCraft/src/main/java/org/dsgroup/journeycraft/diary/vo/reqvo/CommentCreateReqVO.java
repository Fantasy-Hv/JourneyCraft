package org.dsgroup.journeycraft.diary.vo.reqvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 添加评论请求 VO
 */
@Data
@Schema(description = "添加评论请求")
public class CommentCreateReqVO {

    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "写得真好！")
    private String content;

    @Schema(description = "父评论 ID (回复时填写)", example = "65f8a1b2c3d4e5f6a7b8c9e1")
    private String parentId;

    @Schema(description = "评论图片", example = "[\"http://...\"]")
    private List<String> images;
}
