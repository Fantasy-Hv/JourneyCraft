package org.dsgroup.journeycraft.favorite.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.dsgroup.journeycraft.favorite.api.FavoriteService;
import org.dsgroup.journeycraft.favorite.entity.Collection;
import org.dsgroup.journeycraft.favorite.entity.Favorite;
import org.dsgroup.journeycraft.favorite.mapper.CollectionMapper;
import org.dsgroup.journeycraft.favorite.mapper.FavoriteMapper;
import org.dsgroup.journeycraft.favorite.vo.reqvo.CollectionCreateReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.CollectionUpdateReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteAddReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteBatchDeleteReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteCheckReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteListReqVO;
import org.dsgroup.journeycraft.favorite.vo.reqvo.FavoriteMoveReqVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.CollectionItemRspVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.CollectionListRspVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.FavoriteCheckRspVO;
import org.dsgroup.journeycraft.favorite.vo.rspvo.FavoriteItemRspVO;
import org.dsgroup.journeycraft.diary.service.DiaryService;
import org.dsgroup.journeycraft.favorite.vo.rspvo.FavoriteListRspVO;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

/**
 * 收藏服务实现类。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final CollectionMapper collectionMapper;

    private final ScenicService scenicService;

    private final DiaryService diaryService;

    private static final String DEFAULT_COLLECTION_NAME = "默认收藏夹";
    private static final int MAX_CUSTOM_COLLECTIONS = 20;
    private static final int MAX_ITEMS_PER_COLLECTION = 500;
    private static final int MAX_TOTAL_FAVORITES = 2000;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public FavoriteItemRspVO addFavorite(Long userId, FavoriteAddReqVO reqVO) {
        // 1. 检查收藏总数限制
        checkUserFavoriteLimit(userId);

        // 2. 获取或创建默认收藏夹
        Long collectionId = reqVO.getCollectionId();
        if (collectionId == null) {
            collectionId = getOrCreateDefaultCollection(userId);
        } else {
            // 验证收藏夹是否存在且属于当前用户
            Collection collection = collectionMapper.selectById(collectionId);
            if (collection == null || !collection.getUserId().equals(userId)) {
                throw new BusinessException(ResponseCodeEnum.NOT_FOUND, "收藏夹不存在");
            }
        }

        // 3. 检查收藏夹容量
        checkCollectionCapacity(collectionId);

        // 4. 检查是否已存在
        Favorite exists = favoriteMapper.selectOne(lambdaQuery(Favorite.class)
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getFavoriteType, reqVO.getFavoriteType())
                .eq(Favorite::getFavoriteId, reqVO.getFavoriteId()));

        if (exists != null) {
            throw new BusinessException(ResponseCodeEnum.DATA_ALREADY_EXIST, "该对象已在收藏夹中");
        }

        // 5. 插入收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setFavoriteType(reqVO.getFavoriteType());
        favorite.setFavoriteId(reqVO.getFavoriteId());
        favorite.setCollectionId(collectionId);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteMapper.insert(favorite);

        // 6. 返回结果
        return toFavoriteItemRsp(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long id) {
        Favorite favorite = favoriteMapper.selectById(id);
        if (favorite == null) {
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND, "收藏记录不存在");
        }
        if (!favorite.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.PERMISSION_DENIED, "无权操作该收藏");
        }
        favoriteMapper.deleteById(id);
    }

    @Override
    public FavoriteListRspVO listFavorites(Long userId, FavoriteListReqVO reqVO) {
        int page = reqVO.getPage() == null || reqVO.getPage() < 1 ? 1 : reqVO.getPage();
        int size = reqVO.getSize() == null || reqVO.getSize() < 1 ? 10 : reqVO.getSize();
        size = Math.min(size, 100);

        var wrapper = lambdaQuery(Favorite.class)
                .eq(Favorite::getUserId, userId)
                .eq(reqVO.getFavoriteType() != null, Favorite::getFavoriteType, reqVO.getFavoriteType())
                .eq(reqVO.getCollectionId() != null, Favorite::getCollectionId, reqVO.getCollectionId())
                .orderByDesc(Favorite::getCreatedAt);

        Page<Favorite> pageResult = favoriteMapper.selectPage(new Page<>(page, size), wrapper);

        List<FavoriteItemRspVO> items = pageResult.getRecords().stream()
                .map(this::toFavoriteItemRsp)
                .collect(Collectors.toList());

        FavoriteListRspVO rspVO = new FavoriteListRspVO();
        rspVO.setList(items);
        rspVO.setTotal(pageResult.getTotal());
        rspVO.setPage(page);
        rspVO.setSize(size);
        return rspVO;
    }

    @Override
    public void moveFavorite(Long userId, Long id, FavoriteMoveReqVO reqVO) {
        Favorite favorite = favoriteMapper.selectById(id);
        if (favorite == null) {
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND, "收藏记录不存在");
        }
        if (!favorite.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.PERMISSION_DENIED, "无权操作该收藏");
        }

        // 验证目标收藏夹是否存在且属于当前用户
        Collection targetCollection = collectionMapper.selectById(reqVO.getCollectionId());
        if (targetCollection == null || !targetCollection.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND, "目标收藏夹不存在");
        }

        // 检查目标收藏夹容量
        checkCollectionCapacity(reqVO.getCollectionId());

        favorite.setCollectionId(reqVO.getCollectionId());
        favoriteMapper.updateById(favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveFavorites(Long userId, FavoriteBatchDeleteReqVO reqVO) {
        // 验证所有收藏记录都属于当前用户
        List<Favorite> favorites = favoriteMapper.selectBatchIds(reqVO.getIds());
        for (Favorite favorite : favorites) {
            if (!favorite.getUserId().equals(userId)) {
                throw new BusinessException(ResponseCodeEnum.PERMISSION_DENIED, "无权删除部分收藏");
            }
        }

        favoriteMapper.deleteBatchIds(reqVO.getIds());
    }

    @Override
    public FavoriteCheckRspVO checkFavorite(Long userId, FavoriteCheckReqVO reqVO) {
        Favorite favorite = favoriteMapper.selectOne(lambdaQuery(Favorite.class)
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getFavoriteType, reqVO.getFavoriteType())
                .eq(Favorite::getFavoriteId, reqVO.getFavoriteId()));

        FavoriteCheckRspVO rspVO = new FavoriteCheckRspVO();
        if (favorite != null) {
            rspVO.setIsFavorited(true);
            rspVO.setFavoriteId(favorite.getId());
            rspVO.setCollectionId(favorite.getCollectionId());
        } else {
            rspVO.setIsFavorited(false);
        }
        return rspVO;
    }

    @Override
    public CollectionItemRspVO createCollection(Long userId, CollectionCreateReqVO reqVO) {
        // 检查收藏夹数量限制
        long customCount = collectionMapper.selectCount(lambdaQuery(Collection.class)
                .eq(Collection::getUserId, userId)
                .ne(Collection::getCollectionName, DEFAULT_COLLECTION_NAME));

        if (customCount >= MAX_CUSTOM_COLLECTIONS) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST,
                    "最多创建" + MAX_CUSTOM_COLLECTIONS + "个收藏夹");
        }

        // 检查名称是否已存在
        Collection exists = collectionMapper.selectOne(lambdaQuery(Collection.class)
                .eq(Collection::getUserId, userId)
                .eq(Collection::getCollectionName, reqVO.getCollectionName()));

        if (exists != null) {
            throw new BusinessException(ResponseCodeEnum.DATA_ALREADY_EXIST, "收藏夹名称已存在");
        }

        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setCollectionName(reqVO.getCollectionName());
        collection.setCreatedAt(LocalDateTime.now());
        collectionMapper.insert(collection);

        return toCollectionItemRsp(collection);
    }

    @Override
    public CollectionListRspVO listCollections(Long userId) {
        List<Collection> collections = collectionMapper.selectList(lambdaQuery(Collection.class)
                .eq(Collection::getUserId, userId)
                .orderByAsc(Collection::getCreatedAt));

        List<CollectionItemRspVO> result = collections.stream()
                .map(c -> {
                    CollectionItemRspVO vo = toCollectionItemRsp(c);
                    // 查询每个收藏夹的数量
                    Long count = favoriteMapper.selectCount(lambdaQuery(Favorite.class)
                            .eq(Favorite::getCollectionId, c.getId()));
                    vo.setItemCount(count.intValue());
                    return vo;
                })
                .collect(Collectors.toList());

        CollectionListRspVO rspVO = new CollectionListRspVO();
        rspVO.setCollections(result);
        return rspVO;
    }

    @Override
    public void updateCollection(Long userId, Long id, CollectionUpdateReqVO reqVO) {
        Collection collection = collectionMapper.selectById(id);
        if (collection == null) {
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND, "收藏夹不存在");
        }
        if (!collection.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.PERMISSION_DENIED, "无权操作该收藏夹");
        }
        if (DEFAULT_COLLECTION_NAME.equals(collection.getCollectionName())) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST, "不能修改默认收藏夹名称");
        }

        // 检查新名称是否与其他收藏夹重名
        Collection exists = collectionMapper.selectOne(lambdaQuery(Collection.class)
                .eq(Collection::getUserId, userId)
                .eq(Collection::getCollectionName, reqVO.getCollectionName())
                .ne(Collection::getId, id));

        if (exists != null) {
            throw new BusinessException(ResponseCodeEnum.DATA_ALREADY_EXIST, "收藏夹名称已存在");
        }

        collection.setCollectionName(reqVO.getCollectionName());
        collectionMapper.updateById(collection);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCollection(Long userId, Long id) {
        Collection collection = collectionMapper.selectById(id);
        if (collection == null) {
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND, "收藏夹不存在");
        }
        if (!collection.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.PERMISSION_DENIED, "无权操作该收藏夹");
        }
        if (DEFAULT_COLLECTION_NAME.equals(collection.getCollectionName())) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST, "不能删除默认收藏夹");
        }

        // 删除收藏夹及其所有收藏记录
        favoriteMapper.delete(lambdaQuery(Favorite.class)
                .eq(Favorite::getCollectionId, id));
        collectionMapper.deleteById(id);
    }

    // ==================== 辅助方法 ====================

    /**
     * 检查用户收藏总数限制。
     */
    private void checkUserFavoriteLimit(Long userId) {
        long totalCount = favoriteMapper.selectCount(lambdaQuery(Favorite.class)
                .eq(Favorite::getUserId, userId));
        if (totalCount >= MAX_TOTAL_FAVORITES) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST,
                    "收藏总数已达上限 (" + MAX_TOTAL_FAVORITES + ")");
        }
    }

    /**
     * 检查收藏夹容量限制。
     */
    private void checkCollectionCapacity(Long collectionId) {
        long count = favoriteMapper.selectCount(lambdaQuery(Favorite.class)
                .eq(Favorite::getCollectionId, collectionId));
        if (count >= MAX_ITEMS_PER_COLLECTION) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST,
                    "该收藏夹已达容量上限 (" + MAX_ITEMS_PER_COLLECTION + ")");
        }
    }

    /**
     * 获取或创建默认收藏夹。
     */
    private Long getOrCreateDefaultCollection(Long userId) {
        Collection collection = collectionMapper.selectOne(lambdaQuery(Collection.class)
                .eq(Collection::getUserId, userId)
                .eq(Collection::getCollectionName, DEFAULT_COLLECTION_NAME));

        if (collection != null) {
            return collection.getId();
        }

        // 创建默认收藏夹
        Collection newCollection = new Collection();
        newCollection.setUserId(userId);
        newCollection.setCollectionName(DEFAULT_COLLECTION_NAME);
        newCollection.setCreatedAt(LocalDateTime.now());
        collectionMapper.insert(newCollection);
        return newCollection.getId();
    }

    /**
     * Favorite 实体转响应 VO。
     */
    private FavoriteItemRspVO toFavoriteItemRsp(Favorite favorite) {
        FavoriteItemRspVO rspVO = new FavoriteItemRspVO();
        rspVO.setId(favorite.getId());
        rspVO.setUserId(favorite.getUserId());
        rspVO.setFavoriteType(favorite.getFavoriteType());
        rspVO.setFavoriteId(favorite.getFavoriteId());
        rspVO.setCollectionId(favorite.getCollectionId());

        // 获取收藏夹名称
        Collection collection = collectionMapper.selectById(favorite.getCollectionId());
        rspVO.setCollectionName(collection != null ? collection.getCollectionName() : "");

        // 获取目标详情 (根据类型调用对应模块 API)
        rspVO.setTargetInfo(getTargetInfo(favorite.getFavoriteType(), favorite.getFavoriteId()));

        rspVO.setCreatedAt(favorite.getCreatedAt() != null
                ? favorite.getCreatedAt().format(DATE_TIME_FORMATTER) : "");
        return rspVO;
    }

    /**
     * 根据类型获取目标详情。
     *
     * @param favoriteType 收藏类型 (0:景点 1:校园 2:建筑 3:设施 4:日记 5:路线)
     * @param favoriteId   收藏对象 ID
     * @return 目标详情对象，若类型未实现或对象不存在则返回 null
     */
    private Object getTargetInfo(Integer favoriteType, Long favoriteId) {
        if (favoriteType == null || favoriteId == null) {
            return null;
        }

        return switch (favoriteType) {
            case 0, 1 -> safeGet(() -> scenicService.getScenicDetail(favoriteId));
            case 2 -> {
                // 建筑 - TODO: 待实现建筑模块详情接口
                log.debug("建筑收藏详情待实现，favoriteId={}", favoriteId);
                yield null;
            }
            case 3 -> {
                // 设施 (含美食) - TODO: 待实现设施模块详情接口
                log.debug("设施收藏详情待实现，favoriteId={}", favoriteId);
                yield null;
            }
            case 4 -> safeGet(() -> diaryService.getDiaryDetail(favoriteId.toString()));
            case 5 -> {
                // 路线 - TODO: 待实现路线模块详情接口
                log.debug("路线收藏详情待实现，favoriteId={}", favoriteId);
                yield null;
            }
            default -> {
                log.warn("未知的收藏类型：{}", favoriteType);
                yield null;
            }
        };
    }

    /**
     * 安全获取目标详情，捕获异常避免影响列表查询。
     */
    @FunctionalInterface
    private interface SafeSupplier<T> {
        T get() throws BusinessException;
    }

    private <T> T safeGet(SafeSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (BusinessException e) {
            log.debug("获取目标详情失败 (可能已被删除):", e);
            return null;
        }
    }

    /**
     * Collection 实体转响应 VO。
     */
    private CollectionItemRspVO toCollectionItemRsp(Collection collection) {
        CollectionItemRspVO rspVO = new CollectionItemRspVO();
        rspVO.setId(collection.getId());
        rspVO.setUserId(collection.getUserId());
        rspVO.setCollectionName(collection.getCollectionName());
        rspVO.setCreatedAt(collection.getCreatedAt() != null
                ? collection.getCreatedAt().format(DATE_TIME_FORMATTER) : "");
        return rspVO;
    }
}
