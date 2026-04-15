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
 * 拥挤度记录实体类
 * <p>
 * 对应数据库表: t_navigation_crowd_level
 * 存储节点实时拥挤度记录，支持传感器、用户上报和算法预测
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_navigation_crowd_level")
public class CrowdLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 节点ID
     */
    @TableField("node_id")
    private Long nodeId;

    /**
     * 所属景区ID（冗余字段）
     */
    @TableField("scenic_area_id")
    private Long scenicAreaId;

    /**
     * 拥挤等级
     * 0=舒适(<30%), 1=适中(30-60%), 2=拥挤(60-85%), 3=非常拥挤(>85%)
     */
    @TableField("level")
    private Integer level;

    /**
     * 实际人数
     */
    @TableField("crowd_count")
    private Integer crowdCount;

    /**
     * 容量上限
     */
    @TableField("capacity")
    private Integer capacity;

    /**
     * 密度（人/平方米）
     */
    @TableField("density")
    private BigDecimal density;

    /**
     * 数据来源
     * 0=传感器, 1=用户上报, 2=算法预测, 3=历史均值
     */
    @TableField("source")
    private Integer source;

    /**
     * 记录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("recorded_at")
    private Date recordedAt;

    /**
     * 预测时间（用于未来预测）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("predicted_at")
    private Date predictedAt;

    /**
     * 有效期至
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("valid_until")
    private Date validUntil;

    /**
     * 数据可信度（0-1）
     */
    @TableField("confidence")
    private BigDecimal confidence;

    /**
     * 上报用户ID
     */
    @TableField("reporter_user_id")
    private Long reporterUserId;

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
     * 获取拥挤等级名称
     */
    public String getLevelName() {
        if (level == null) return "未知";
        switch (level) {
            case 0: return "舒适";
            case 1: return "适中";
            case 2: return "拥挤";
            case 3: return "非常拥挤";
            default: return "未知";
        }
    }

    /**
     * 获取数据来源名称
     */
    public String getSourceName() {
        if (source == null) return "未知";
        switch (source) {
            case 0: return "传感器";
            case 1: return "用户上报";
            case 2: return "算法预测";
            case 3: return "历史均值";
            default: return "未知";
        }
    }

    /**
     * 计算拥挤百分比（占用率）
     */
    public BigDecimal getOccupancyRate() {
        if (crowdCount == null || capacity == null || capacity == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(crowdCount)
                .divide(new BigDecimal(capacity), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100));
    }

    /**
     * 判断是否有效（未过期）
     */
    public boolean isValid() {
        if (validUntil == null) {
            return true; // 没有有效期限制，视为有效
        }
        return validUntil.after(new Date());
    }

    /**
     * 判断是否为预测数据
     */
    public boolean isPredicted() {
        return source != null && source == 2;
    }

    /**
     * 获取实际时间（recorded_at 或 predicted_at）
     */
    public Date getActualTime() {
        return recordedAt != null ? recordedAt : predictedAt;
    }
}