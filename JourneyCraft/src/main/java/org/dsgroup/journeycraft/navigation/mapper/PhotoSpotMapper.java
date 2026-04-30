package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dsgroup.journeycraft.navigation.entity.PhotoSpot;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 拍照点 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 操作
 * 添加自定义查询方法支持拍照点推荐业务
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Mapper
@Repository
public interface PhotoSpotMapper extends BaseMapper<PhotoSpot> {

    /**
     * 根据景区ID查询拍照点
     * 
     * @param scenicAreaId 景区ID
     * @return 拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE scenic_area_id = #{scenicAreaId} AND is_deleted = 0")
    List<PhotoSpot> selectByScenicAreaId(@Param("scenicAreaId") Long scenicAreaId);

    /**
     * 查询高评分拍照点（rating >= 4.0）
     * 
     * @return 高评分拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE rating >= 4.0 AND is_deleted = 0")
    List<PhotoSpot> selectHighRatedSpots();

    /**
     * 查询热门拍照点（check_in_count >= 10）
     * 
     * @return 热门拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE check_in_count >= 10 AND is_deleted = 0")
    List<PhotoSpot> selectPopularSpots();

    /**
     * 根据评分排序查询拍照点
     * 
     * @param limit 限制数量
     * @return 拍照点列表（按评分降序）
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE is_deleted = 0 ORDER BY rating DESC LIMIT #{limit}")
    List<PhotoSpot> selectTopRatedSpots(@Param("limit") Integer limit);

    /**
     * 根据打卡次数排序查询拍照点
     * 
     * @param limit 限制数量
     * @return 拍照点列表（按打卡次数降序）
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE is_deleted = 0 ORDER BY check_in_count DESC LIMIT #{limit}")
    List<PhotoSpot> selectMostCheckedInSpots(@Param("limit") Integer limit);

    /**
     * 根据地理位置查询附近的拍照点
     * 
     * @param centerLat 中心点纬度
     * @param centerLon 中心点经度
     * @param radius 半径（米）
     * @return 附近拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE " +
            "is_deleted = 0 AND is_enabled = 1 AND " +
            "calculate_distance(latitude, longitude, #{centerLat}, #{centerLon}) <= #{radius}")
    List<PhotoSpot> selectNearbySpots(@Param("centerLat") BigDecimal centerLat,
                                      @Param("centerLon") BigDecimal centerLon,
                                      @Param("radius") Double radius);

    /**
     * 查询关联节点的拍照点
     * 
     * @param nodeId 节点ID
     * @return 拍照点
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE node_id = #{nodeId} AND is_deleted = 0")
    PhotoSpot selectByNodeId(@Param("nodeId") Long nodeId);

    /**
     * 根据拍摄目标名称查询拍照点
     * 
     * @param targetName 拍摄目标名称
     * @return 拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE target_name LIKE CONCAT('%', #{targetName}, '%') AND is_deleted = 0")
    List<PhotoSpot> selectByTargetName(@Param("targetName") String targetName);

    /**
     * 查询有示例图片的拍照点
     * 
     * @return 有示例图片的拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE sample_image_url IS NOT NULL AND is_deleted = 0")
    List<PhotoSpot> selectSpotsWithSampleImages();

    /**
     * 根据季节推荐拍照点
     * 
     * @param season 季节（如"春季"、"夏季"等）
     * @return 适合该季节的拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE " +
            "best_season LIKE CONCAT('%', #{season}, '%') AND is_deleted = 0")
    List<PhotoSpot> selectBySeason(@Param("season") String season);

    /**
     * 查询最佳拍摄时间的拍照点
     * 
     * @param time 时间关键词（如"日落"、"日出"、"夜景"）
     * @return 适合该时间的拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE " +
            "best_time LIKE CONCAT('%', #{time}, '%') AND is_deleted = 0")
    List<PhotoSpot> selectByBestTime(@Param("time") String time);

    /**
     * 根据拍照点名称模糊查询
     * 
     * @param name 拍照点名称（模糊匹配）
     * @return 拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE name LIKE CONCAT('%', #{name}, '%') AND is_deleted = 0")
    List<PhotoSpot> selectByNameLike(@Param("name") String name);

    /**
     * 查询可用拍照点（启用+未删除）
     * 
     * @return 可用拍照点列表
     */
    @Select("SELECT * FROM t_navigation_photo_spot WHERE is_enabled = 1 AND is_deleted = 0")
    List<PhotoSpot> selectAvailableSpots();

    /**
     * 批量插入拍照点
     * 
     * @param spots 拍照点列表
     * @return 插入数量
     */
    int batchInsert(@Param("spots") List<PhotoSpot> spots);
}