package org.dsgroup.journeycraft.navigation.controller;

import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.navigation.service.PathPlanningService;
import org.dsgroup.journeycraft.navigation.service.NavigationRouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 导航控制器单元测试
 * 
 * @author 后端智能体
 * @since 2026-04-15
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NavigationControllerTest {

    @Mock
    private PathPlanningService pathPlanningService;

    @Mock
    private NavigationRouteService navigationRouteService;

    private NavigationController navigationController;

    @BeforeEach
    void setUp() {
        navigationController = new NavigationController();
        
        try {
            java.lang.reflect.Field pathField = NavigationController.class.getDeclaredField("pathPlanningService");
            pathField.setAccessible(true);
            pathField.set(navigationController, pathPlanningService);
            
            java.lang.reflect.Field routeField = NavigationController.class.getDeclaredField("navigationRouteService");
            routeField.setAccessible(true);
            routeField.set(navigationController, navigationRouteService);
        } catch (Exception e) {
            fail("Failed to inject mocks: " + e.getMessage());
        }
    }

    /**
     * 测试：单目标路径规划 - 正常情况
     */
    @Test
    void testCalculateRoute_Success() {
        PathPlanningService.PathPlanningResult mockResult = createMockPathResult();
        when(pathPlanningService.calculateShortestPath(eq(1L), eq(5L), eq(1), eq("shortest_distance")))
            .thenReturn(mockResult);

        Response<?> response = navigationController.calculateRoute(
            1L, 1L, 5L, "shortest_distance", "walk");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getCode());
    }

    /**
     * 测试：单目标路径规划 - 未找到路径
     */
    @Test
    void testCalculateRoute_NoPathFound() {
        when(pathPlanningService.calculateShortestPath(anyLong(), anyLong(), anyInt(), anyString()))
            .thenReturn(null);

        Response<?> response = navigationController.calculateRoute(
            1L, 1L, 999L, "shortest_distance", "walk");

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("未找到可行路径", response.getMessage());
    }

    /**
     * 测试：单目标路径规划 - 骑行模式
     */
    @Test
    void testCalculateRoute_BikeMode() {
        PathPlanningService.PathPlanningResult bikeResult = createMockPathResult();
        bikeResult.setTransportMode("bike");
        bikeResult.setEstimatedTime(160);
        
        when(pathPlanningService.calculateShortestPath(eq(1L), eq(5L), eq(2), eq("shortest_distance")))
            .thenReturn(bikeResult);

        Response<?> response = navigationController.calculateRoute(
            1L, 1L, 5L, "shortest_distance", "bike");

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    /**
     * 测试：多目标路线规划 - 正常情况
     */
    @Test
    void testCalculateMultiRoute_Success() {
        PathPlanningService.MultiTargetRouteResult mockResult = new PathPlanningService.MultiTargetRouteResult();
        mockResult.setTotalDistance(BigDecimal.valueOf(1310.0));
        mockResult.setTotalTime(995);
        mockResult.setReturnedToStart(false);
        mockResult.setVisitOrder(Arrays.asList(7L, 5L, 10L));
        mockResult.setSegments(Collections.emptyList());

        when(pathPlanningService.calculateMultiTargetRoute(eq(1L), anyList(), eq(1), eq(false)))
            .thenReturn(mockResult);

        Response<?> response = navigationController.calculateMultiRoute(
            1L, 1L, "5,7,10", "shortest_distance", "walk", false);

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    /**
     * 测试：多目标路线规划 - 空目标列表
     */
    @Test
    void testCalculateMultiRoute_EmptyTargets() {
        Response<?> response = navigationController.calculateMultiRoute(
            1L, 1L, "", "shortest_distance", "walk", false);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("目标节点列表不能为空", response.getMessage());
    }

    /**
     * 测试：多目标路线规划 - 需要返回起点
     */
    @Test
    void testCalculateMultiRoute_WithReturn() {
        PathPlanningService.MultiTargetRouteResult mockResult = new PathPlanningService.MultiTargetRouteResult();
        mockResult.setTotalDistance(BigDecimal.valueOf(1860.0));
        mockResult.setTotalTime(1430);
        mockResult.setReturnedToStart(true);
        mockResult.setVisitOrder(Arrays.asList(7L, 5L, 10L));
        mockResult.setSegments(Collections.emptyList());

        when(pathPlanningService.calculateMultiTargetRoute(eq(1L), anyList(), eq(1), eq(true)))
            .thenReturn(mockResult);

        Response<?> response = navigationController.calculateMultiRoute(
            1L, 1L, "5,7,10", "shortest_distance", "walk", true);

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    /**
     * 测试：获取附近设施
     */
    @Test
    void testGetNearbyFacilities() {
        Response<?> response = navigationController.getNearbyFacilities(1L, 1L, null, 500, 10);

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    /**
     * 测试：室内导航
     */
    @Test
    void testCalculateIndoorRoute() {
        Response<?> response = navigationController.calculateIndoorRoute(
            1L, 1, "entrance", 2, "room-201");

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    /**
     * 测试：获取实时拥挤度
     */
    @Test
    void testGetCongestion() {
        Response<?> response = navigationController.getCongestion(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    /**
     * 测试：反向游览建议
     */
    @Test
    void testGetAlternativeRoute() {
        Response<?> response = navigationController.getAlternativeRoute(1L, "1,2,3,4,5");

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    /**
     * 测试：健康检查
     */
    @Test
    void testHealthCheck() {
        when(navigationRouteService.count()).thenReturn(10L);

        Response<?> response = navigationController.healthCheck();

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    // ==================== 辅助方法 ====================

    private PathPlanningService.PathPlanningResult createMockPathResult() {
        PathPlanningService.PathPlanningResult result = new PathPlanningService.PathPlanningResult();
        result.setTotalDistance(BigDecimal.valueOf(490.0));
        result.setEstimatedTime(375);
        result.setTransportMode("walk");
        result.setStrategy("shortest_distance");
        
        PathPlanningService.PathNode node1 = new PathPlanningService.PathNode();
        node1.setNodeId(1L);
        node1.setName("午门入口");
        node1.setLatitude(BigDecimal.valueOf(39.9161));
        node1.setLongitude(BigDecimal.valueOf(116.3970));
        node1.setSequence(0);
        node1.setAction("start");

        PathPlanningService.PathNode node2 = new PathPlanningService.PathNode();
        node2.setNodeId(5L);
        node2.setName("太和殿");
        node2.setLatitude(BigDecimal.valueOf(39.9168));
        node2.setLongitude(BigDecimal.valueOf(116.3975));
        node2.setSequence(1);
        node2.setAction("end");

        result.setNodes(Arrays.asList(node1, node2));
        return result;
    }
}
