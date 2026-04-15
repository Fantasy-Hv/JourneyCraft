package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 路网节点 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 操作
 * 添加自定义查询方法支持导航业务需求
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Mapper
@Repository
public interface RoadNodeMapper extends BaseMapper<RoadNode> {

    /**
     * 根据景区ID查询节点列表
     * 
     * @param scenicAreaId 景区ID
     * @return 节点列表
     */
    @Select("SELECT * FROM t_navigation_road_node WHERE scenic_area_id = #{scenicAreaId} AND is_deleted = 0")
    List<RoadNode> selectByScenicAreaId(@Param("scenicAreaId") Long scenicAreaId);

    /**
     * 根据节点类型查询节点列表
     * 
     * @param nodeType 节点类型（0=入口,1=路口,2=POI,3=设施入口,4=拍照点,5=OSM普通节点）
     * @return 节点列表
     */
    @Select("SELECT * FROM t_navigation_road_node WHERE node_type = #{nodeType} AND is_deleted = 0")
    List<RoadNode> selectByNodeType(@Param("nodeType") Integer nodeType);

    /**
     * 查询重要节点（is_important = 1）
     * 
     * @return 重要节点列表
     */
    @Select("SELECT * FROM t_navigation_road_node WHERE is_important = 1 AND is_deleted = 0")
    List<RoadNode> selectImportantNodes();

    /**
     * 根据地理位置查询附近的节点（圆形区域）
     * 
     * @param centerLat 中心点纬度
     * @param centerLon 中心点经度
     * @param radius 半径（米）
     * @return 附近节点列表
     */
    @Select("SELECT * FROM t_navigation_road_node WHERE " +
            "is_deleted = 0 AND is_enabled = 1 AND is_accessible = 1 AND " +
            "calculate_distance(latitude, longitude, #{centerLat}, #{centerLon}) <= #{radius}")
    List<RoadNode> selectNearbyNodes(@Param("centerLat") BigDecimal centerLat,
                                     @Param("centerLon") BigDecimal centerLon,
                                     @Param("radius") Double radius);

    /**
     * 根据建筑ID查询节点列表
     * 
     * @param buildingId 建筑ID
     * @return 节点列表
     */
    @Select("SELECT * FROM t_navigation_road_node WHERE building_id = #{buildingId} AND is_deleted = 0")
    List<RoadNode> selectByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 查询指定楼层内的节点
     * 
     * @param buildingId 建筑ID
     * @param floorNumber 楼层号
     * @return 节点列表
     */
    @Select("SELECT * FROM t_navigation_road_node WHERE " +
            "building_id = #{buildingId} AND floor_number = #{floorNumber} AND is_deleted = 0")
    List<RoadNode> selectByFloor(@Param("buildingId") Long buildingId,
                                 @Param("floorNumber") Integer floorNumber);

    /**
     * 根据节点名称模糊查询
     * 
     * @param name 节点名称（模糊匹配）
     * @return 节点列表
     */
    @Select("SELECT * FROM t_navigation_road_node WHERE name LIKE CONCAT('%', #{name}, '%') AND is_deleted = 0")
    List<RoadNode> selectByNameLike(@Param("name") String name);

    /**
     * 根据OSM ID查询节点
     * 
     * @param osmId OSM节点ID
     * @return 节点
     */
    @Select("SELECT * FROM t_navigation_road_node WHERE osm_id = #{osmId} AND is_deleted = 0")
    RoadNode selectByOsmId(@Param("osmId") Long osmId);

    /**
     * 批量插入节点（用于OSM数据导入）
     * 
     * @param nodes 节点列表
     * @return 插入数量
     */
    int batchInsert(@Param("nodes") List<RoadNode> nodes);
}