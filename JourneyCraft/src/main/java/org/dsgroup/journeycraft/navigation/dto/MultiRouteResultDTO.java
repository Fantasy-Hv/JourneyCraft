package org.dsgroup.journeycraft.navigation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 多目标路线规划结果 DTO。
 * <p>
 * 从 PathPlanningService.MultiTargetRouteResult 提取的独立 DTO，
 * 供 NavigationService API 对外暴露。
 */
@Data
public class MultiRouteResultDTO {

    /** 总距离（米） */
    private BigDecimal totalDistance;

    /** 总时间（秒） */
    private Integer totalTime;

    /** 访问顺序（节点ID列表） */
    private List<Long> visitOrder;

    /** 各段详情 */
    private List<RouteResultDTO> segments;

    /** 是否返回起点 */
    private boolean returnedToStart;
}
