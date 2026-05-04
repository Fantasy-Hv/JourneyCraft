package org.dsgroup.journeycraft.history.service;

import org.dsgroup.journeycraft.history.vo.reqvo.RecordViewReqVO;
import org.dsgroup.journeycraft.history.vo.rspvo.NavigationHistoryRspVO;
import org.dsgroup.journeycraft.history.vo.rspvo.SearchHistoryRspVO;
import org.dsgroup.journeycraft.history.vo.rspvo.ViewHistoryRspVO;

import java.util.List;

/**
 * 历史记录服务接口
 */
public interface HistoryService {

    /**
     * 查询浏览历史
     * @param userId 用户 ID
     * @param type 目标类型（可选）
     * @param page 页码
     * @param size 每页数量
     * @return 浏览历史列表
     */
    List<ViewHistoryRspVO> getViewHistory(Long userId, Integer type, Integer page, Integer size);

    /**
     * 查询搜索历史
     * @param userId 用户 ID
     * @param type 搜索类型（可选）
     * @param limit 返回数量
     * @return 搜索历史列表
     */
    List<SearchHistoryRspVO> getSearchHistory(Long userId, Integer type, Integer limit);

    /**
     * 查询导航历史
     * @param userId 用户 ID
     * @param scenicAreaId 景区 ID（可选）
     * @param page 页码
     * @param size 每页数量
     * @return 导航历史列表
     */
    List<NavigationHistoryRspVO> getNavigationHistory(Long userId, Long scenicAreaId, Integer page, Integer size);

    /**
     * 记录浏览历史
     * @param userId 用户 ID
     * @param reqVO 请求参数
     */
    void recordViewHistory(Long userId, RecordViewReqVO reqVO);

    /**
     * 清空历史记录
     * @param userId 用户 ID
     * @param type 历史类型：view/search/navigation，null 则清空全部
     */
    void clearHistory(Long userId, String type);
}
