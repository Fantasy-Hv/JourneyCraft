package org.dsgroup.journeycraft.navigation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dsgroup.journeycraft.navigation.entity.NavigationHistory;

/**
 * 导航历史 Service 接口
 * <p>
 * 对应表: t_navigation_history
 * 用途: 记录用户的历史导航记录，供history模块查询
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
public interface NavigationHistoryService extends IService<NavigationHistory> {

    /**
     * 记录导航历史
     *
     * @param history 历史记录实体
     * @return 保存成功返回历史ID
     */
    Long saveNavigationHistory(NavigationHistory history);

    /**
     * 根据用户ID查询导航历史
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 历史记录列表
     */
    java.util.List<NavigationHistory> getRecentHistory(Long userId, Integer limit);

    /**
     * 根据景区ID查询导航历史
     *
     * @param scenicAreaId 景区ID
     * @param limit 返回数量
     * @return 历史记录列表
     */
    java.util.List<NavigationHistory> getHistoryByScenicArea(Long scenicAreaId, Integer limit);
}
