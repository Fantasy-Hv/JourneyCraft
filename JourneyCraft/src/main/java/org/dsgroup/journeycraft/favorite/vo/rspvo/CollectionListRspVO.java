package org.dsgroup.journeycraft.favorite.vo.rspvo;

import lombok.Data;

import java.util.List;

/**
 * 收藏夹列表响应 VO。
 */
@Data
public class CollectionListRspVO {

    /**
     * 收藏夹列表
     */
    private List<CollectionItemRspVO> collections;
}
