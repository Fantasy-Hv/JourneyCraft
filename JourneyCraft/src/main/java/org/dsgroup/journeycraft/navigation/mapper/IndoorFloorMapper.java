package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dsgroup.journeycraft.navigation.entity.IndoorFloor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 室内楼层 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 操作
 * 添加自定义查询方法支持室内导航业务
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Mapper
@Repository
public interface IndoorFloorMapper extends BaseMapper<IndoorFloor> {

    /**
     * 根据建筑ID查询楼层
     * 
     * @param buildingId 建筑ID
     * @return 楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND is_deleted = 0")
    List<IndoorFloor> selectByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 查询建筑的所有楼层（按楼层号排序）
     * 
     * @param buildingId 建筑ID
     * @return 楼层列表（升序）
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND is_deleted = 0 ORDER BY floor_number")
    List<IndoorFloor> selectAllFloorsByBuilding(@Param("buildingId") Long buildingId);

    /**
     * 查询指定楼层
     * 
     * @param buildingId 建筑ID
     * @param floorNumber 楼层号
     * @return 楼层
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND floor_number = #{floorNumber} AND is_deleted = 0")
    IndoorFloor selectByBuildingAndFloor(@Param("buildingId") Long buildingId,
                                         @Param("floorNumber") Integer floorNumber);

    /**
     * 查询地面层（floor_number = 0）
     * 
     * @param buildingId 建筑ID
     * @return 地面层
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND floor_number = 0 AND is_deleted = 0")
    IndoorFloor selectGroundFloorByBuilding(@Param("buildingId") Long buildingId);

    /**
     * 查询有电梯的楼层
     * 
     * @param buildingId 建筑ID
     * @return 有电梯的楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND elevator_node_id IS NOT NULL AND is_deleted = 0")
    List<IndoorFloor> selectFloorsWithElevator(@Param("buildingId") Long buildingId);

    /**
     * 查询有楼梯的楼层
     * 
     * @param buildingId 建筑ID
     * @return 有楼梯的楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND stair_node_id IS NOT NULL AND is_deleted = 0")
    List<IndoorFloor> selectFloorsWithStair(@Param("buildingId") Long buildingId);

    /**
     * 查询有地图的楼层
     * 
     * @param buildingId 建筑ID
     * @return 有地图的楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND map_image_url IS NOT NULL AND is_deleted = 0")
    List<IndoorFloor> selectFloorsWithMap(@Param("buildingId") Long buildingId);

    /**
     * 根据楼层名称查询
     * 
     * @param floorName 楼层名称（如"一层大厅"）
     * @return 楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE floor_name LIKE CONCAT('%', #{floorName}, '%') AND is_deleted = 0")
    List<IndoorFloor> selectByFloorName(@Param("floorName") String floorName);

    /**
     * 查询相邻楼层
     * 
     * @param buildingId 建筑ID
     * @param floorNumber 当前楼层号
     * @return 相邻楼层列表（上一层和下一层）
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND " +
            "floor_number IN (#{floorNumber} - 1, #{floorNumber} + 1) AND is_deleted = 0")
    List<IndoorFloor> selectAdjacentFloors(@Param("buildingId") Long buildingId,
                                           @Param("floorNumber") Integer floorNumber);

    /**
     * 查询地下室楼层（floor_number < 0）
     * 
     * @param buildingId 建筑ID
     * @return 地下室楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND floor_number < 0 AND is_deleted = 0")
    List<IndoorFloor> selectBasementFloors(@Param("buildingId") Long buildingId);

    /**
     * 查询高层楼层（floor_number >= 3）
     * 
     * @param buildingId 建筑ID
     * @return 高层楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND floor_number >= 3 AND is_deleted = 0")
    List<IndoorFloor> selectHighFloors(@Param("buildingId") Long buildingId);

    /**
     * 查询有入口节点的楼层
     * 
     * @param buildingId 建筑ID
     * @return 有入口的楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND " +
            "entrance_node_ids IS NOT NULL AND entrance_node_ids != '[]' AND is_deleted = 0")
    List<IndoorFloor> selectFloorsWithEntrances(@Param("buildingId") Long buildingId);

    /**
     * 查询垂直交通楼层（有电梯或楼梯）
     * 
     * @param buildingId 建筑ID
     * @return 垂直交通楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND " +
            "(elevator_node_id IS NOT NULL OR stair_node_id IS NOT NULL) AND is_deleted = 0")
    List<IndoorFloor> selectFloorsWithVerticalTransport(@Param("buildingId") Long buildingId);

    /**
     * 查询楼层范围
     * 
     * @param buildingId 建筑ID
     * @param minFloor 最小楼层号
     * @param maxFloor 最大楼层号
     * @return 楼层列表
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND " +
            "floor_number BETWEEN #{minFloor} AND #{maxFloor} AND is_deleted = 0 ORDER BY floor_number")
    List<IndoorFloor> selectFloorRange(@Param("buildingId") Long buildingId,
                                       @Param("minFloor") Integer minFloor,
                                       @Param("maxFloor") Integer maxFloor);

    /**
     * 查询建筑的最高楼层
     * 
     * @param buildingId 建筑ID
     * @return 最高楼层
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND is_deleted = 0 ORDER BY floor_number DESC LIMIT 1")
    IndoorFloor selectTopFloor(@Param("buildingId") Long buildingId);

    /**
     * 查询建筑的最低楼层
     * 
     * @param buildingId 建筑ID
     * @return 最低楼层
     */
    @Select("SELECT * FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND is_deleted = 0 ORDER BY floor_number LIMIT 1")
    IndoorFloor selectBottomFloor(@Param("buildingId") Long buildingId);

    /**
     * 查询楼层统计信息
     * 
     * @param buildingId 建筑ID
     * @return 统计信息（总层数、地下室数、高层数等）
     */
    @Select("SELECT " +
            "COUNT(*) as total_floors, " +
            "SUM(CASE WHEN floor_number < 0 THEN 1 ELSE 0 END) as basement_count, " +
            "SUM(CASE WHEN floor_number >= 3 THEN 1 ELSE 0 END) as high_floor_count, " +
            "SUM(CASE WHEN elevator_node_id IS NOT NULL THEN 1 ELSE 0 END) as elevator_count, " +
            "SUM(CASE WHEN stair_node_id IS NOT NULL THEN 1 ELSE 0 END) as stair_count, " +
            "MIN(floor_number) as min_floor, " +
            "MAX(floor_number) as max_floor " +
            "FROM t_navigation_indoor_floor WHERE building_id = #{buildingId} AND is_deleted = 0")
    Object selectFloorStatistics(@Param("buildingId") Long buildingId);

    /**
     * 批量插入楼层
     * 
     * @param floors 楼层列表
     * @return 插入数量
     */
    int batchInsert(@Param("floors") List<IndoorFloor> floors);
}