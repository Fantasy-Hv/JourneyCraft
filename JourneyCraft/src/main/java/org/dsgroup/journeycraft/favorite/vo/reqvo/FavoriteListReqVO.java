package org.dsgroup.journeycraft.favorite.vo.reqvo;

import lombok.Data;

/**
 * 收藏列表查询请求 VO。
 */
@Data
public class FavoriteListReqVO {

    /**
     * 收藏类型过滤 (0:景点 1:校园 2:建筑 (预留) 3:设施 (预留) 4:日记 5:路线 (预留))
     */
    private Integer favoriteType;

    /**
     * 收藏夹 ID 过滤
     */
    private Long collectionId;

    /**
     * 页码 (默认 1)
     */
    private Integer page = 1;

    /**
     * 每页数量 (默认 10)
     */
    private Integer size = 10;
}
