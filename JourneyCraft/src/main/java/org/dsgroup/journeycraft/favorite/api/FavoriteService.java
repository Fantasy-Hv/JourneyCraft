package org.dsgroup.journeycraft.favorite.api;

import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteAddReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteBatchDeleteReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteCheckReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteListReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteMoveReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.CollectionCreateReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.CollectionUpdateReqVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.CollectionItemRspVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.CollectionListRspVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.FavoriteCheckRspVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.FavoriteItemRspVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.FavoriteListRspVO;

/**
 * 收藏服务接口。
 */
public interface FavoriteService {

    // ==================== 收藏管理 ====================

    /**
     * 添加收藏。
     *
     * @param userId 用户 ID
     * @param reqVO  请求参数
     * @return 收藏项信息
     */
    FavoriteItemRspVO addFavorite(Long userId, FavoriteAddReqVO reqVO);

    /**
     * 取消收藏。
     *
     * @param userId 用户 ID
     * @param id     收藏记录 ID
     */
    void removeFavorite(Long userId, Long id);

    /**
     * 获取收藏列表。
     *
     * @param userId 用户 ID
     * @param reqVO  查询参数
     * @return 收藏列表
     */
    FavoriteListRspVO listFavorites(Long userId, FavoriteListReqVO reqVO);

    /**
     * 移动收藏到指定收藏夹。
     *
     * @param userId 用户 ID
     * @param id     收藏记录 ID
     * @param reqVO  请求参数
     */
    void moveFavorite(Long userId, Long id, FavoriteMoveReqVO reqVO);

    /**
     * 批量取消收藏。
     *
     * @param userId 用户 ID
     * @param reqVO  请求参数
     */
    void batchRemoveFavorites(Long userId, FavoriteBatchDeleteReqVO reqVO);

    /**
     * 检查是否已收藏。
     *
     * @param userId 用户 ID
     * @param reqVO  请求参数
     * @return 收藏状态
     */
    FavoriteCheckRspVO checkFavorite(Long userId, FavoriteCheckReqVO reqVO);

    // ==================== 收藏夹管理 ====================

    /**
     * 创建收藏夹。
     *
     * @param userId 用户 ID
     * @param reqVO  请求参数
     * @return 收藏夹信息
     */
    CollectionItemRspVO createCollection(Long userId, CollectionCreateReqVO reqVO);

    /**
     * 获取收藏夹列表。
     *
     * @param userId 用户 ID
     * @return 收藏夹列表
     */
    CollectionListRspVO listCollections(Long userId);

    /**
     * 更新收藏夹名称。
     *
     * @param userId 用户 ID
     * @param id     收藏夹 ID
     * @param reqVO  请求参数
     */
    void updateCollection(Long userId, Long id, CollectionUpdateReqVO reqVO);

    /**
     * 删除收藏夹。
     *
     * @param userId 用户 ID
     * @param id     收藏夹 ID
     */
    void deleteCollection(Long userId, Long id);
}
