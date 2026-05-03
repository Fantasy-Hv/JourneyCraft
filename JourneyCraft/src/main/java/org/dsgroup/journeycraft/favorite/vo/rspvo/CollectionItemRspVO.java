package org.dsgroup.journeycraft.favorite.vo.rspvo;

import lombok.Data;

/**
 * 收藏夹项响应 VO。
 */
@Data
public class CollectionItemRspVO {

    /**
     * 收藏夹 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 收藏夹名称
     */
    private String collectionName;

    /**
     * 收藏数量
     */
    private Integer itemCount;

    /**
     * 创建时间
     */
    private String createdAt;
}
