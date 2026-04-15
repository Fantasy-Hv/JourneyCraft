package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dsgroup.journeycraft.navigation.entity.CrowdLevel;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 拥挤度记录 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 操作
 * 添加自定义查询方法支持拥挤度监控业务
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Mapper
@Repository
public interface CrowdLevelMapper extends BaseMapper<CrowdLevel> {

    /**
     * 根据节点ID查询拥挤度记录
     * 
     * @param nodeId 节点ID
     * @return 拥挤度记录列表
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE node_id = #{nodeId} AND is_deleted = 0 ORDER BY recorded_at DESC")
    List<CrowdLevel> selectByNodeId(@Param("nodeId") Long nodeId);

    /**
     * 查询节点的最新拥挤度记录
     * 
     * @param nodeId 节点ID
     * @return 最新拥挤度记录
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE node_id = #{nodeId} AND is_deleted = 0 ORDER BY recorded_at DESC LIMIT 1")
    CrowdLevel selectLatestByNodeId(@Param("nodeId") Long nodeId);

    /**
     * 查询景区内所有节点的最新拥挤度
     * 
     * @param scenicAreaId 景区ID
     * @return 拥挤度记录列表（每个节点最新记录）
     */
    @Select("SELECT cl.* FROM t_navigation_crowd_level cl " +
            "JOIN t_navigation_road_node n ON cl.node_id = n.id " +
            "WHERE n.scenic_area_id = #{scenicAreaId} AND cl.is_deleted = 0 " +
            "AND cl.recorded_at = (SELECT MAX(recorded_at) FROM t_navigation_crowd_level " +
            "WHERE node_id = cl.node_id AND is_deleted = 0)")
    List<CrowdLevel> selectLatestByScenicAreaId(@Param("scenicAreaId") Long scenicAreaId);

    /**
     * 根据拥挤等级查询记录
     * 
     * @param level 拥挤等级（0=舒适,1=适中,2=拥挤,3=非常拥挤）
     * @return 拥挤度记录列表
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE level = #{level} AND is_deleted = 0")
    List<CrowdLevel> selectByLevel(@Param("level") Integer level);

    /**
     * 根据数据来源查询记录
     * 
     * @param source 数据来源（0=传感器,1=用户上报,2=算法预测,3=历史均值）
     * @return 拥挤度记录列表
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE source = #{source} AND is_deleted = 0")
    List<CrowdLevel> selectBySource(@Param("source") Integer source);

    /**
     * 查询指定时间范围内的拥挤度记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 拥挤度记录列表
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE recorded_at BETWEEN #{startTime} AND #{endTime} AND is_deleted = 0")
    List<CrowdLevel> selectByTimeRange(@Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime);

    /**
     * 查询高拥挤度记录（level >= 2）
     * 
     * @return 高拥挤度记录列表
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE level >= 2 AND is_deleted = 0")
    List<CrowdLevel> selectHighCrowdLevels();

    /**
     * 查询有效的拥挤度记录（未过期）
     * 
     * @return 有效拥挤度记录列表
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE " +
            "(valid_until IS NULL OR valid_until > NOW()) AND is_deleted = 0")
    List<CrowdLevel> selectValidRecords();

    /**
     * 查询预测数据（source = 2）
     * 
     * @return 预测拥挤度记录列表
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE source = 2 AND is_deleted = 0")
    List<CrowdLevel> selectPredictions();

    /**
     * 查询用户上报的记录
     * 
     * @param userId 用户ID
     * @return 用户上报记录列表
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE reporter_user_id = #{userId} AND is_deleted = 0")
    List<CrowdLevel> selectByReporterUserId(@Param("userId") Long userId);

    /**
     * 统计节点的拥挤度历史趋势
     * 
     * @param nodeId 节点ID
     * @param days 天数
     * @return 拥挤度记录列表（按时间分组）
     */
    @Select("SELECT * FROM t_navigation_crowd_level WHERE " +
            "node_id = #{nodeId} AND recorded_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "AND is_deleted = 0 ORDER BY recorded_at")
    List<CrowdLevel> selectHistoryTrend(@Param("nodeId") Long nodeId,
                                        @Param("days") Integer days);

    /**
     * 批量插入拥挤度记录
     * 
     * @param records 拥挤度记录列表
     * @return 插入数量
     */
    int batchInsert(@Param("records") List<CrowdLevel> records);
}