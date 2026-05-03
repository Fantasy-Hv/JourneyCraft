package org.dsgroup.journeycraft.navigation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 路径规划结果 DTO。
 * <p>
 * 从 PathPlanningService.PathPlanningResult 提取的独立 DTO，
 * 供 NavigationService API 对外暴露，避免依赖内部 service 类型。
 */
@Data
public class RouteResultDTO {

    /** 路线ID */
    private Long routeId;

    /** 总距离（米） */
    private BigDecimal totalDistance;

    /** 预计时间（秒） */
    private Integer estimatedTime;

    /** 交通方式 */
    private String transportMode;

    /** 路径节点列表 */
    private List<PathNodeDTO> nodes;

    /** 规划策略 */
    private String strategy;
}
