package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 建筑信息响应数据。
 */
@Data
public class BuildingRspVO {

    private Long id;

    private String name;

    private Integer type;

    private Integer floorCount;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String description;

    private List<String> images;
}
