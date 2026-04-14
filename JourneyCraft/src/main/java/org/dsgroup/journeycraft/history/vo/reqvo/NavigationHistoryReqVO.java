package org.dsgroup.journeycraft.history.vo.reqvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询导航历史请求 VO
 */
@Data
@Schema(description = "查询导航历史请求")
public class NavigationHistoryReqVO {

    @Schema(description = "景区 ID", example = "1")
    private Long scenicAreaId;

    @Schema(description = "页码", example = "1")
    private Integer page;

    @Schema(description = "每页数量", example = "10")
    private Integer size;
}
