package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     * 根据名称分页搜索已启用的路网节点（仅返回有名称的节点）
     *
     * @param keyword 搜索关键词（模糊匹配），为空时返回所有有名称的已启用节点
     * @param limit   返回数量限制
     * @param offset  偏移量
     * @return 节点列表
     */
    @Select("<script>SELECT * FROM t_navigation_road_node WHERE is_deleted = 0 AND is_enabled = 1 AND name IS NOT NULL " +
            "<if test='keyword != null and keyword != \"\"'>AND name LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "ORDER BY node_type ASC LIMIT #{limit} OFFSET #{offset}</script>")
    List<RoadNode> selectByName(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 查询节点关联的通行方式列表（去重）
     *
     * @param nodeId 节点ID
     * @return 通行方式列表（1=仅步行, 2=仅自行车, 3=仅车辆, 4=步行+自行车, 5=全部）
     */
    @Select("SELECT DISTINCT transport_type FROM t_navigation_road_edge WHERE is_deleted = 0 AND from_node_id = #{nodeId}")
    List<Integer> selectTransportTypesByNode(@Param("nodeId") Long nodeId);

    /**
     * 根据名称分页搜索已启用的路网节点，同时返回 GROUP_CONCAT 聚合的通行方式列表
     *
     * @param keyword 搜索关键词（模糊匹配），为空时返回所有有名称的已启用节点
     * @param limit   返回数量限制
     * @param offset  偏移量
     * @return 包含节点信息和 transport_types 字符串的 Map 列表
     */
    @Select("<script>" +
            "SELECT rn.id, rn.osm_id, rn.name, rn.node_type, rn.latitude, rn.longitude, " +
            "GROUP_CONCAT(DISTINCT re.transport_type ORDER BY re.transport_type) AS transport_types " +
            "FROM t_navigation_road_node rn " +
            "LEFT JOIN t_navigation_road_edge re ON (re.from_node_id = rn.id OR re.to_node_id = rn.id) AND re.is_deleted = 0 " +
            "WHERE rn.is_deleted = 0 AND rn.is_enabled = 1 AND rn.name IS NOT NULL " +
            "<if test='keyword != null and keyword != \"\"'>AND rn.name LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "GROUP BY rn.id " +
            "ORDER BY rn.node_type ASC LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    List<Map<String, Object>> selectByNameWithTransport(@Param("keyword") String keyword, 
                                                        @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 批量插入节点（用于OSM数据导入）
     * 
     * @param nodes 节点列表
     * @return 插入数量
     */
    int batchInsert(@Param("nodes") List<RoadNode> nodes);
}