package org.dsgroup.journeycraft.favorite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏记录实体，对应用户收藏关系表。
 */
@Data
@TableName("t_favorite")
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 收藏类型：0-景点 1-校园 2-建筑 (预留) 3-设施 (预留) 4-日记 5-路线 (预留)
     */
    private Integer favoriteType;

    private Long favoriteId;

    private Long collectionId;

    private LocalDateTime createdAt;
}
