package org.dsgroup.journeycraft.history.vo.rspvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 搜索历史项响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索历史项响应")
public class SearchHistoryRspVO {

    @Schema(description = "记录 ID", example = "1")
    private Long id;

    @Schema(description = "搜索关键词", example = "故宫")
    private String keyword;

    @Schema(description = "搜索类型：0-景点 1-校园 2-日记 3-美食", example = "0")
    private Integer searchType;

    @Schema(description = "结果数量", example = "15")
    private Integer resultCount;

    @Schema(description = "点击的目标 ID", example = "1")
    private Long clickedId;

    @Schema(description = "搜索时间", example = "2024-03-25T10:00:00")
    private LocalDateTime searchedAt;
}
