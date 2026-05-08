package org.dsgroup.journeycraft.navigation.service;

import org.dsgroup.journeycraft.navigation.vo.rspvo.SearchResultVO;

/**
 * 统一搜索服务接口
 */
public interface SearchService {

    /**
     * 统一搜索：按关键词同时检索景点和路网节点
     *
     * @param keyword 搜索关键词，为空时仅搜索景点
     * @param types   搜索类型，逗号分隔（scenic, node）
     * @param limit   返回数量限制
     * @param offset  偏移量
     * @return 搜索结果
     */
    SearchResultVO search(String keyword, String types, int limit, int offset);
}
