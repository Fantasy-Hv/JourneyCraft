package org.dsgroup.journeycraft.history.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.history.entity.ViewHistory;

/**
 * 浏览历史数据访问接口。
 */
@Mapper
public interface ViewHistoryMapper extends BaseMapper<ViewHistory> {
}
