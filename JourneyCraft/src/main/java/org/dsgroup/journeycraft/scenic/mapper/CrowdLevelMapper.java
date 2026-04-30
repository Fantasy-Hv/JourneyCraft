package org.dsgroup.journeycraft.scenic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.scenic.entity.CrowdLevel;

/**
 * 拥挤度记录表访问接口。
 */
@Mapper
public interface CrowdLevelMapper extends BaseMapper<CrowdLevel> {
}
