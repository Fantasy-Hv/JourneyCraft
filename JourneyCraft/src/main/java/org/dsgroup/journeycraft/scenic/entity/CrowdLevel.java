package org.dsgroup.journeycraft.scenic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拥挤度记录实体。
 */
@Data
@TableName("t_crowd_level")
public class CrowdLevel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long scenicAreaId;

    private Long nodeId;

    private Integer level;

    private Integer crowdCount;

    private LocalDateTime recordedAt;

    private Integer source;

    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
