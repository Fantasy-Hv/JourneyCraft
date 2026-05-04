package org.dsgroup.journeycraft.favorite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏夹实体，对应用户收藏夹表。
 */
@Data
@TableName("t_collection")
public class Collection {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String collectionName;

    private LocalDateTime createdAt;
}
