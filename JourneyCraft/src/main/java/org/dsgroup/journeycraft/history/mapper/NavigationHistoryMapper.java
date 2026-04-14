package org.dsgroup.journeycraft.history.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.history.entity.NavigationHistory;

/**
 * 导航历史数据访问接口。
 */
@Mapper
public interface NavigationHistoryMapper extends BaseMapper<NavigationHistory> {
}
