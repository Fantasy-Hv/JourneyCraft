package org.dsgroup.journeycraft.navigation.service.impl;

import org.dsgroup.journeycraft.navigation.entity.RoadEdge;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.service.RoadEdgeService;
import org.dsgroup.journeycraft.navigation.service.RoadNodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 路径规划算法单元测试（简化版）
 * <p>
 * 由于MyBatis-Plus的LambdaQueryChainWrapper是final类且方法链复杂，
 * 部分测试需要依赖集成测试验证。此处测试不涉及复杂wrapper链的方法。
 * 
 * @author 后端智能体
 * @since 2026-04-15
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PathPlanningServiceImplTest {

    @Mock
    private RoadNodeService roadNodeService;

    @Mock
    private RoadEdgeService roadEdgeService;

    private PathPlanningServiceImpl pathPlanningService;

    @BeforeEach
    void setUp() throws Exception {
        pathPlanningService = new PathPlanningServiceImpl();
        
        java.lang.reflect.Field nodeField = PathPlanningServiceImpl.class.getDeclaredField("roadNodeService");
        nodeField.setAccessible(true);
        nodeField.set(pathPlanningService, roadNodeService);
        
        java.lang.reflect.Field edgeField = PathPlanningServiceImpl.class.getDeclaredField("roadEdgeService");
        edgeField.setAccessible(true);
        edgeField.set(pathPlanningService, roadEdgeService);
    }

    /**
     * 测试：单目标路径规划 - 起点不存在
     */
    @Test
    void testCalculateShortestPath_StartNodeNotFound() {
        when(roadNodeService.getById(999L)).thenReturn(null);

        var result = pathPlanningService.calculateShortestPath(999L, 2L, 1, "shortest_distance");

        assertNull(result);
    }

    /**
     * 测试：单目标路径规划 - 终点不存在
     */
    @Test
    void testCalculateShortestPath_EndNodeNotFound() {
        when(roadNodeService.getById(1L)).thenReturn(createNode(1L, "起点", 39.0, 116.0));
        when(roadNodeService.getById(999L)).thenReturn(null);

        var result = pathPlanningService.calculateShortestPath(1L, 999L, 1, "shortest_distance");

        assertNull(result);
    }

    /**
     * 测试：单目标路径规划 - 起点终点都是null
     */
    @Test
    void testCalculateShortestPath_BothNull() {
        when(roadNodeService.getById(1L)).thenReturn(null);
        when(roadNodeService.getById(2L)).thenReturn(null);

        var result = pathPlanningService.calculateShortestPath(1L, 2L, 1, "shortest_distance");

        assertNull(result);
    }


    /**
     * 测试：多目标路线规划 - 空目标列表
     */
    @Test
    void testCalculateMultiTargetRoute_EmptyTargets() {
        var result = pathPlanningService.calculateMultiTargetRoute(1L, Collections.emptyList(), 1, "shortest_distance", false);

        assertNull(result);
    }

    /**
     * 测试：多目标路线规划 - null目标列表
     */
    @Test
    void testCalculateMultiTargetRoute_NullTargets() {
        var result = pathPlanningService.calculateMultiTargetRoute(1L, null, 1, "shortest_distance", false);

        assertNull(result);
    }

    /**
     * 测试：算法接口存在性验证
     * 验证PathPlanningService接口定义了正确的方法
     */
    @Test
    void testAlgorithmInterface_Exists() {
        // 验证getDistanceBetweenNodes方法存在
        try {
            var method = PathPlanningServiceImpl.class.getMethod("getDistanceBetweenNodes", Long.class, Long.class);
            assertNotNull(method);
            assertEquals(BigDecimal.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("getDistanceBetweenNodes method should exist");
        }

        // 验证getTimeBetweenNodes方法存在
        try {
            var method = PathPlanningServiceImpl.class.getMethod("getTimeBetweenNodes", Long.class, Long.class, Integer.class);
            assertNotNull(method);
            assertEquals(Integer.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("getTimeBetweenNodes method should exist");
        }

        // 验证calculateShortestPath方法存在
        try {
            var method = PathPlanningServiceImpl.class.getMethod("calculateShortestPath", Long.class, Long.class, Integer.class, String.class);
            assertNotNull(method);
        } catch (NoSuchMethodException e) {
            fail("calculateShortestPath method should exist");
        }

        // 验证calculateMultiTargetRoute方法存在
        try {
            var method = PathPlanningServiceImpl.class.getMethod("calculateMultiTargetRoute", Long.class, java.util.List.class, Integer.class, boolean.class);
            assertNotNull(method);
        } catch (NoSuchMethodException e) {
            fail("calculateMultiTargetRoute method should exist");
        }
    }

    /**
     * 测试：PathNode内部类存在且字段正确
     */
    @Test
    void testPathNodeClass_Exists() {
        try {
            var pathNodeClass = Class.forName("org.dsgroup.journeycraft.navigation.service.PathPlanningService$PathNode");
            assertNotNull(pathNodeClass);
            
            // 验证关键方法存在
            assertNotNull(pathNodeClass.getMethod("getNodeId"));
            assertNotNull(pathNodeClass.getMethod("getName"));
            assertNotNull(pathNodeClass.getMethod("getLatitude"));
            assertNotNull(pathNodeClass.getMethod("getLongitude"));
            assertNotNull(pathNodeClass.getMethod("getSequence"));
            assertNotNull(pathNodeClass.getMethod("getAction"));
        } catch (ClassNotFoundException e) {
            fail("PathNode inner class should exist");
        } catch (NoSuchMethodException e) {
            fail("PathNode should have required getter methods");
        }
    }

    /**
     * 测试：PathPlanningResult内部类存在且字段正确
     */
    @Test
    void testPathPlanningResultClass_Exists() {
        try {
            var resultClass = Class.forName("org.dsgroup.journeycraft.navigation.service.PathPlanningService$PathPlanningResult");
            assertNotNull(resultClass);
            
            // 验证关键方法存在
            assertNotNull(resultClass.getMethod("getTotalDistance"));
            assertNotNull(resultClass.getMethod("getEstimatedTime"));
            assertNotNull(resultClass.getMethod("getTransportMode"));
            assertNotNull(resultClass.getMethod("getNodes"));
            assertNotNull(resultClass.getMethod("getStrategy"));
        } catch (ClassNotFoundException e) {
            fail("PathPlanningResult inner class should exist");
        } catch (NoSuchMethodException e) {
            fail("PathPlanningResult should have required getter methods");
        }
    }

    /**
     * 测试：MultiTargetRouteResult内部类存在且字段正确
     */
    @Test
    void testMultiTargetRouteResultClass_Exists() {
        try {
            var resultClass = Class.forName("org.dsgroup.journeycraft.navigation.service.PathPlanningService$MultiTargetRouteResult");
            assertNotNull(resultClass);
            
            // 验证关键方法存在
            assertNotNull(resultClass.getMethod("getTotalDistance"));
            assertNotNull(resultClass.getMethod("getTotalTime"));
            assertNotNull(resultClass.getMethod("getVisitOrder"));
            assertNotNull(resultClass.getMethod("getSegments"));
            assertNotNull(resultClass.getMethod("isReturnedToStart"));
        } catch (ClassNotFoundException e) {
            fail("MultiTargetRouteResult inner class should exist");
        } catch (NoSuchMethodException e) {
            fail("MultiTargetRouteResult should have required getter methods");
        }
    }

    // ==================== 辅助方法 ====================

    private RoadNode createNode(Long id, String name, double lat, double lon) {
        RoadNode node = new RoadNode();
        node.setId(id);
        node.setName(name);
        node.setLatitude(BigDecimal.valueOf(lat));
        node.setLongitude(BigDecimal.valueOf(lon));
        node.setNodeType(1);
        node.setAccessibleFlag(true);
        node.setEnabled(true);
        node.setDeleted(false);
        return node;
    }
}
