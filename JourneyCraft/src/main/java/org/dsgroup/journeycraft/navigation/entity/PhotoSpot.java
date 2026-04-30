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
 * 拍照点实体类
 * <p>
 * 对应数据库表: t_navigation_photo_spot
 * 存储景区内推荐的拍照点信息，包含拍摄建议和评分
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_navigation_photo_spot")
public class PhotoSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 拍照点ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属景区ID
     */
    @TableField("scenic_area_id")
    private Long scenicAreaId;

    /**
     * 最佳拍摄节点ID
     */
    @TableField("node_id")
    private Long nodeId;

    /**
     * 拍照点名称
     */
    @TableField("name")
    private String name;

    /**
     * 拍摄目标（如"太和殿"）
     */
    @TableField("target_name")
    private String targetName;

    /**
     * 拍摄描述
     */
    @TableField("description")
    private String description;

    /**
     * 推荐角度（如"北向45度"）
     */
    @TableField("recommended_angle")
    private String recommendedAngle;

    /**
     * 最佳时间（如"日落前1小时"）
     */
    @TableField("best_time")
    private String bestTime;

    /**
     * 最佳季节
     */
    @TableField("best_season")
    private String bestSeason;

    /**
     * 示例图片URL
     */
    @TableField("sample_image_url")
    private String sampleImageUrl;

    /**
     * 评分（1-5）
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 打卡次数
     */
    @TableField("check_in_count")
    private Integer checkInCount;

    /**
     * 推荐拍摄位置纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 推荐拍摄位置经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 是否启用
     * 0=禁用, 1=启用
     */
    @TableField("is_enabled")
    private Boolean enabled;

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
     * 检查是否有坐标信息
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    /**
     * 检查是否关联节点
     */
    public boolean hasNode() {
        return nodeId != null && nodeId > 0;
    }

    /**
     * 获取评分等级（星数）
     */
    public Integer getStarRating() {
        if (rating == null) return 0;
        return rating.intValue();
    }

    /**
     * 检查是否为热门拍照点（打卡次数大于10次）
     */
    public boolean isPopular() {
        return checkInCount != null && checkInCount > 10;
    }

    /**
     * 获取完整描述（名称+目标）
     */
    public String getFullDescription() {
        if (targetName != null && !targetName.isEmpty()) {
            return name + " - 拍摄 " + targetName;
        }
        return name;
    }

    /**
     * 检查是否可用（启用+未删除）
     */
    public boolean isAvailable() {
        return Boolean.TRUE.equals(enabled) && !Boolean.TRUE.equals(deleted);
    }
}