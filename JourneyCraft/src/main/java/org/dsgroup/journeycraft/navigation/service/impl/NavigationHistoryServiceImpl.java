package org.dsgroup.journeycraft.navigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.NavigationHistory;
import org.dsgroup.journeycraft.navigation.mapper.NavigationHistoryMapper;
import org.dsgroup.journeycraft.navigation.service.NavigationHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 导航历史 Service 实现类
 *
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class NavigationHistoryServiceImpl extends ServiceImpl<NavigationHistoryMapper, NavigationHistory> 
    implements NavigationHistoryService {

    /**
     * 记录导航历史
     * <p>
     * 用途: 用户完成导航后，记录导航历史到数据库，供history模块查询
     *
     * @param history 历史记录实体
     * @return 保存成功返回历史ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveNavigationHistory(NavigationHistory history) {
        log.debug("记录导航历史: userId={}, scenicAreaId={}, startNodeId={}, endNodeId={}", 
                history.getUserId(), history.getScenicAreaId(), 
                history.getStartNodeId(), history.getEndNodeId());
        
        // 设置导航时间（如果未设置）
        if (history.getNavigatedAt() == null) {
            history.setNavigatedAt(java.time.LocalDateTime.now());
        }
        
        // 保存历史记录
        boolean saved = save(history);
        if (saved) {
            log.info("导航历史保存成功: historyId={}", history.getId());
            return history.getId();
        }
        
        log.error("导航历史保存失败");
        return null;
    }

    /**
     * 根据用户ID查询导航历史
     */
    @Override
    public List<NavigationHistory> getRecentHistory(Long userId, Integer limit) {
        log.debug("查询用户导航历史: userId={}, limit={}", userId, limit);
        return lambdaQuery()
                .eq(NavigationHistory::getUserId, userId)
                .orderByDesc(NavigationHistory::getNavigatedAt)
                .last("LIMIT " + (limit != null ? limit : 10))
                .list();
    }

    /**
     * 根据景区ID查询导航历史
     */
    @Override
    public List<NavigationHistory> getHistoryByScenicArea(Long scenicAreaId, Integer limit) {
        log.debug("查询景区导航历史: scenicAreaId={}, limit={}", scenicAreaId, limit);
        return lambdaQuery()
                .eq(NavigationHistory::getScenicAreaId, scenicAreaId)
                .orderByDesc(NavigationHistory::getNavigatedAt)
                .last("LIMIT " + (limit != null ? limit : 10))
                .list();
    }
}
