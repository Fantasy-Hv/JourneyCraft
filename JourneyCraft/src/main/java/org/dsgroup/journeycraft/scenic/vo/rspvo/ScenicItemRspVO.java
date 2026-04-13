package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 景点详情返回数据。
 */
@Data
public class ScenicItemRspVO {

    /** 景点 ID。 */
    private Long id;

    /** 景点名称。 */
    private String name;

    /** 景点类型。 */
    private Integer type;

    /** 城市。 */
    private String city;

    /** 详细地址。 */
    private String address;

    /** 纬度。 */
    private BigDecimal latitude;

    /** 经度。 */
    private BigDecimal longitude;

    /** 描述。 */
    private String description;

    /** 评分。 */
    private BigDecimal rating;

    /** 热度分。 */
    private Integer heatScore;

    /** 浏览数。 */
    private Integer visitCount;

    /** 门票价格。 */
    private BigDecimal ticketPrice;

    /** 开放时间。 */
    private Map<String, Map<String, String>> openingHours;

    /** 图片列表。 */
    private List<String> images;
}
