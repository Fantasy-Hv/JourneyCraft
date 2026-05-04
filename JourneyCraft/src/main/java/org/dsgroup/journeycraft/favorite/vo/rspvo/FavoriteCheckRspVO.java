package org.dsgroup.journeycraft.favorite.vo.rspvo;

import lombok.Data;

/**
 * 检查收藏状态响应 VO。
 */
@Data
public class FavoriteCheckRspVO {

    /**
     * 是否已收藏
     */
    private Boolean isFavorited;

    /**
     * 收藏记录 ID (未收藏时为 null)
     */
    private Long favoriteId;

    /**
     * 收藏夹 ID (未收藏时为 null)
     */
    private Long collectionId;
}
