package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 附近设施返回结果
 * <p>
 * 用于 4.3 获取附近设施接口
 * 
 * @author 后端智能体
 * @since 2026-04-22
 */
@Data
public class NearbyFacilityRspVO {

    /** 设施 ID */
    private Long id;

    /** 设施名称 */
    private String name;

    /** 设施类型 */
    private Integer type;

    /** 纬度 */
    private BigDecimal latitude;

    /** 经度 */
    private BigDecimal longitude;

    /** 实际路径距离（米） */
    private BigDecimal distance;
}
