package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 建筑信息返回数据。
 */
@Data
public class BuildingRspVO {

    /** 建筑 ID。 */
    private Long id;

    /** 建筑名称。 */
    private String name;

    /** 建筑类型。 */
    private Integer type;

    /** 楼层数。 */
    private Integer floorCount;

    /** 纬度。 */
    private BigDecimal latitude;

    /** 经度。 */
    private BigDecimal longitude;

    /** 描述。 */
    private String description;

    /** 图片列表。 */
    private List<String> images;
}
