package org.dsgroup.journeycraft.history.vo.reqvo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 记录浏览历史请求 VO
 */
@Data
@Schema(description = "记录浏览历史请求")
public class RecordViewReqVO {

    @NotNull(message = "目标类型不能为空")
    @Schema(description = "目标类型：0-景点 1-校园 2-日记 3-设施", example = "0", required = true)
    private Integer targetType;

    @NotNull(message = "目标 ID 不能为空")
    @Schema(description = "目标 ID", example = "1", required = true)
    private Long targetId;

    @Schema(description = "目标名称", example = "故宫")
    private String targetName;

    @Schema(description = "浏览时长 (秒)", example = "120")
    private Integer viewDuration;
}
