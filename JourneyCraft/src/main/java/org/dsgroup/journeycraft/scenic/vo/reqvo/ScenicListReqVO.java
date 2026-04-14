package org.dsgroup.journeycraft.scenic.vo.reqvo;

import lombok.Data;

/**
 * 景点列表查询参数。
 */
@Data
public class ScenicListReqVO {

    private Integer type;

    private String city;

    private Integer page = 1;

    private Integer size = 10;

    private String sortBy;

    private String sortOrder;

    private Double latitude;

    private Double longitude;
}
