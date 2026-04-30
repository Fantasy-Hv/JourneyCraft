package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

import java.util.List;

/**
 * 景区实时拥挤度响应 VO。
 * <p>
 * 对应王哲（Scenic 模块）定义的 CrowdCongestionRspVO 结构。
 * Navigation 通过 ScenicService API 获取原始数据后自行聚合填充。
 */
@Data
public class CongestionRspVO {

    /** 景区ID */
    private Long scenicAreaId;

    /** 整体拥挤等级: 取所有节点中最高的 level（0-3） */
    private Integer overallLevel;

    /** 更新时间 */
    private String updateTime;

    /** 各节点拥挤度列表 */
    private List<NodeCongestionVO> nodes;
}
