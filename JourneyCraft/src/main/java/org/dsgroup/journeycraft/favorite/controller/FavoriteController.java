package org.dsgroup.journeycraft.favorite.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.favorite.api.FavoriteService;
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
import org.dsgroup.journeycraft.favorite.vo.rspvo.FavoriteListRspVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收藏模块控制器。
 */
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
@Tag(name = "收藏管理", description = "收藏记录和收藏夹相关接口")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 添加收藏。
     */
    @PostMapping
    @Operation(summary = "添加收藏", description = "添加一个收藏对象到用户的收藏夹中")
    public Response<FavoriteItemRspVO> addFavorite(
            @RequestBody @Valid FavoriteAddReqVO reqVO,
            @RequestAttribute("currentUserId") Long userId) {
        return Response.ok(favoriteService.addFavorite(userId, reqVO));
    }

    /**
     * 取消收藏。
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "取消收藏", description = "删除一条收藏记录")
    public Response<Void> removeFavorite(
            @PathVariable Long id,
            @RequestAttribute("currentUserId") Long userId) {
        favoriteService.removeFavorite(userId, id);
        return Response.ok();
    }

    /**
     * 获取收藏列表。
     */
    @GetMapping("/list")
    @Operation(summary = "获取收藏列表", description = "查询用户的收藏列表，支持按类型和收藏夹过滤")
    public Response<FavoriteListRspVO> listFavorites(
            FavoriteListReqVO reqVO,
            @RequestAttribute("currentUserId") Long userId) {
        return Response.ok(favoriteService.listFavorites(userId, reqVO));
    }

    /**
     * 移动收藏到指定收藏夹。
     */
    @PutMapping("/{id}/move")
    @Operation(summary = "移动收藏", description = "将收藏记录移动到另一个收藏夹")
    public Response<Void> moveFavorite(
            @PathVariable Long id,
            @RequestBody @Valid FavoriteMoveReqVO reqVO,
            @RequestAttribute("currentUserId") Long userId) {
        favoriteService.moveFavorite(userId, id, reqVO);
        return Response.ok();
    }

    /**
     * 批量取消收藏。
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量取消收藏", description = "批量删除多条收藏记录")
    public Response<Void> batchRemoveFavorites(
            @RequestBody @Valid FavoriteBatchDeleteReqVO reqVO,
            @RequestAttribute("currentUserId") Long userId) {
        favoriteService.batchRemoveFavorites(userId, reqVO);
        return Response.ok();
    }

    /**
     * 检查是否已收藏。
     */
    @GetMapping("/check")
    @Operation(summary = "检查收藏状态", description = "检查用户是否已收藏指定对象")
    public Response<FavoriteCheckRspVO> checkFavorite(
            @Valid FavoriteCheckReqVO reqVO,
            @RequestAttribute("currentUserId") Long userId) {
        return Response.ok(favoriteService.checkFavorite(userId, reqVO));
    }

    /**
     * 创建收藏夹。
     */
    @PostMapping("/collection")
    @Operation(summary = "创建收藏夹", description = "创建一个新的自定义收藏夹")
    public Response<CollectionItemRspVO> createCollection(
            @RequestBody @Valid CollectionCreateReqVO reqVO,
            @RequestAttribute("currentUserId") Long userId) {
        return Response.ok(favoriteService.createCollection(userId, reqVO));
    }

    /**
     * 获取收藏夹列表。
     */
    @GetMapping("/collections")
    @Operation(summary = "获取收藏夹列表", description = "查询用户的所有收藏夹列表")
    public Response<CollectionListRspVO> listCollections(
            @RequestAttribute("currentUserId") Long userId) {
        return Response.ok(favoriteService.listCollections(userId));
    }

    /**
     * 更新收藏夹名称。
     */
    @PutMapping("/collection/{id}")
    @Operation(summary = "更新收藏夹", description = "修改收藏夹名称（默认收藏夹不可修改）")
    public Response<Void> updateCollection(
            @PathVariable Long id,
            @RequestBody @Valid CollectionUpdateReqVO reqVO,
            @RequestAttribute("currentUserId") Long userId) {
        favoriteService.updateCollection(userId, id, reqVO);
        return Response.ok();
    }

    /**
     * 删除收藏夹。
     */
    @DeleteMapping("/collection/{id}")
    @Operation(summary = "删除收藏夹", description = "删除收藏夹及其中的所有收藏记录（默认收藏夹不可删除）")
    public Response<Void> deleteCollection(
            @PathVariable Long id,
            @RequestAttribute("currentUserId") Long userId) {
        favoriteService.deleteCollection(userId, id);
        return Response.ok();
    }
}
