package org.dsgroup.journeycraft.scenic.vo.reqvo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 拥挤度上报参数。
 */
@Data
public class ReportCrowdReqVO {

    @NotNull(message = "nodeId不能为空")
    private Long nodeId;

    @NotNull(message = "level不能为空")
    private Integer level;

    private Integer crowdCount;
}
