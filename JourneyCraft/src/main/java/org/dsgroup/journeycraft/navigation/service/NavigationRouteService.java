package org.dsgroup.journeycraft.navigation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dsgroup.journeycraft.navigation.entity.NavigationRoute;

/**
 * 导航路线 Service 接口
 * <p>
 * 对应表: t_navigation_route
 * 用途: 存储用户规划的路线，供接口4.1/4.2返回routeId
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
public interface NavigationRouteService extends IService<NavigationRoute> {

    /**
     * 保存路线规划结果
     *
     * @param route 路线实体
     * @return 保存成功返回路线ID
     */
    Long saveRoute(NavigationRoute route);

    /**
     * 根据用户ID查询最近路线
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 路线列表
     */
    java.util.List<NavigationRoute> getRecentRoutes(Long userId, Integer limit);

    /**
     * 根据景区ID查询路线
     *
     * @param scenicAreaId 景区ID
     * @return 路线列表
     */
    java.util.List<NavigationRoute> getRoutesByScenicArea(Long scenicAreaId);
}
