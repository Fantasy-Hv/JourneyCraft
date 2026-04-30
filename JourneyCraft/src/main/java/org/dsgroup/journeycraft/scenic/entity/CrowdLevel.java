package org.dsgroup.journeycraft.scenic.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 拥挤度记录实体
 * <p>
 * 对应数据库表: t_crowd_level (Scenic模块)
 * 存储景区节点拥挤度数据，供 Navigation 4.5 接口读取
 * 
 * @author 后端智能体
 * @since 2026-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_crowd_level")
public class CrowdLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属景区ID
     */
    @TableField("scenic_area_id")
    private Long scenicAreaId;

    /**
     * 节点ID
     */
    @TableField("node_id")
    private Long nodeId;

    /**
     * 拥挤等级: 0=舒适, 1=适中, 2=拥挤, 3=非常拥挤
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
     * 数据来源: 0=传感器, 1=用户上报, 2=算法预测, 3=历史均值
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
     * 预测时间
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
     * 数据可信度 (0-1)
     */
    @TableField("confidence")
    private BigDecimal confidence;

    /**
     * 上报用户ID
     */
    @TableField("reporter_user_id")
    private Long reporterUserId;

    /**
     * 是否删除
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
     * 获取拥挤等级名称
     */
    public String getLevelName() {
        if (level == null) return "未知";
        return switch (level) {
            case 0 -> "舒适";
            case 1 -> "适中";
            case 2 -> "拥挤";
            case 3 -> "非常拥挤";
            default -> "未知";
        };
    }

    /**
     * 获取显示颜色
     */
    public String getColor() {
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
