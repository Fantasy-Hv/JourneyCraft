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

    /** 所属景区 ID。 */
    private Long scenicAreaId;

    /** 所属建筑 ID。 */
    private Long buildingId;

    /** 关联导航节点 ID。 */
    private Long nodeId;

    /** 设施名称。 */
    private String name;

    /** 设施类型。 */
    private Integer type;

    /** 子类型。 */
    private String subtype;

    /** 描述。 */
    private String description;

    /** 纬度。 */
    private BigDecimal latitude;

    /** 经度。 */
    private BigDecimal longitude;

    /** 评分。 */
    private BigDecimal rating;

    /** 评价数。 */
    private Integer reviewCount;

    /** 热度分。 */
    private Integer heatScore;

    /** 价格区间。 */
    private String priceRange;

    /** 联系方式。 */
    private String contactInfo;

    /** 标签。 */
    private List<String> tags;

    /** 图片列表。 */
    private List<String> images;
}
