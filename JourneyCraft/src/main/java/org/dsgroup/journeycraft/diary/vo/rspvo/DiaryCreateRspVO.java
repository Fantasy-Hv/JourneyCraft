package org.dsgroup.journeycraft.diary.vo.rspvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建日记响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建日记响应")
public class DiaryCreateRspVO {

    @Schema(description = "日记 ID", example = "65f8a1b2c3d4e5f6a7b8c9d0")
    private String diaryId;
}
