package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dsgroup.journeycraft.navigation.entity.ScenicAccess;

import java.util.List;

/**
 * 景区路网接入点 Mapper
 * <p>
 * 管理 t_navigation_scenic_access 表的数据库操作
 * 
 * @author 后端智能体
 * @since 2026-05-08
 */
@Mapper
public interface ScenicAccessMapper extends BaseMapper<ScenicAccess> {

    /**
     * 按景区ID和接入类型查询接入点（按优先级排序）
     */
    @Select("SELECT * FROM t_navigation_scenic_access " +
            "WHERE scenic_area_id = #{scenicAreaId} " +
            "AND access_type = #{accessType} " +
            "AND is_deleted = 0 " +
            "ORDER BY rank_order ASC")
    List<ScenicAccess> selectByScenicAndType(@Param("scenicAreaId") Long scenicAreaId,
                                             @Param("accessType") Integer accessType);

    /**
     * 查询景区所有接入点（按类型和优先级排序）
     */
    @Select("SELECT * FROM t_navigation_scenic_access " +
            "WHERE scenic_area_id = #{scenicAreaId} " +
            "AND is_deleted = 0 " +
            "ORDER BY access_type, rank_order ASC")
    List<ScenicAccess> selectByScenicArea(@Param("scenicAreaId") Long scenicAreaId);
}
