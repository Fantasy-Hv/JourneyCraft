# Navigation 模块记录

> **负责人**: 刘方正  
> **分支**: `back-liu-feature`  
> **最后更新**: 2026-04-30  
> **Java 文件数**: 52  

---

## 一、模块进度总览

| 功能领域 | 进度 | 说明 |
|---------|------|------|
| 路网数据管理 | ✅ 100% | RoadNode/RoadEdge CRUD、OSM 批量导入、缓存 |
| 单目标路径规划 | ✅ 100% | Dijkstra/A* 双算法，策略参数完整生效，支持 3 种策略 + 3 种交通方式 |
| 多目标路线规划 | ✅ 100% | TSP 变种算法，支持返回起点、按访问顺序分段 |
| 附近设施查询 | ✅ 100% | 基于路网实际路径距离排序（非直线距离） |
| 实时拥挤度 | ✅ 100% | 数据记录、等级查询（调用 Scenic 模块 CrowdLevel） |
| 路线保存与历史 | ✅ 100% | 路线持久化、导航历史记录 |
| 拍照点推荐 | 🟡 80% | 数据表+Service+Controller 已建，跨模块接口待补 |
| 室内导航 | 🟡 30% | 数据表+Service 已建，Controller 为桩代码，算法未实现 |
| 反向游览建议 | 🟡 10% | Controller 为桩代码，算法未实现 |
| **整体** | **~90%** | 核心算法完备（Dijkstra + A*），策略切实生效，api 跨模块接口已定义，剩余均为桩代码补齐 |

---

## 二、已实现功能详情

### 2.1 单目标路径规划 ✅
- **算法**: Dijkstra 最短路径 **+ A\*（Haversine 启发式）**，基于自设计数据结构实现
- **策略**: `shortest_distance`（最短距离）、`shortest_time`（最短时间，按交通方式取对应时间字段）、`avoid_crowd`（避开拥挤，距离 × (1+拥挤度) 加权）
- **交通方式**: `walk`（步行）、`bike`（自行车）、`shuttle`（电瓶车）
- **接口**: `POST /api/navigation/route?algorithm=dijkstra|astar`
- **核心类**: `PathPlanningService.calculateShortestPath()` / `calculateAStarPath()` → `PathPlanningServiceImpl`

### 2.2 多目标路线规划 ✅
- **算法**: TSP 变种（贪心 + 全排列优化），基于自设计数据结构实现
- **特性**: 支持返回起点（`needReturn=true`）、按访问顺序分段返回每段详情
- **接口**: `POST /api/navigation/multi-route`
- **核心类**: `PathPlanningService.calculateMultiTargetRoute()` → `PathPlanningServiceImpl`

### 2.3 附近设施查询 ✅
- **特性**: 基于**路网实际路径距离**排序（非直线距离），满足课程设计要求
- **流程**: 设施列表 → 查找关联路网节点 → Dijkstra计算实际距离 → 按距离排序 → Top-N截断
- **接口**: `GET /api/navigation/facilities/nearby`
- **注意**: 当前使用 `TempFacilityService`（临时桩表），待 Scenic 模块的 Facility 接口稳定后替换调用

### 2.4 实时拥挤度查询 ✅
- **特性**: 调用 Scenic 模块的 `ScenicService.getCrowdLevelsByScenicArea()` 获取节点拥挤数据
- **返回**: 整体拥挤等级 + 各节点红/黄/绿标识
- **接口**: `GET /api/navigation/congestion/{scenicId}`
- **数据来源**: 结合 Scenic 模块的 `CrowdLevel` 表

### 2.5 路线保存 ✅
- **功能**: 每次路径规划后自动保存路线到 `t_navigation_route` 表
- **存储**: JSON 字段存储路径节点序列
- **Service**: `NavigationRouteService` / `NavigationRouteServiceImpl`

### 2.6 导航历史 ✅
- **功能**: 记录用户导航历史（`t_navigation_history` 表）
- **Service**: `NavigationHistoryService` / `NavigationHistoryServiceImpl`

### 2.7 路网数据管理 ✅
- **RoadNode**: 节点 CRUD，支持 OSM 数据导入、景区/建筑/设施关联
- **RoadEdge**: 路径段 CRUD，支持距离/时间/交通方式/拥挤度/双向标记
- **OSM 导入**: 批量导入 OpenStreetMap 数据，带日志追踪
- **缓存**: `RouteCacheService` 对已计算路径进行缓存，支持过期清理

### 2.8 路径缓存 ✅
- **表**: `t_navigation_route_cache`
- **策略**: 按 `(起点, 终点, 交通方式, 策略)` 组合缓存，记录命中次数
- **过期**: MySQL Event 每日自动清理过期缓存

---

## 三、对外提供的接口

### 3.1 HTTP API（Controller 层）

| 方法 | 路径 | 状态 | 说明 |
|------|------|------|------|
| POST | `/api/navigation/route` | ✅ | 单目标路径规划 |
| POST | `/api/navigation/multi-route` | ✅ | 多目标路线规划 |
| GET | `/api/navigation/facilities/nearby` | ✅ | 附近设施查询（路网距离排序） |
| POST | `/api/navigation/indoor/route` | 🟡 | 室内导航（桩代码） |
| GET | `/api/navigation/congestion/{scenicId}` | ✅ | 实时拥挤度 |
| GET | `/api/navigation/alternative-route` | 🟡 | 反向游览建议（桩代码） |
| GET | `/api/navigation/health` | ✅ | 健康检查 |

### 3.2 Service 层（供其他模块调用）

| Service 接口 | 核心方法 | 说明 |
|-------------|---------|------|
| `PathPlanningService` | `calculateShortestPath()` | 单目标路径规划（Dijkstra） |
| `PathPlanningService` | `calculateAStarPath()` | 单目标路径规划（A*，Haversine 启发式） |
| `PathPlanningService` | `calculateMultiTargetRoute()` | 多目标路线规划（TSP 贪心） |
| `PathPlanningService` | `getDistanceBetweenNodes()` | 获取两节点实际路径距离 |
| `PathPlanningService` | `getTimeBetweenNodes()` | 获取两节点预计时间 |
| `PathPlanningService` | `getFacilityNodeId()` | 获取设施关联的路网节点ID |
| `PathPlanningService` | `findNearestNodeByCoords()` | 查找距离坐标最近的路网节点（Haversine） |
| `RoadNodeService` | CRUD + 按景区/建筑查询 | 路网节点管理 |
| `RoadEdgeService` | CRUD + 按节点/交通方式查询 | 路径段管理 |
| `CrowdLevelService` | CRUD + 按节点/时间查询 | 拥挤度数据管理 |
| `PhotoSpotService` | CRUD + 按景区/评分查询 | 拍照点数据管理 |
| `NavigationRouteService` | save/list/getById | 路线持久化 |
| `NavigationHistoryService` | record/list/getByUser | 导航历史记录 |

> **跨模块调用约定**: 其他模块通过 `api` 包下的 `NavigationService` 接口使用 navigation 功能（遵循 `模块调用约定.md`）。
> - **接口类**: `api/NavigationService.java`
> - **实现类**: `service/impl/NavigationApiServiceImpl.java`
> - **使用方式**: `@Resource NavigationService navigationService;`
> - **暴露方法**: `planSingleRoute()`, `planSingleRouteAStar()`, `planMultiRoute()`, `getDistanceBetweenNodes()`, `getTimeBetweenNodes()`, `getNearbyFacilities()`, **`getDistanceToTarget()`（新增：供 Recommend 模块）**

---

## 四、管理的数据库表

### 4.1 临时桩表（供独立开发，后续由 Scenic 模块替换）

| 表名 | 说明 | 用途 |
|------|------|------|
| `t_temp_scenic_area` | 临时景区表 | 模拟 Scenic 模块的 ScenicArea |
| `t_temp_building` | 临时建筑表 | 模拟 Scenic 模块的 Building |
| `t_temp_facility` | 临时设施表 | 模拟 Scenic 模块的 Facility |

> ⚠️ **注意**: 这 3 张桩表仅在 Navigation 模块独立开发期间使用。Scenic 模块完成后，Navigation 应通过 Scenic 模块的 `api` 接口获取数据，不再使用桩表。

### 4.2 Navigation 核心表（9 张）

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `t_navigation_road_node` | 路网节点表 | osm_id, scenic_area_id, node_type(0-5), latitude, longitude, floor_number |
| `t_navigation_road_edge` | 路径段表 | from_node_id, to_node_id, distance, walk/bike/shuttle_time, transport_type, congestion_level |
| `t_navigation_crowd_level` | 拥挤度记录表 | node_id, level(0-3), crowd_count, capacity, source(0-2), recorded_at |
| `t_navigation_photo_spot` | 拍照点推荐表 | scenic_area_id, node_id, target_name, recommended_angle, best_time, rating, check_in_count |
| `t_navigation_route_cache` | 路径规划缓存表 | start_node_id, end_node_id, transport_type, route_nodes(JSON), expires_at, hit_count |
| `t_navigation_osm_import_log` | OSM 导入日志表 | file_name, import_type, total_records, success_count, status, processing_time_ms |
| `t_navigation_indoor_floor` | 室内楼层表 | building_id, floor_number, floor_name, indoor_data(JSON), elevator/stair_node_id |
| `t_navigation_route` | 导航路线表 | user_id, scenic_area_id, start_node_id, end_node_ids(JSON), path_nodes(JSON), total_distance, strategy |
| `t_navigation_history` | 导航历史表 | user_id, scenic_area_id, start_node_id, end_node_id, path_nodes(JSON), navigated_at |

### 4.3 数据库视图（3 个）

| 视图名 | 说明 |
|--------|------|
| `v_navigation_important_nodes` | 重要节点视图（is_important=1 且已启用的节点） |
| `v_navigation_walkable_edges` | 可步行路径视图（筛选 transport_type 支持步行的边） |
| `v_navigation_realtime_congestion` | 实时拥挤度视图（每个节点最新一条拥挤度记录） |

### 4.4 数据库函数与事件

| 名称 | 类型 | 说明 |
|------|------|------|
| `calculate_distance(lat1, lon1, lat2, lon2)` | 函数 | Haversine 公式计算两点直线距离 |
| `ev_navigation_cleanup` | 事件 | 每日自动清理过期缓存（30天）、旧拥挤度记录（30天）、旧 OSM 导入日志（90天） |

### 4.5 测试数据

- 1 个景区（故宫博物院）
- 3 座建筑（太和殿、乾清宫、养心殿）
- 13 个路网节点 + 27 条路径段（模拟故宫游览路线）
- 4 个拍照点
- 5 条拥挤度记录

---

## 五、待完成事项

| 优先级 | 事项 | 预计工作量 | 状态 |
|--------|------|-----------|------|
| ~~P0~~ | ~~策略参数生效（shortest_time / avoid_crowd 边权计算）~~ | ~~1.5h~~ | ✅ 已完成 |
| ~~P0~~ | ~~A* 算法实现（Haversine 启发式）~~ | ~~2h~~ | ✅ 已完成 |
| ~~P0~~ | ~~api 包对外接口定义（NavigationService）~~ | ~~1h~~ | ✅ 已完成 |
| P1 | 实现室内导航算法（电梯/楼梯跨楼层路径） | 3h | 待开发 |
| P1 | 实现反向游览建议算法（基于拥挤度） | 2h | 待开发 |
| P1 | 路径缓存接入 PathPlanningService 计算流程 | 1h | 待开发 |
| P2 | 多交通工具混合路径（hybrid 模式） | 3h | 待开发 |
| P2 | 移除临时桩表，改为调用 Scenic 模块的 api 接口 | 1h | 依赖王哲 |
| P2 | 算法性能对比分析（Dijkstra vs A* vs Greedy TSP） | 2h | 待开发 |

---

*本文档遵循项目组「模块记录」规定，随模块开发进度同步更新。*
