package org.dsgroup.journeycraft.navigation.mapper;

import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface SpatialRoadNodeMapper {

    @Select("SELECT re.id as edgeId, re.from_node_id, re.to_node_id, re.distance as edgeDistance, " +
            "re.highway_type, re.transport_type, re.name as edgeName, " +
            "rn_from.latitude as fromLat, rn_from.longitude as fromLng, " +
            "rn_to.latitude as toLat, rn_to.longitude as toLng, " +
            "ST_Distance_Sphere(rn_from.geom, ST_PointFromText(CONCAT('POINT(', #{lat}, ' ', #{lng}, ')'), 4326)) as dist " +
            "FROM t_navigation_road_edge re " +
            "JOIN t_navigation_road_node rn_from ON re.from_node_id = rn_from.id " +
            "JOIN t_navigation_road_node rn_to ON re.to_node_id = rn_to.id " +
            "WHERE re.is_deleted = 0 AND re.is_enabled = 1 " +
            "AND rn_from.is_deleted = 0 AND rn_from.is_enabled = 1 " +
            "AND rn_from.latitude BETWEEN #{minLat} AND #{maxLat} " +
            "AND rn_from.longitude BETWEEN #{minLng} AND #{maxLng} " +
            "ORDER BY dist ASC LIMIT #{limit}")
    List<NearestEdgeCandidate> findNearestEdges(@Param("lat") double lat, @Param("lng") double lng,
                                                 @Param("minLat") double minLat, @Param("maxLat") double maxLat,
                                                 @Param("minLng") double minLng, @Param("maxLng") double maxLng,
                                                 @Param("limit") int limit);

    @Data
    class NearestEdgeCandidate {
        private Long edgeId;
        private Long fromNodeId;
        private Long toNodeId;
        private BigDecimal edgeDistance;
        private String highwayType;
        private Integer transportType;
        private String edgeName;
        private BigDecimal fromLat;
        private BigDecimal fromLng;
        private BigDecimal toLat;
        private BigDecimal toLng;
        private Double dist;
    }
}
