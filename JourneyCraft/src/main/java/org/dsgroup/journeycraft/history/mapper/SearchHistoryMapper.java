package org.dsgroup.journeycraft.history.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.history.entity.SearchHistory;

/**
 * 搜索历史数据访问接口。
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
}
