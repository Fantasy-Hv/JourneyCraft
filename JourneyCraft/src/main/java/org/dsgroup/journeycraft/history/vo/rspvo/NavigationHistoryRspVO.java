package org.dsgroup.journeycraft.history.vo.rspvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 导航历史项响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "导航历史项响应")
public class NavigationHistoryRspVO {

    @Schema(description = "记录 ID", example = "1")
    private Long id;

    @Schema(description = "景区 ID", example = "1")
    private Long scenicAreaId;

    @Schema(description = "景区名称", example = "故宫")
    private String scenicAreaName;

    @Schema(description = "起点节点 ID", example = "10")
    private Long startNodeId;

    @Schema(description = "起点名称", example = "东门入口")
    private String startNodeName;

    @Schema(description = "终点节点 ID", example = "20")
    private Long endNodeId;

    @Schema(description = "终点名称", example = "太和殿")
    private String endNodeName;

    @Schema(description = "交通方式：1-步行 2-自行车 3-电瓶车 4-公共交通", example = "1")
    private Integer transportMode;

    @Schema(description = "实际用时 (秒)", example = "900")
    private Integer actualTime;

    @Schema(description = "是否完成：0-否 1-是", example = "1")
    private Integer isCompleted;

    @Schema(description = "导航时间", example = "2024-03-25T10:00:00")
    private LocalDateTime navigatedAt;
}
