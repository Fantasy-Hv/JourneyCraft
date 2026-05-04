package org.dsgroup.journeycraft.navigation.api;

import org.dsgroup.journeycraft.navigation.dto.MultiRouteResultDTO;
import org.dsgroup.journeycraft.navigation.dto.RouteResultDTO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.NearbyFacilityRspVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 导航模块对外服务接口。
 * <p>
 * 供其他模块（group、recommend 等）通过 {@code @Resource} 注入调用，
 * 遵循「模块调用约定.md」中「只依赖接口，不依赖实现类」的规范。
 * <p>
 * 调用示例：
 * <pre>{@code
 * @Resource
 * private NavigationService navigationService;
 *
 * RouteResultDTO route = navigationService.planSingleRoute(
 *     scenicId, startNodeId, endNodeId, "shortest_time", "walk");
 * }</pre>
 *
 * @author 刘方正
 * @since 2026-04-25
 */
public interface NavigationService {

    /**
     * 单目标路径规划（默认使用 Dijkstra 算法）。
     *
     * @param scenicAreaId 景区ID
     * @param startNodeId  起点节点ID
     * @param endNodeId    终点节点ID
     * @param strategy     规划策略 (shortest_distance / shortest_time / avoid_crowd)
     * @param transportMode 交通方式 (walk / bike / shuttle)
     * @return 路径规划结果，包含路段节点序列、总距离、预计时间
     */
    RouteResultDTO planSingleRoute(
            Long scenicAreaId, Long startNodeId, Long endNodeId,
            String strategy, String transportMode);

    /**
     * 单目标路径规划（使用 A* 算法）。
     * <p>
     * A* 使用 Haversine 直线距离作启发式函数，在大多数场景下比 Dijkstra 探索更少节点。
     *
     * @param scenicAreaId 景区ID
     * @param startNodeId  起点节点ID
     * @param endNodeId    终点节点ID
     * @param strategy     规划策略
     * @param transportMode 交通方式
     * @return 路径规划结果
     */
    RouteResultDTO planSingleRouteAStar(
            Long scenicAreaId, Long startNodeId, Long endNodeId,
            String strategy, String transportMode);

    /**
     * 多目标路线规划（TSP 变种，贪心最近邻算法）。
     * <p>
     * 从起点出发，依次访问所有目标节点。可选是否返回起点。
     *
     * @param scenicAreaId 景区ID
     * @param startNodeId  起点节点ID
     * @param targetNodeIds 目标节点ID列表
     * @param needReturn   是否返回起点
     * @param transportMode 交通方式
     * @return 多目标路线规划结果，包含各段详情、访问顺序、总距离、总时间
     */
    MultiRouteResultDTO planMultiRoute(
            Long scenicAreaId, Long startNodeId, List<Long> targetNodeIds,
            boolean needReturn, String transportMode);

    /**
     * 获取两个节点之间的实际路径距离。
     * <p>
     * 基于路网的路径距离，非直线距离。
     *
     * @param fromNodeId 起始节点ID
     * @param toNodeId   目标节点ID
     * @return 距离（米），路径不存在返回 null
     */
    BigDecimal getDistanceBetweenNodes(Long fromNodeId, Long toNodeId);

    /**
     * 获取两个节点之间的预计通行时间。
     *
     * @param fromNodeId    起始节点ID
     * @param toNodeId      目标节点ID
     * @param transportMode 交通方式 (1=步行, 2=骑行, 3=驾驶)
     * @return 时间（秒），路径不存在返回 null
     */
    Integer getTimeBetweenNodes(Long fromNodeId, Long toNodeId, Integer transportMode);

    /**
     * 获取指定节点附近的设施列表（按路网实际距离排序）。
     *
     * @param scenicAreaId 景区ID
     * @param nodeId       当前节点ID
     * @param facilityType 设施类型过滤（可选）
     * @param radiusMeters 搜索半径（米）
     * @param limit        返回数量限制
     * @return 附近设施列表，按实际路径距离升序
     */
    List<NearbyFacilityRspVO> getNearbyFacilities(
            Long scenicAreaId, Long nodeId, Integer facilityType,
            Integer radiusMeters, Integer limit);

    /**
     * 获取用户到指定目标地点的实际路网距离（供 Recommend 模块调用）。
     * <p>
     * 不同于直线距离，该方法先将用户 GPS 坐标映射到最近的路网节点，
     * 再通过 Dijkstra 算法沿道路网络计算到目标地点入口的实际可达距离。
     *
     * <h3>使用示例</h3>
     * <pre>{@code
     * // 方案 B: 故宫离我多远？
     * BigDecimal dist = navigationService.getDistanceToTarget(
     *     0,                     // 0 = 景区入口
     *     1L,                    // scenicAreaId = 1 (故宫)
     *     new BigDecimal("39.9100"),   // 用户纬度
     *     new BigDecimal("116.4000")   // 用户经度
     * );
     *
     * // 方案 C: 太和殿离我多远？
     * BigDecimal dist = navigationService.getDistanceToTarget(
     *     1,                     // 1 = 建筑物
     *     1L,                    // buildingId = 1 (太和殿)
     *     new BigDecimal("39.9100"),
     *     new BigDecimal("116.4000")
     * );
     * }</pre>
     *
     * @param targetType  目标类型: 0=景区入口, 1=建筑物
     * @param targetId    目标ID（景区ID 或 建筑ID）
     * @param userLat     用户当前纬度
     * @param userLng     用户当前经度
     * @return 实际路网距离（米），路径不存在或参数无效返回 null
     */
    BigDecimal getDistanceToTarget(Integer targetType, Long targetId,
                                    BigDecimal userLat, BigDecimal userLng);
}
