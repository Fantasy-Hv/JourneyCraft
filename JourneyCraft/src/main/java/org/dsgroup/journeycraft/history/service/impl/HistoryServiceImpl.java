package org.dsgroup.journeycraft.history.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.history.entity.NavigationHistory;
import org.dsgroup.journeycraft.history.entity.SearchHistory;
import org.dsgroup.journeycraft.history.entity.ViewHistory;
import org.dsgroup.journeycraft.history.mapper.NavigationHistoryMapper;
import org.dsgroup.journeycraft.history.mapper.SearchHistoryMapper;
import org.dsgroup.journeycraft.history.mapper.ViewHistoryMapper;
import org.dsgroup.journeycraft.history.service.HistoryService;
import org.dsgroup.journeycraft.history.vo.reqvo.RecordViewReqVO;
import org.dsgroup.journeycraft.history.vo.rspvo.NavigationHistoryRspVO;
import org.dsgroup.journeycraft.history.vo.rspvo.SearchHistoryRspVO;
import org.dsgroup.journeycraft.history.vo.rspvo.ViewHistoryRspVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 历史记录服务实现
 */
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final ViewHistoryMapper viewHistoryMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final NavigationHistoryMapper navigationHistoryMapper;

    /**
     * 查询浏览历史
     */
    @Override
    public List<ViewHistoryRspVO> getViewHistory(Long userId, Integer type, Integer page, Integer size) {
        // 默认参数
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;
        if (size > 100) size = 100;

        // 构建查询条件
        LambdaQueryWrapper<ViewHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ViewHistory::getUserId, userId)
                .eq(ViewHistory::getIsDeleted, 0)
                .eq(type != null, ViewHistory::getTargetType, type)
                .orderByDesc(ViewHistory::getViewTime);

        // 分页查询
        Page<ViewHistory> pageResult = viewHistoryMapper.selectPage(
                new Page<>(page, size),
                wrapper
        );

        // 转换为 VO
        return pageResult.getRecords().stream()
                .map(this::convertToViewHistoryRspVO)
                .collect(Collectors.toList());
    }

    /**
     * 查询搜索历史
     */
    @Override
    public List<SearchHistoryRspVO> getSearchHistory(Long userId, Integer type, Integer limit) {
        // 默认参数
        if (limit == null || limit < 1) limit = 10;
        if (limit > 100) limit = 100;

        // 构建查询条件
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
                .eq(SearchHistory::getIsDeleted, 0)
                .eq(type != null, SearchHistory::getSearchType, type)
                .orderByDesc(SearchHistory::getSearchedAt);

        // 查询最近 limit 条
        Page<SearchHistory> pageResult = searchHistoryMapper.selectPage(
                new Page<>(1, limit),
                wrapper
        );

        // 转换为 VO
        return pageResult.getRecords().stream()
                .map(this::convertToSearchHistoryRspVO)
                .collect(Collectors.toList());
    }

    /**
     * 查询导航历史
     */
    @Override
    public List<NavigationHistoryRspVO> getNavigationHistory(Long userId, Long scenicAreaId, Integer page, Integer size) {
        // 默认参数
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;
        if (size > 100) size = 100;

        // 构建查询条件
        LambdaQueryWrapper<NavigationHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NavigationHistory::getUserId, userId)
                .eq(NavigationHistory::getIsDeleted, 0)
                .eq(scenicAreaId != null, NavigationHistory::getScenicAreaId, scenicAreaId)
                .orderByDesc(NavigationHistory::getNavigatedAt);

        // 分页查询
        Page<NavigationHistory> pageResult = navigationHistoryMapper.selectPage(
                new Page<>(page, size),
                wrapper
        );

        // 转换为 VO
        return pageResult.getRecords().stream()
                .map(this::convertToNavigationHistoryRspVO)
                .collect(Collectors.toList());
    }

    /**
     * 记录浏览历史
     */
    @Override
    public void recordViewHistory(Long userId, RecordViewReqVO reqVO) {
        ViewHistory viewHistory = new ViewHistory();
        viewHistory.setUserId(userId);
        viewHistory.setTargetType(reqVO.getTargetType());
        viewHistory.setTargetId(reqVO.getTargetId());
        viewHistory.setTargetName(reqVO.getTargetName());
        viewHistory.setViewDuration(reqVO.getViewDuration() != null ? reqVO.getViewDuration() : 0);
        viewHistory.setViewTime(LocalDateTime.now());
        viewHistory.setIsDeleted(0);
        viewHistory.setCreatedAt(LocalDateTime.now());
        viewHistory.setUpdatedAt(LocalDateTime.now());

        viewHistoryMapper.insert(viewHistory);
    }

    /**
     * 清空历史记录
     */
    @Override
    public void clearHistory(Long userId, String type) {
        if (type == null || type.isEmpty()) {
            // 清空全部类型历史
            clearViewHistory(userId);
            clearSearchHistory(userId);
            clearNavigationHistory(userId);
        } else {
            switch (type) {
                case "view":
                    clearViewHistory(userId);
                    break;
                case "search":
                    clearSearchHistory(userId);
                    break;
                case "navigation":
                    clearNavigationHistory(userId);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的历史类型：" + type);
            }
        }
    }

    /**
     * 清空浏览历史
     */
    private void clearViewHistory(Long userId) {
        LambdaQueryWrapper<ViewHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ViewHistory::getUserId, userId);
        viewHistoryMapper.delete(wrapper);
    }

    /**
     * 清空搜索历史
     */
    private void clearSearchHistory(Long userId) {
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        searchHistoryMapper.delete(wrapper);
    }

    /**
     * 清空导航历史
     */
    private void clearNavigationHistory(Long userId) {
        LambdaQueryWrapper<NavigationHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NavigationHistory::getUserId, userId);
        navigationHistoryMapper.delete(wrapper);
    }

    /**
     * 将 ViewHistory 实体转换为 VO
     */
    private ViewHistoryRspVO convertToViewHistoryRspVO(ViewHistory viewHistory) {
        return ViewHistoryRspVO.builder()
                .id(viewHistory.getId())
                .targetType(viewHistory.getTargetType())
                .targetId(viewHistory.getTargetId())
                .targetName(viewHistory.getTargetName())
                .viewDuration(viewHistory.getViewDuration())
                .viewTime(viewHistory.getViewTime())
                .build();
    }

    /**
     * 将 SearchHistory 实体转换为 VO
     */
    private SearchHistoryRspVO convertToSearchHistoryRspVO(SearchHistory searchHistory) {
        return SearchHistoryRspVO.builder()
                .id(searchHistory.getId())
                .keyword(searchHistory.getKeyword())
                .searchType(searchHistory.getSearchType())
                .resultCount(searchHistory.getResultCount())
                .clickedId(searchHistory.getClickedId())
                .searchedAt(searchHistory.getSearchedAt())
                .build();
    }

    /**
     * 将 NavigationHistory 实体转换为 VO
     */
    private NavigationHistoryRspVO convertToNavigationHistoryRspVO(NavigationHistory navigationHistory) {
        return NavigationHistoryRspVO.builder()
                .id(navigationHistory.getId())
                .scenicAreaId(navigationHistory.getScenicAreaId())
                .startNodeId(navigationHistory.getStartNodeId())
                .endNodeId(navigationHistory.getEndNodeId())
                .transportMode(navigationHistory.getTransportMode())
                .actualTime(navigationHistory.getActualTime())
                .isCompleted(navigationHistory.getIsCompleted())
                .navigatedAt(navigationHistory.getNavigatedAt())
                .build();
    }
}
