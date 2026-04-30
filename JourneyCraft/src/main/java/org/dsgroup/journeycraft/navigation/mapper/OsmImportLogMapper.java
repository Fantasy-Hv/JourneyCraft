package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dsgroup.journeycraft.navigation.entity.OsmImportLog;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * OSM导入日志 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 操作
 * 添加自定义查询方法支持导入日志监控
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Mapper
@Repository
public interface OsmImportLogMapper extends BaseMapper<OsmImportLog> {

    /**
     * 根据状态查询导入日志
     * 
     * @param status 状态（processing, completed, failed）
     * @return 导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE status = #{status} AND is_deleted = 0")
    List<OsmImportLog> selectByStatus(@Param("status") String status);

    /**
     * 查询最近的导入日志
     * 
     * @param limit 限制数量
     * @return 最近导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE is_deleted = 0 ORDER BY started_at DESC LIMIT #{limit}")
    List<OsmImportLog> selectRecentLogs(@Param("limit") Integer limit);

    /**
     * 根据区域查询导入日志
     * 
     * @param region 区域（如"北京西城区"）
     * @return 导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE region = #{region} AND is_deleted = 0")
    List<OsmImportLog> selectByRegion(@Param("region") String region);

    /**
     * 根据导入类型查询日志
     * 
     * @param importType 导入类型（node, way, relation）
     * @return 导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE import_type = #{importType} AND is_deleted = 0")
    List<OsmImportLog> selectByImportType(@Param("importType") String importType);

    /**
     * 查询失败的导入日志
     * 
     * @return 失败导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE status = 'failed' AND is_deleted = 0")
    List<OsmImportLog> selectFailedLogs();

    /**
     * 查询正在处理的导入日志
     * 
     * @return 处理中导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE status = 'processing' AND is_deleted = 0")
    List<OsmImportLog> selectProcessingLogs();

    /**
     * 根据文件名查询导入日志
     * 
     * @param fileName 文件名
     * @return 导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE file_name = #{fileName} AND is_deleted = 0")
    List<OsmImportLog> selectByFileName(@Param("fileName") String fileName);

    /**
     * 查询指定时间范围内的导入日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE started_at BETWEEN #{startTime} AND #{endTime} AND is_deleted = 0")
    List<OsmImportLog> selectByTimeRange(@Param("startTime") Date startTime,
                                         @Param("endTime") Date endTime);

    /**
     * 查询耗时较长的导入日志（大于30秒）
     * 
     * @return 耗时较长导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE " +
            "finished_at IS NOT NULL AND " +
            "TIMESTAMPDIFF(SECOND, started_at, finished_at) > 30 AND is_deleted = 0")
    List<OsmImportLog> selectLongRunningLogs();

    /**
     * 查询导入成功率统计
     * 
     * @return 统计信息（总记录数、平均成功率等）
     */
    @Select("SELECT " +
            "COUNT(*) as total_imports, " +
            "SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_count, " +
            "SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) as failed_count, " +
            "AVG(success_count * 100.0 / GREATEST(total_records, 1)) as avg_success_rate, " +
            "AVG(TIMESTAMPDIFF(SECOND, started_at, finished_at)) as avg_duration_seconds " +
            "FROM t_navigation_osm_import_log WHERE is_deleted = 0")
    Object selectImportStatistics();

    /**
     * 查询最新的成功导入日志
     * 
     * @return 最新成功导入日志
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE status = 'completed' AND is_deleted = 0 ORDER BY finished_at DESC LIMIT 1")
    OsmImportLog selectLatestSuccessfulLog();

    /**
     * 查询需要重试的失败导入（最近24小时内失败且错误信息不为空）
     * 
     * @return 需要重试的导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE " +
            "status = 'failed' AND error_message IS NOT NULL AND " +
            "started_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR) AND is_deleted = 0")
    List<OsmImportLog> selectRetryableFailedLogs();

    /**
     * 根据文件大小范围查询导入日志
     * 
     * @param minSize 最小文件大小（字节）
     * @param maxSize 最大文件大小（字节）
     * @return 导入日志列表
     */
    @Select("SELECT * FROM t_navigation_osm_import_log WHERE " +
            "file_size BETWEEN #{minSize} AND #{maxSize} AND is_deleted = 0")
    List<OsmImportLog> selectByFileSizeRange(@Param("minSize") Long minSize,
                                             @Param("maxSize") Long maxSize);

    /**
     * 查询导入任务的平均性能指标
     * 
     * @param importType 导入类型（可选）
     * @return 性能指标
     */
    @Select("<script>" +
            "SELECT " +
            "import_type, " +
            "COUNT(*) as task_count, " +
            "AVG(total_records) as avg_records, " +
            "AVG(TIMESTAMPDIFF(SECOND, started_at, finished_at)) as avg_duration_seconds, " +
            "AVG(success_count * 100.0 / GREATEST(total_records, 1)) as avg_success_rate " +
            "FROM t_navigation_osm_import_log " +
            "WHERE status = 'completed' AND is_deleted = 0 " +
            "<if test='importType != null'>AND import_type = #{importType}</if> " +
            "GROUP BY import_type" +
            "</script>")
    List<Object> selectPerformanceMetrics(@Param("importType") String importType);

    /**
     * 批量插入导入日志
     * 
     * @param logs 导入日志列表
     * @return 插入数量
     */
    int batchInsert(@Param("logs") List<OsmImportLog> logs);
}