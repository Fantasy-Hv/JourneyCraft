package org.dsgroup.journeycraft.diary.vo.reqvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询日记列表请求 VO
 */
@Data
@Schema(description = "查询日记列表请求")
public class DiaryListReqVO {

    @Schema(description = "作者 ID", example = "1001")
    private Long userId;

    @Schema(description = "标签过滤 (逗号分隔)", example = "自然风光，登山")
    private String tags;

    @Schema(description = "排序方式：heat-热度，rating-评分，time-时间", example = "time")
    private String sortBy;

    @Schema(description = "页码", example = "1")
    private Integer page;

    @Schema(description = "每页数量", example = "10")
    private Integer size;
}
