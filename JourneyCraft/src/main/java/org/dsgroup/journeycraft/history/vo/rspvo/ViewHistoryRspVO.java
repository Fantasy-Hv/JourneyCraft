package org.dsgroup.journeycraft.history.vo.rspvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 浏览历史项响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "浏览历史项响应")
public class ViewHistoryRspVO {

    @Schema(description = "记录 ID", example = "1")
    private Long id;

    @Schema(description = "目标类型：0-景点 1-校园 2-日记 3-设施", example = "0")
    private Integer targetType;

    @Schema(description = "目标 ID", example = "1")
    private Long targetId;

    @Schema(description = "目标名称", example = "故宫")
    private String targetName;

    @Schema(description = "目标图片", example = "http://localhost:9000/journeycraft/scenic/1.jpg")
    private String targetImage;

    @Schema(description = "浏览时长 (秒)", example = "120")
    private Integer viewDuration;

    @Schema(description = "浏览时间", example = "2024-03-25T10:00:00")
    private LocalDateTime viewTime;
}
