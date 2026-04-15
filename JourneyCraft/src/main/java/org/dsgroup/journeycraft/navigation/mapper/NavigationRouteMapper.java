package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.navigation.entity.NavigationRoute;

/**
 * 导航路线 Mapper 接口
 * <p>
 * 对应表: t_navigation_route
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Mapper
public interface NavigationRouteMapper extends BaseMapper<NavigationRoute> {

    /**
     * 根据用户ID查询最近路线
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 路线列表
     */
    // List<NavigationRoute> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据景区ID查询路线
     *
     * @param scenicAreaId 景区ID
     * @return 路线列表
     */
    // List<NavigationRoute> selectByScenicAreaId(@Param("scenicAreaId") Long scenicAreaId);
}
