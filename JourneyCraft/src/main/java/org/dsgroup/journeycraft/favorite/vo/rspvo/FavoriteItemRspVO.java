package org.dsgroup.journeycraft.favorite.vo.rspvo;

import lombok.Data;

/**
 * 收藏项响应 VO。
 */
@Data
public class FavoriteItemRspVO {

    /**
     * 收藏记录 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 收藏类型 (0:景点 1:校园 2:建筑 3:设施 4:日记 5:路线)
     * targetInfo 对应类型：
     * - 0/1: ScenicItemRspVO (景点/校园)
     * - 4: DiaryDetailRspVO (日记)
     * - 其他：暂不支持或待实现
     */
    private Integer favoriteType;

    /**
     * 收藏对象 ID
     */
    private Long favoriteId;

    /**
     * 收藏夹 ID
     */
    private Long collectionId;

    /**
     * 收藏夹名称
     */
    private String collectionName;

    /**
     * 收藏目标详情
     * 类型说明：
     * - favoriteType=0/1: ScenicItemRspVO (景点/校园)
     * - favoriteType=4: DiaryDetailRspVO (日记)
     * - 其他类型：暂不支持或待实现
     */
    private Object targetInfo;

    /**
     * 备注
     */
    private String notes;

    /**
     * 创建时间
     */
    private String createdAt;
}
