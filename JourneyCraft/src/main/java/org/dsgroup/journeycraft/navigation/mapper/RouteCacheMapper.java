package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.dsgroup.journeycraft.navigation.entity.RouteCache;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 路径规划缓存 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 操作
 * 添加自定义查询方法支持缓存管理业务
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Mapper
@Repository
public interface RouteCacheMapper extends BaseMapper<RouteCache> {

    /**
     * 根据查询条件查找缓存
     * 
     * @param startNodeId 起始节点ID
     * @param endNodeId 目标节点ID
     * @param transportType 通行方式
     * @param strategy 规划策略
     * @return 缓存记录
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE " +
            "start_node_id = #{startNodeId} AND end_node_id = #{endNodeId} AND " +
            "transport_type = #{transportType} AND strategy = #{strategy} AND is_deleted = 0")
    RouteCache selectByQuery(@Param("startNodeId") Long startNodeId,
                             @Param("endNodeId") Long endNodeId,
                             @Param("transportType") Integer transportType,
                             @Param("strategy") String strategy);

    /**
     * 查询有效的缓存（未过期）
     * 
     * @return 有效缓存列表
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE expires_at > NOW() AND is_deleted = 0")
    List<RouteCache> selectValidCaches();

    /**
     * 查询过期的缓存
     * 
     * @return 过期缓存列表
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE expires_at <= NOW() AND is_deleted = 0")
    List<RouteCache> selectExpiredCaches();

    /**
     * 查询高频命中缓存（hit_count >= 10）
     * 
     * @return 高频命中缓存列表
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE hit_count >= 10 AND is_deleted = 0")
    List<RouteCache> selectHighHitCaches();

    /**
     * 根据通行方式查询缓存
     * 
     * @param transportType 通行方式
     * @return 缓存列表
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE transport_type = #{transportType} AND is_deleted = 0")
    List<RouteCache> selectByTransportType(@Param("transportType") Integer transportType);

    /**
     * 根据规划策略查询缓存
     * 
     * @param strategy 规划策略
     * @return 缓存列表
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE strategy = #{strategy} AND is_deleted = 0")
    List<RouteCache> selectByStrategy(@Param("strategy") String strategy);

    /**
     * 查询起始节点的所有缓存
     * 
     * @param startNodeId 起始节点ID
     * @return 缓存列表
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE start_node_id = #{startNodeId} AND is_deleted = 0")
    List<RouteCache> selectByStartNodeId(@Param("startNodeId") Long startNodeId);

    /**
     * 查询目标节点的所有缓存
     * 
     * @param endNodeId 目标节点ID
     * @return 缓存列表
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE end_node_id = #{endNodeId} AND is_deleted = 0")
    List<RouteCache> selectByEndNodeId(@Param("endNodeId") Long endNodeId);

    /**
     * 查询最近计算的缓存
     * 
     * @param limit 限制数量
     * @return 最近缓存列表
     */
    @Select("SELECT * FROM t_navigation_route_cache WHERE is_deleted = 0 ORDER BY calculated_at DESC LIMIT #{limit}")
    List<RouteCache> selectRecentCaches(@Param("limit") Integer limit);

    /**
     * 增加缓存命中次数
     * 
     * @param id 缓存ID
     * @return 更新影响行数
     */
    @Update("UPDATE t_navigation_route_cache SET hit_count = hit_count + 1, updated_at = NOW() WHERE id = #{id}")
    int incrementHitCount(@Param("id") Long id);

    /**
     * 批量更新缓存过期时间
     * 
     * @param ids 缓存ID列表
     * @param newExpiresAt 新的过期时间
     * @return 更新影响行数
     */
    @Update("<script>" +
            "UPDATE t_navigation_route_cache SET expires_at = #{newExpiresAt}, updated_at = NOW() " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateExpires(@Param("ids") List<Long> ids,
                           @Param("newExpiresAt") Date newExpiresAt);

    /**
     * 删除过期缓存（逻辑删除）
     * 
     * @return 删除影响行数
     */
    @Update("UPDATE t_navigation_route_cache SET is_deleted = 1, updated_at = NOW() WHERE expires_at <= NOW() AND is_deleted = 0")
    int deleteExpiredCaches();

    /**
     * 查询缓存统计信息
     * 
     * @return 统计信息（总缓存数、有效缓存数、平均命中次数等）
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN expires_at > NOW() THEN 1 ELSE 0 END) as valid_count, " +
            "AVG(hit_count) as avg_hit_count, " +
            "MAX(hit_count) as max_hit_count, " +
            "MIN(calculation_time_ms) as min_calc_time, " +
            "AVG(calculation_time_ms) as avg_calc_time " +
            "FROM t_navigation_route_cache WHERE is_deleted = 0")
    Object selectCacheStatistics();

    /**
     * 查询缓存使用效率（命中次数/天数）
     * 
     * @param minEfficiency 最小效率值
     * @return 高效缓存列表
     */
    @Select("SELECT *, hit_count / GREATEST(DATEDIFF(NOW(), calculated_at), 1) as efficiency " +
            "FROM t_navigation_route_cache WHERE is_deleted = 0 " +
            "HAVING efficiency >= #{minEfficiency} " +
            "ORDER BY efficiency DESC")
    List<RouteCache> selectEfficientCaches(@Param("minEfficiency") Double minEfficiency);

    /**
     * 批量插入缓存记录
     * 
     * @param caches 缓存记录列表
     * @return 插入数量
     */
    int batchInsert(@Param("caches") List<RouteCache> caches);
}