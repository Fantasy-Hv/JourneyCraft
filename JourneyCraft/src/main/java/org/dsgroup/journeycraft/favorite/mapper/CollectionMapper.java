package org.dsgroup.journeycraft.favorite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.favorite.entity.Collection;

/**
 * 收藏夹 Mapper 接口。
 */
@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {
}
