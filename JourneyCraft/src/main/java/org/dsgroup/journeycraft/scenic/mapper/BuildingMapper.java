package org.dsgroup.journeycraft.scenic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.scenic.entity.Building;

/**
 * 建筑物表访问接口。
 */
@Mapper
public interface BuildingMapper extends BaseMapper<Building> {
}
