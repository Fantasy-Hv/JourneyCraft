package org.dsgroup.journeycraft.navigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.NavigationRoute;
import org.dsgroup.journeycraft.navigation.mapper.NavigationRouteMapper;
import org.dsgroup.journeycraft.navigation.service.NavigationRouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 导航路线 Service 实现类
 *
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class NavigationRouteServiceImpl extends ServiceImpl<NavigationRouteMapper, NavigationRoute> 
    implements NavigationRouteService {

    /**
     * 保存路线规划结果
     * <p>
     * 用途: 路径规划完成后，保存路线到数据库，返回routeId供接口使用
     *
     * @param route 路线实体
     * @return 保存成功返回路线ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveRoute(NavigationRoute route) {
        log.debug("保存路线: scenicAreaId={}, startNodeId={}, totalDistance={}", 
                route.getScenicAreaId(), route.getStartNodeId(), route.getTotalDistance());
        
        // 设置创建时间（如果未设置）
        if (route.getCreatedAt() == null) {
            route.setCreatedAt(java.time.LocalDateTime.now());
        }
        
        // 保存路线
        boolean saved = save(route);
        if (saved) {
            log.info("路线保存成功: routeId={}", route.getId());
            return route.getId();
        }
        
        log.error("路线保存失败");
        return null;
    }

    /**
     * 根据用户ID查询最近路线
     */
    @Override
    public List<NavigationRoute> getRecentRoutes(Long userId, Integer limit) {
        log.debug("查询用户最近路线: userId={}, limit={}", userId, limit);
        return lambdaQuery()
                .eq(NavigationRoute::getUserId, userId)
                .orderByDesc(NavigationRoute::getCreatedAt)
                .last("LIMIT " + (limit != null ? limit : 10))
                .list();
    }

    /**
     * 根据景区ID查询路线
     */
    @Override
    public List<NavigationRoute> getRoutesByScenicArea(Long scenicAreaId) {
        log.debug("查询景区路线: scenicAreaId={}", scenicAreaId);
        return lambdaQuery()
                .eq(NavigationRoute::getScenicAreaId, scenicAreaId)
                .orderByDesc(NavigationRoute::getCreatedAt)
                .list();
    }
}
