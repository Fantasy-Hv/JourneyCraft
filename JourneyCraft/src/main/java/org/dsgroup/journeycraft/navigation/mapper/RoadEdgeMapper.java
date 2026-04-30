package org.dsgroup.journeycraft.navigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dsgroup.journeycraft.navigation.entity.RoadEdge;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 路径段 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 操作
 * 添加自定义查询方法支持路径规划业务
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Mapper
@Repository
public interface RoadEdgeMapper extends BaseMapper<RoadEdge> {

    /**
     * 根据起始节点ID查询出边
     * 
     * @param fromNodeId 起始节点ID
     * @return 出边列表
     */
    @Select("SELECT * FROM t_navigation_road_edge WHERE from_node_id = #{fromNodeId} AND is_deleted = 0")
    List<RoadEdge> selectOutgoingEdges(@Param("fromNodeId") Long fromNodeId);

    /**
     * 根据目标节点ID查询入边
     * 
     * @param toNodeId 目标节点ID
     * @return 入边列表
     */
    @Select("SELECT * FROM t_navigation_road_edge WHERE to_node_id = #{toNodeId} AND is_deleted = 0")
    List<RoadEdge> selectIncomingEdges(@Param("toNodeId") Long toNodeId);

    /**
     * 查询两个节点之间的边（双向）
     * 
     * @param nodeId1 节点1 ID
     * @param nodeId2 节点2 ID
     * @return 边列表（可能包含两个方向的边）
     */
    @Select("SELECT * FROM t_navigation_road_edge WHERE " +
            "((from_node_id = #{nodeId1} AND to_node_id = #{nodeId2}) OR " +
            "(from_node_id = #{nodeId2} AND to_node_id = #{nodeId1} AND is_bidirectional = 1)) " +
            "AND is_deleted = 0")
    List<RoadEdge> selectEdgesBetweenNodes(@Param("nodeId1") Long nodeId1,
                                           @Param("nodeId2") Long nodeId2);

    /**
     * 根据通行方式查询边
     * 
     * @param transportType 通行方式（0=未知,1=仅步行,2=仅自行车,3=仅车辆,4=步行+自行车,5=全部）
     * @return 边列表
     */
    @Select("SELECT * FROM t_navigation_road_edge WHERE transport_type = #{transportType} AND is_deleted = 0")
    List<RoadEdge> selectByTransportType(@Param("transportType") Integer transportType);

    /**
     * 查询支持步行的边
     * 
     * @return 可步行边列表
     */
    @Select("SELECT * FROM t_navigation_road_edge WHERE " +
            "transport_type IN (1, 4, 5) AND is_deleted = 0")
    List<RoadEdge> selectWalkableEdges();

    /**
     * 根据OSM路径ID查询边
     * 
     * @param osmWayId OSM路径ID
     * @return 边列表
     */
    @Select("SELECT * FROM t_navigation_road_edge WHERE osm_way_id = #{osmWayId} AND is_deleted = 0")
    List<RoadEdge> selectByOsmWayId(@Param("osmWayId") Long osmWayId);

    /**
     * 根据道路类型查询边
     * 
     * @param highwayType 道路类型（highway标签值）
     * @return 边列表
     */
    @Select("SELECT * FROM t_navigation_road_edge WHERE highway_type = #{highwayType} AND is_deleted = 0")
    List<RoadEdge> selectByHighwayType(@Param("highwayType") String highwayType);

    /**
     * 查询景区内的边（通过节点关联）
     * 
     * @param scenicAreaId 景区ID
     * @return 边列表
     */
    @Select("SELECT e.* FROM t_navigation_road_edge e " +
            "JOIN t_navigation_road_node n1 ON e.from_node_id = n1.id " +
            "WHERE n1.scenic_area_id = #{scenicAreaId} AND e.is_deleted = 0")
    List<RoadEdge> selectByScenicAreaId(@Param("scenicAreaId") Long scenicAreaId);

    /**
     * 查询建筑内的边（室内路径）
     * 
     * @param buildingId 建筑ID
     * @return 边列表
     */
    @Select("SELECT e.* FROM t_navigation_road_edge e " +
            "JOIN t_navigation_road_node n1 ON e.from_node_id = n1.id " +
            "WHERE n1.building_id = #{buildingId} AND e.is_deleted = 0")
    List<RoadEdge> selectByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 查询节点所有连接的边（出边+入边，考虑双向性）
     * 
     * @param nodeId 节点ID
     * @return 连接边列表
     */
    @Select("SELECT * FROM t_navigation_road_edge WHERE " +
            "(from_node_id = #{nodeId} OR (to_node_id = #{nodeId} AND is_bidirectional = 1)) " +
            "AND is_deleted = 0")
    List<RoadEdge> selectConnectedEdges(@Param("nodeId") Long nodeId);

    /**
     * 批量插入边（用于OSM数据导入）
     * 
     * @param edges 边列表
     * @return 插入数量
     */
    int batchInsert(@Param("edges") List<RoadEdge> edges);
}