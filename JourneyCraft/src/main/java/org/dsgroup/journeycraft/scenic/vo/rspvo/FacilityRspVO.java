package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 设施信息返回数据。
 */
@Data
public class FacilityRspVO {

    /** 设施 ID。 */
    private Long id;

    /** 设施名称。 */
    private String name;

    /** 设施类型。 */
    private Integer type;

    /** 子类型。 */
    private String subtype;

    /** 纬度。 */
    private BigDecimal latitude;

    /** 经度。 */
    private BigDecimal longitude;

    /** 评分。 */
    private BigDecimal rating;

    /** 价格区间。 */
    private String priceRange;

    /** 图片列表。 */
    private List<String> images;
}
