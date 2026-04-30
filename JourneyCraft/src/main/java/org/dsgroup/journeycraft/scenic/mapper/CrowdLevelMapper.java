package org.dsgroup.journeycraft.scenic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.scenic.entity.CrowdLevel;

/**
 * 拥挤度 Mapper
 * <p>
 * 对应数据库表: t_crowd_level (Scenic模块)
 * 
 * @author 后端智能体
 * @since 2026-04-22
 */
@Mapper
public interface CrowdLevelMapper extends BaseMapper<CrowdLevel> {
}
