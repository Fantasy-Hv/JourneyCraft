package org.dsgroup.journeycraft.navigation.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 路径节点 DTO。
 * <p>
 * 从 PathPlanningService.PathNode 提取的独立 DTO，
 * 供 NavigationService API 对外暴露，避免依赖内部 service 类型。
 */
@Data
public class PathNodeDTO {

    /** 节点ID */
    private Long nodeId;

    /** 节点名称 */
    private String name;

    /** 顺序号（0 起点） */
    private Integer sequence;

    /** WGS-84 纬度 */
    private BigDecimal latitude;

    /** WGS-84 经度 */
    private BigDecimal longitude;

    /** 动作: start / visit / end */
    private String action;

    /** 到达时间（预留，当前为 null） */
    private String arrivalTime;
}
