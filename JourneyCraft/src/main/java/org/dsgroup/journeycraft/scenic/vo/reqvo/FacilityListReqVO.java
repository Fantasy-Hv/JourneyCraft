package org.dsgroup.journeycraft.scenic.vo.reqvo;

import lombok.Data;

/**
 * 设施列表查询参数。
 */
@Data
public class FacilityListReqVO {

    private Integer type;

    private Long buildingId;
}
