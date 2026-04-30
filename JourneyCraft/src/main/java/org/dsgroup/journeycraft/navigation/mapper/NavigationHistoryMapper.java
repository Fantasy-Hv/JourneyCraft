package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.navigation.entity.NavigationHistory;

/**
 * 导航历史 Mapper 接口
 * <p>
 * 对应表: t_navigation_history
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Mapper
public interface NavigationHistoryMapper extends BaseMapper<NavigationHistory> {

    /**
     * 根据用户ID查询导航历史
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 历史记录列表
     */
    // List<NavigationHistory> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据景区ID查询导航历史
     *
     * @param scenicAreaId 景区ID
     * @param limit 返回数量
     * @return 历史记录列表
     */
    // List<NavigationHistory> selectByScenicAreaId(@Param("scenicAreaId") Long scenicAreaId, @Param("limit") Integer limit);
}
