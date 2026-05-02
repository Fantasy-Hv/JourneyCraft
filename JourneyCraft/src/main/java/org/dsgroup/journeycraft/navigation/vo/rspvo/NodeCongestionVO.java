package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

/**
 * 节点拥挤度 VO。
 * <p>
 * 由 Navigation 模块根据 Scenic 模块返回的拥挤度原始数据填充，
 * color 由 Navigation 根据 level 自行推导。
 */
@Data
public class NodeCongestionVO {

    /** 节点ID */
    private Long nodeId;

    /** 拥挤等级: 0=舒适, 1=适中, 2=拥挤, 3=非常拥挤 */
    private Integer level;

    /** 人数 */
    private Integer crowdCount;

    /** 显示颜色: green / yellow / orange / red */
    private String color;

    /**
     * 根据 level 推导 color。
     */
    public static String colorOf(Integer level) {
        if (level == null) return "green";
        return switch (level) {
            case 0 -> "green";
            case 1 -> "yellow";
            case 2 -> "orange";
            case 3 -> "red";
            default -> "green";
        };
    }
}
