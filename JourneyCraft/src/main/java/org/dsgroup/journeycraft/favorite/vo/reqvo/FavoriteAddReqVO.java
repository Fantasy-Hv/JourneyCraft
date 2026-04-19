package org.dsgroup.journeycraft.favorite.vo.reqvo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加收藏请求 VO。
 */
@Data
public class FavoriteAddReqVO {

    /**
     * 收藏类型 (0:景点 1:校园 2:建筑 (预留) 3:设施 (预留) 4:日记 5:路线 (预留))
     */
    @NotNull(message = "收藏类型必填")
    private Integer favoriteType;

    /**
     * 收藏对象 ID
     */
    @NotNull(message = "收藏对象 ID 必填")
    private Long favoriteId;

    /**
     * 收藏夹 ID (不传则添加到默认收藏夹)
     */
    private Long collectionId;
}
