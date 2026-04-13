package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 设施信息响应数据。
 */
@Data
public class FacilityRspVO {

    private Long id;

    private String name;

    private Integer type;

    private String subtype;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal rating;

    private String priceRange;

    private List<String> images;
}
