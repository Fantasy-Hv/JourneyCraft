package org.dsgroup.journeycraft.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 室内楼层实体类
 * <p>
 * 对应数据库表: t_navigation_indoor_floor
 * 存储建筑楼层信息，支持室内导航
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_navigation_indoor_floor")
public class IndoorFloor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 楼层ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 建筑ID
     */
    @TableField("building_id")
    private Long buildingId;

    /**
     * 楼层号
     * -1=地下室, 0=地面, 1=一层...
     */
    @TableField("floor_number")
    private Integer floorNumber;

    /**
     * 楼层名称（如"一层大厅"）
     */
    @TableField("floor_name")
    private String floorName;

    /**
     * 楼层地图URL
     */
    @TableField("map_image_url")
    private String mapImageUrl;

    /**
     * 地图尺寸（JSON格式）
     * 例如：{"width":100,"height":100,"scale":0.5}
     */
    @TableField("map_dimensions")
    private String mapDimensions;

    /**
     * 电梯节点ID
     */
    @TableField("elevator_node_id")
    private Long elevatorNodeId;

    /**
     * 楼梯节点ID
     */
    @TableField("stair_node_id")
    private Long stairNodeId;

    /**
     * 入口节点ID列表（JSON数组）
     * 例如：[101, 102, 103]
     */
    @TableField("entrance_node_ids")
    private String entranceNodeIds;

    /**
     * 是否删除（逻辑删除字段）
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Boolean deleted;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /**
     * 获取楼层显示名称
     */
    public String getDisplayName() {
        if (floorName != null && !floorName.isEmpty()) {
            return floorName;
        }
        if (floorNumber == null) return "未知楼层";
        
        switch (floorNumber) {
            case -1: return "地下室";
            case 0: return "地面层";
            case 1: return "一层";
            case 2: return "二层";
            case 3: return "三层";
            case 4: return "四层";
            case 5: return "五层";
            default: return floorNumber + "层";
        }
    }

    /**
     * 检查是否有地图
     */
    public boolean hasMap() {
        return mapImageUrl != null && !mapImageUrl.isEmpty();
    }

    /**
     * 检查是否有电梯
     */
    public boolean hasElevator() {
        return elevatorNodeId != null && elevatorNodeId > 0;
    }

    /**
     * 检查是否有楼梯
     */
    public boolean hasStair() {
        return stairNodeId != null && stairNodeId > 0;
    }

    /**
     * 检查是否为地面层
     */
    public boolean isGroundFloor() {
        return floorNumber != null && floorNumber == 0;
    }

    /**
     * 检查是否为地下室
     */
    public boolean isBasement() {
        return floorNumber != null && floorNumber < 0;
    }

    /**
     * 检查是否为高层（3层及以上）
     */
    public boolean isHighFloor() {
        return floorNumber != null && floorNumber >= 3;
    }

    /**
     * 获取入口节点数量
     */
    public Integer getEntranceCount() {
        if (entranceNodeIds == null || entranceNodeIds.isEmpty()) {
            return 0;
        }
        try {
            // 简单统计JSON数组中的元素数量
            String clean = entranceNodeIds.trim();
            if (clean.startsWith("[") && clean.endsWith("]")) {
                String content = clean.substring(1, clean.length() - 1).trim();
                if (content.isEmpty()) return 0;
                return content.split(",").length;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取完整的楼层标识（建筑ID-楼层号）
     */
    public String getFloorIdentifier() {
        return buildingId + "-F" + floorNumber;
    }

    /**
     * 检查是否支持垂直交通（电梯或楼梯）
     */
    public boolean hasVerticalTransport() {
        return hasElevator() || hasStair();
    }

    /**
     * 获取楼层类型描述
     */
    public String getFloorType() {
        if (floorNumber == null) return "未知";
        if (floorNumber < 0) return "地下室";
        if (floorNumber == 0) return "地面层";
        if (floorNumber <= 2) return "低层";
        if (floorNumber <= 5) return "中层";
        return "高层";
    }
}