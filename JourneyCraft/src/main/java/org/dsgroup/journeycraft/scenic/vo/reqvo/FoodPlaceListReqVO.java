package org.dsgroup.journeycraft.scenic.vo.reqvo;

import lombok.Data;

/**
 * 美食列表查询参数。
 */
@Data
public class FoodPlaceListReqVO {

    private Long buildingId;

    private Long nodeId;

    private String category;

    private String cuisineType;

    private String tag;
}
