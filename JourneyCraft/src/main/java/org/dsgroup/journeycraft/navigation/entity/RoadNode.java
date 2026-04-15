package org.dsgroup.journeycraft.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 路网节点实体类
 * <p>
 * 对应数据库表: t_navigation_road_node
 * 存储OSM路网节点，用于路径规划的基础数据
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_navigation_road_node")
public class RoadNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID（自增主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * OSM节点ID
     */
    @TableField("osm_id")
    private Long osmId;

    /**
     * OSM标签（JSON格式，存储原始标签）
     * 例如：{"name":"故宫博物院","tourism":"attraction"}
     */
    @TableField("osm_tags")
    private String osmTags;

    /**
     * 所属景区ID（外键，指向临时景区表）
     */
    @TableField("scenic_area_id")
    private Long scenicAreaId;

    /**
     * 所属建筑ID（外键，指向临时建筑表）
     */
    @TableField("building_id")
    private Long buildingId;

    /**
     * 关联设施ID（外键，指向临时设施表）
     */
    @TableField("facility_id")
    private Long facilityId;

    /**
     * 节点名称
     */
    @TableField("name")
    private String name;

    /**
     * 节点类型
     * 0=入口, 1=路口, 2=POI, 3=设施入口, 4=拍照点, 5=OSM普通节点
     */
    @TableField("node_type")
    private Integer nodeType;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 楼层（室内导航用，1=地面层）
     */
    @TableField("floor_number")
    private Integer floorNumber;

    /**
     * 是否重要节点
     * 0=否, 1=是（用于路径规划优化）
     */
    @TableField("`is_important`")
    private Boolean important;

    /**
     * 是否可通行
     * 0=禁用, 1=启用
     */
    @TableField("`is_accessible`")
    private Boolean accessibleFlag;

    /**
     * 是否启用
     * 0=禁用, 1=启用
     */
    @TableField("`is_enabled`")
    private Boolean enabled;

    /**
     * 是否删除（逻辑删除字段）
     * 0=否, 1=是
     * 
     * MyBatis-Plus配置: logic-delete-field=deleted
     * 但数据库字段名为is_deleted，这里使用@TableLogic注解适配
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("`is_deleted`")
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
     * 获取节点类型名称（中文描述）
     */
    public String getNodeTypeName() {
        if (nodeType == null) return "未知";
        switch (nodeType) {
            case 0: return "入口";
            case 1: return "路口";
            case 2: return "POI";
            case 3: return "设施入口";
            case 4: return "拍照点";
            case 5: return "OSM普通节点";
            default: return "未知";
        }
    }

    /**
     * 检查节点是否可用于路径规划
     * 条件：启用、可通行、未删除
     */
    public boolean isAvailableForNavigation() {
        return Boolean.TRUE.equals(enabled) && 
               Boolean.TRUE.equals(accessibleFlag) && 
               !Boolean.TRUE.equals(deleted);
    }
}