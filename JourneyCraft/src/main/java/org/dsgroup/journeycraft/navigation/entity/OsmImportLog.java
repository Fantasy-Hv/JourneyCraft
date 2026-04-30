package org.dsgroup.journeycraft.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * OSM导入日志实体类
 * <p>
 * 对应数据库表: t_navigation_osm_import_log
 * 记录OSM数据导入过程，便于监控和调试
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_navigation_osm_import_log")
public class OsmImportLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 区域（如"北京西城区"）
     */
    @TableField("region")
    private String region;

    /**
     * 数据范围[min_lon,min_lat,max_lon,max_lat]
     */
    @TableField("osm_extent")
    private String osmExtent;

    /**
     * 导入类型
     * node, way, relation
     */
    @TableField("import_type")
    private String importType;

    /**
     * 总记录数
     */
    @TableField("total_records")
    private Integer totalRecords;

    /**
     * 成功数
     */
    @TableField("success_count")
    private Integer successCount;

    /**
     * 失败数
     */
    @TableField("failed_count")
    private Integer failedCount;

    /**
     * 状态
     * processing, completed, failed
     */
    @TableField("status")
    private String status;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("started_at")
    private Date startedAt;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("finished_at")
    private Date finishedAt;

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
     * 获取状态名称
     */
    public String getStatusName() {
        if (status == null || status.isEmpty()) return "未知";
        switch (status.toLowerCase()) {
            case "processing": return "处理中";
            case "completed": return "已完成";
            case "failed": return "失败";
            default: return status;
        }
    }

    /**
     * 判断导入是否完成
     */
    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }

    /**
     * 判断导入是否失败
     */
    public boolean isFailed() {
        return "failed".equalsIgnoreCase(status);
    }

    /**
     * 判断导入是否进行中
     */
    public boolean isProcessing() {
        return "processing".equalsIgnoreCase(status);
    }

    /**
     * 计算成功率
     */
    public Double getSuccessRate() {
        if (totalRecords == null || totalRecords == 0) return 0.0;
        if (successCount == null) return 0.0;
        return successCount.doubleValue() / totalRecords * 100.0;
    }

    /**
     * 计算导入耗时（秒）
     */
    public Long getDurationSeconds() {
        if (startedAt == null || finishedAt == null) return null;
        return (finishedAt.getTime() - startedAt.getTime()) / 1000;
    }

    /**
     * 获取导入类型名称
     */
    public String getImportTypeName() {
        if (importType == null || importType.isEmpty()) return "未知";
        switch (importType.toLowerCase()) {
            case "node": return "节点";
            case "way": return "路径";
            case "relation": return "关系";
            default: return importType;
        }
    }

    /**
     * 检查是否有错误
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }

    /**
     * 获取数据范围数组 [minLon, minLat, maxLon, maxLat]
     */
    public double[] getExtentArray() {
        if (osmExtent == null || osmExtent.isEmpty()) return null;
        try {
            String[] parts = osmExtent.split(",");
            if (parts.length != 4) return null;
            double[] extent = new double[4];
            for (int i = 0; i < 4; i++) {
                extent[i] = Double.parseDouble(parts[i].trim());
            }
            return extent;
        } catch (Exception e) {
            return null;
        }
    }
}