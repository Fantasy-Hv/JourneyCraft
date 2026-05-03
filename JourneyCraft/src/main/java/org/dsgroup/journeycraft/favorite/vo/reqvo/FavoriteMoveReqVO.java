package org.dsgroup.journeycraft.favorite.vo.reqvo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 移动收藏请求 VO。
 */
@Data
public class FavoriteMoveReqVO {

    /**
     * 目标收藏夹 ID
     */
    @NotNull(message = "目标收藏夹 ID 必填")
    private Long collectionId;
}
