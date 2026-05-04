package org.dsgroup.journeycraft.history.vo.reqvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询浏览历史请求 VO
 */
@Data
@Schema(description = "查询浏览历史请求")
public class ViewHistoryReqVO {

    @Schema(description = "目标类型：0-景点 1-校园 2-日记 3-设施", example = "0")
    private Integer type;

    @Schema(description = "页码", example = "1")
    private Integer page;

    @Schema(description = "每页数量", example = "10")
    private Integer size;
}
