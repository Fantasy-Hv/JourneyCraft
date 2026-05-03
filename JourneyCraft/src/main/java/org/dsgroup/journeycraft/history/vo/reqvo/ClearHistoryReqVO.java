package org.dsgroup.journeycraft.history.vo.reqvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 清空历史请求 VO
 */
@Data
@Schema(description = "清空历史请求")
public class ClearHistoryReqVO {

    @Schema(description = "历史类型：view-浏览历史 search-搜索历史 navigation-导航历史，不传则清空全部",
            example = "view")
    private String type;
}
