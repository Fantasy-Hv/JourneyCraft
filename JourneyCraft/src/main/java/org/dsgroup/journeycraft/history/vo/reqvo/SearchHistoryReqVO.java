package org.dsgroup.journeycraft.history.vo.reqvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询搜索历史请求 VO
 */
@Data
@Schema(description = "查询搜索历史请求")
public class SearchHistoryReqVO {

    @Schema(description = "搜索类型：0-景点 1-校园 2-日记 3-美食", example = "0")
    private Integer type;

    @Schema(description = "返回数量", example = "10")
    private Integer limit;
}
