package org.dsgroup.journeycraft.favorite.vo.rspvo;

import lombok.Data;

import java.util.List;

/**
 * 收藏列表响应 VO。
 */
@Data
public class FavoriteListRspVO {

    /**
     * 收藏列表
     */
    private List<FavoriteItemRspVO> list;

    /**
     * 总数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer size;
}
