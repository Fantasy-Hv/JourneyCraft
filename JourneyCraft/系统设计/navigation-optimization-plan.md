# Navigation 模块优化方案 — 前后端接口与实现说明

> **版本**: v2.0  
> **日期**: 2026-05-07  
> **基于**: OSM 北京昌平区路网数据（420,223 节点 / 263,292 边）  
> **调研来源**: GraphHopper、OSRM、Valhalla、OpenTripPlanner 源码级分析  
> **实现约束**: **仅修改 navigation 模块**。跨模块依赖只使用已有的 `scenic.api.ScenicService`（不新增 scenic 接口）

---

## 一、方案总览

### 1.1 核心问题

当前 OSM 路网数据存在 **transport 分区问题**：

- 93,890 条 `path` 边 → **仅步行**（transport_type=1）
- 20,143 条 `footway` 边 → **仅步行**（transport_type=1）
- 3,894 条 `cycleway` 边 → **仅骑行**（transport_type=2）
- 31,157 条 `residential` 边 → 步行+自行车（transport_type=4）
- 仅 **176 个节点** 桥接 footway ↔ cycleway 子图

导致：用户选择景区作为导航目标时，系统不知道应路由到哪个路网节点；bike 模式被 footway 子图阻断。

### 1.2 修正方案三大支柱

| 支柱 | 问题 | 方案 | 参考引擎 |
|------|------|------|---------|
| **Snap-to-Edge** | 随机地图选点 | 空间索引找最近可达边 + SCC 过滤 + 虚拟投影点 | OSRM PhantomNode + GraphHopper QueryGraph |
| **Push-Bike / Auto-Pedestrian** | 多交通方式混合 | 每种模式内建步行 fallback，带速度降级和模式切换惩罚 | OSRM bicycle.lua + Valhalla auto_pedestrian |
| **取消 Hybrid** | hybrid 语义模糊 | 改为 bike 模式内建 push-bike；vehicle 模式内建首尾步行 | 业界无 hybrid 概念 |

### 1.3 交通方式定义

| 模式 | 默认行为 | Fallback | 惩罚 | 速度映射 |
|------|---------|---------|------|---------|
| **walk** | 步行所有 walkable 边 | — | — | 统一 4 km/h |
| **bike** | 骑行 bike-capable 边 | push-bike 步行（全程可穿插） | +30s/次 | cycleway=18, residential=18, footway=4, path=6 km/h |
| **shuttle** | 行驶 vehicle-capable 边 | 首尾各 ≤500m 步行 | +60s/次 | motorway=100, trunk=90, primary=70, service=30, footway=4 km/h |

---

## 二、全部 API 接口定义

> **坐标约定**: 数据库存储 WGS-84（来自 OSM）。前端收发 GCJ-02（高德地图）。Controller 出口统一做 WGS-84 → GCJ-02 转换；入口做 GCJ-02 → WGS-84。

### 2.1 统一搜索 `GET /api/search`

**用途**: 搜索框输入，同时搜索景区和命名路网节点。**keyword 为空时只返回景区**（路网节点无热度字段，空 keyword 不返回节点）。

```
请求:
  GET /api/search?keyword=故宫&types=scenic,node&limit=10&page=1

参数:
  keyword    String   可选  搜索关键词（模糊匹配，为空时只返回景区前10）
  types      String   可选  搜索类型: scenic,node（逗号分隔，默认全部）
  limit      Integer  可选  每页数量（默认 10）
  page       Integer  可选  页码（默认 1）

响应 200:
{
  "code": 200,
  "data": {
    "scenicResults": [
      {
        "id": 1,
        "name": "故宫博物院",
        "type": "scenic",
        "scenicType": 0,           // 0=景区, 1=校园
        "city": "北京",
        "latitude": 39.9124,        // GCJ-02
        "longitude": 116.4039,      // GCJ-02
        "rating": 4.9,
        "heatScore": 9800,
        "matchField": "name"
      }
    ],
    "nodeResults": [
      {
        "id": 419685,
        "osmId": 13718887090,
        "name": "西关环岛",
        "type": "road_node",
        "nodeType": 1,              // 0=入口, 1=路口, 2=POI
        "latitude": 40.0791,        // GCJ-02
        "longitude": 116.3479,      // GCJ-02
        "transportTypes": [1, 4, 5], // 该节点连接的边支持的交通方式
        "matchField": "name"
      }
    ],
    "total": 15,
    "page": 1,
    "size": 10
  }
}
```

**后端逻辑**: 
1. 调用 `ScenicService.searchScenic()` → 获取景区搜索结果（遵循跨模块约定）
2. `t_navigation_road_node` WHERE name IS NOT NULL AND name LIKE CONCAT('%', keyword, '%')（路网节点是 navigation 自身数据）
3. 合并结果 → 坐标转换 → 分页返回

---

### 2.2 地图点击最近边 `GET /api/navigation/nearest-edge`

**用途**: 用户点击地图任意位置，返回最近的可达路网边（非节点）。

```
请求:
  GET /api/navigation/nearest-edge?lat=39.9124&lng=116.4039&transportMode=bike&radius=2000

参数:
  lat             Double   必填  纬度 (GCJ-02)
  lng             Double   必填  经度 (GCJ-02)
  transportMode   String   可选  过滤交通方式: walk/bike/shuttle（默认 walk）
  radius          Integer  可选  搜索半径米（默认 1000，最大 5000）

响应 200:
{
  "code": 200,
  "data": {
    "edgeId": 262800,
    "snapLat": 39.9128,             // 点击投影到边上的位置 (GCJ-02)
    "snapLng": 116.4045,
    "snapPosition": 0.35,           // 投影位置在边上的比例 (0~1)
    "distance": 12.3,               // 点击位置到投影点的距离(米)
    "edgeInfo": {
      "osmWayId": 1493545173,
      "fromNodeId": 418811,
      "toNodeId": 418812,
      "distance": 14.20,            // 边长(米)
      "highwayType": "service",
      "transportModes": ["walk", "bike"],     // 后端从 transport_type 标量翻译
      "name": null
    },
    "connectedComponentSize": 15623,
    "confidence": "high",
    "alternatives": [                          // confidence 非 high 时提供
      { "type": "nearest_walkable", "nodeId": 67890, "distance": 850 },
      { "type": "nearest_bikeable",  "nodeId": 67891, "distance": 1200 }
    ]
  }
}

响应 404:
{ "code": 404, "message": "指定半径内未找到可达路网边" }
```

**后端逻辑**:
1. GCJ-02 → WGS-84
2. 空间索引查询候选边（`ST_Distance_Sphere` + `MBRContains`）
3. 按 transportMode 过滤可达边（含 walking fallback 判断）
4. 计算候选边所在 SCC 大小
5. 排除 SCC < 100 的小分量
6. 选最近可达边 → 计算投影点 → WGS-84 → GCJ-02

---

### 2.3 景区接入点 `GET /api/navigation/scenic/{scenicAreaId}/access-nodes`

> **数据来源**: 调用 `ScenicService.getScenicDetail()` 获取景区名称/坐标；路网节点数据来自 navigation 自身表。

**用途**: 用户选中景区后，展示该景区在各交通方式下的接入路网节点。

```
请求:
  GET /api/navigation/scenic/1/access-nodes?maxDistance=1500

参数:
  maxDistance  Integer  可选  搜索半径米（默认 1500）

响应 200:
{
  "code": 200,
  "data": {
    "scenicAreaId": 1,
    "scenicAreaName": "故宫博物院",
    "scenicCenter": {
      "latitude": 39.9163,          // GCJ-02
      "longitude": 116.3971
    },
    "accessNodes": {
      "walk": [
        {
          "nodeId": 12345,
          "osmId": 269422471,
          "name": "东门入口",
          "nodeType": 0,            // 入口
          "latitude": 39.9170,      // GCJ-02
          "longitude": 116.3980,
          "distance": 120,          // 到景区中心距离(米)
          "isPrimary": true,        // 是否主入口
          "connectedComponentSize": 85000
        }
      ],
      "bike": [
        {
          "nodeId": 67890,
          "osmId": 533496561,
          "name": null,
          "latitude": 39.9168,
          "longitude": 116.3975,
          "distance": 350,
          "connectedComponentSize": 52000
        },
        {
          "nodeId": 12345,          // 同一节点可作为 bike 的 push-bike 入口
          "osmId": 269422471,
          "name": "东门入口",
          "latitude": 39.9170,
          "longitude": 116.3980,
          "distance": 120,
          "viaWalk": true,          // 标记：需步行至此
          "walkDistance": 120
        }
      ],
      "shuttle": [
        {
          "nodeId": 99999,
          "osmId": 610822237,
          "name": "故宫停车场",
          "latitude": 39.9155,
          "longitude": 116.3960,
          "distance": 580,
          "connectedComponentSize": 35000
        }
      ]
    }
  }
}
```

**后端逻辑**:
1. 查 `t_scenic_area` 获取景区中心坐标
2. 查 `t_navigation_scenic_access` 获取预处理好的接入点
3. 按 access_type 分组：walk_entry(1) / bike_entry(2) / vehicle_entry(3)
4. 对 bike_entry：如果距离 >500m，同时加入最近的 walk_entry 标记 viaWalk
5. 坐标转换

> **前端注意**: `shuttle` 数组为空时，表示该景区无可车行接入点，前端应**禁用 shuttle 模式按钮**。`bike` 数组为空时同理禁用 bike 模式。`walk` 数组理论上不应为空（每个景区 500m 内必有一个步行可达节点）。

---

### 2.4 景区 POI 展示 `GET /api/navigation/scenic/{scenicAreaId}/poi-nodes`

> **数据来源**: POI 类型从 road_node 的 `osm_tags` JSON 提取；关联设施信息通过 `ScenicService.listFacilities()` 获取。

**用途**: 在地图上展示景区内的 POI（餐厅/厕所/停车场等），仅标注不参与路径计算。

```
请求:
  GET /api/navigation/scenic/1/poi-nodes?types=restaurant,toilet,parking&limit=50

参数:
  types   String   可选  过滤 POI 类型（逗号分隔，默认全部）
  limit   Integer  可选  最大返回数（默认 50）

响应 200:
{
  "code": 200,
  "data": [
    {
      "nodeId": 54321,
      "osmId": 774387181,
      "name": "故宫餐厅",
      "nodeType": 2,              // POI
      "poiType": "restaurant",    // 从 osm_tags.amenity 提取
      "latitude": 39.9156,        // GCJ-02
      "longitude": 116.3978,
      "description": "中式快餐",       // ⚠️ 仅关联 facility 的节点有值，OSM 原生 POI 通常为 null
      "facilityId": null          // 关联设施 ID（可为空）
    },
    {
      "nodeId": 54322,
      "osmId": 1297036357,
      "name": "公共卫生间",
      "nodeType": 2,
      "poiType": "toilet",
      "latitude": 39.9161,
      "longitude": 116.3982,
      "description": null,
      "facilityId": null
    }
  ]
}
```

**后端逻辑**:
1. 查 `t_navigation_road_node` WHERE `display_for_scenic_id = scenicAreaId` OR (`scenic_area_id = scenicAreaId` AND `node_type = 2`)
2. 从 `osm_tags` JSON 提取 `amenity` / `shop` / `tourism` 等标签作为 poiType
3. 可选按 poiType 过滤
4. 坐标转换

**POI 类型枚举**（从 OSM osm_tags 提取）:
`restaurant`, `toilet`, `parking`, `fast_food`, `cafe`, `bank`, `hospital`, `pharmacy`, `supermarket`, `shelter`, `bench`, `bicycle_rental`, `charging_station`, `vending_machine`, `post_office`, `place_of_worship`, `cinema`, `swimming_pool`, `kindergarten`

---

### 2.5 路径规划 `POST /api/navigation/route`

**起点/终点支持四种输入**（`inputType` 对应 `scenicArea` / `node` / `edge` / `coordinate`）：

```
请求:
POST /api/navigation/route
Content-Type: application/json

{
  // 起点（四选一）:
  "startScenicAreaId": 1,         // A) 景区 ID
  // "startNodeId": 419685,       // B) 路网节点 ID（从搜索结果的 nodeResults 中直接使用）
  // "startEdgeId": 262800,       // C) 边 ID + 投影位置（从 nearest-edge 返回）
  //   "startSnapPos": 0.35,
  // "startLat": 39.9124,         // D) 坐标 (GCJ-02)
  //   "startLng": 116.4039,

  // 终点（四选一）:
  "endScenicAreaId": 2,
  // "endNodeId": ...,             // B) 路网节点 ID
  // "endEdgeId": ..., "endSnapPos": ...,
  // "endLat": ..., "endLng": ...,

  "transportMode": "bike",          // walk / bike / shuttle
  "strategy": "shortest_time",      // shortest_distance / shortest_time / avoid_crowd
  "algorithm": "astar"              // dijkstra / astar（默认 astar）
}

响应 200:
{
  "code": 200,
  "data": {
    "routeId": 42,
    "totalDistance": 1530.5,        // 总距离(米)
    "estimatedTime": 1280,          // 预计时间(秒)
    "transportMode": "bike",
    "strategy": "shortest_distance",
    "segments": [                   // 按交通方式分段
      {
        "type": "push_bike",        // walk / bike / push_bike / shuttle / walk_access / walk_egress
        "distance": 120.0,
        "time": 108,                // 4 km/h walking
        "nodeIds": [12345]
      },
      {
        "type": "bike",
        "distance": 1350.5,
        "time": 270,                // 18 km/h cycling
        "nodeIds": [12346, 12347, ..., 67890]
      }
    ],
    "startInfo": {
      "inputType": "scenicArea",    // scenicArea / edge / coordinate
      "scenicAreaId": 1,
      "scenicAreaName": "故宫博物院",
      "resolvedNodeId": 12345,
      "accessType": "walk_entry",
      "latitude": 39.9170,          // GCJ-02
      "longitude": 116.3980
    },
    "endInfo": {
      "inputType": "scenicArea",
      "scenicAreaId": 2,
      "scenicAreaName": "虎谷风景区",
      "resolvedNodeId": 67890,
      "accessType": "bike_entry",
      "latitude": 40.2710,
      "longitude": 116.1430
    },
    "path": [                       // 前端绘制路线的点序列
      { "lat": 39.9170, "lng": 116.3980, "segmentType": "push_bike" },
      { "lat": 39.9165, "lng": 116.3978, "segmentType": "bike" },
      ...
      { "lat": 40.2710, "lng": 116.1430, "segmentType": "bike" }
    ]
  }
}
```

**后端逻辑**（见第四部分实现说明）:
1. 解析输入：scenicAreaId → 查 `t_navigation_scenic_access` 取接入节点；edgeId → 查边+创建虚拟节点；坐标 → `nearest-edge` 逻辑
2. 构建邻接表：按 `transportMode` 过滤边，加载到内存 `Map<Long, List<RoadEdge>>`
3. 执行算法：Dijkstra 或 A*（Haversine 启发式）
4. 边权计算：见 §4.3
5. 结果分段标记 + 坐标转换

---

### 2.6 多目标路径规划 `POST /api/navigation/multi-route`

**用途**: 途经多目标的路径规划（TSP 变种）。

```
请求:
POST /api/navigation/multi-route
Content-Type: application/json

{
  "startScenicAreaId": 1,
  "targets": [
    { "scenicAreaId": 2 },
    { "nodeId": 67890 },
    { "lat": 40.2200, "lng": 116.2280 }
  ],
  "transportMode": "walk",
  "strategy": "shortest_distance",
  "needReturn": true                // 是否返回起点
}

响应 200:
{
  "code": 200,
  "data": {
    "totalDistance": 5200.5,
    "totalTime": 4680,
    "transportMode": "walk",
    "segments": [
      {
        "order": 1,
        "from": { "nodeId": 12345, "name": "东门入口", "lat": GCJ-02, "lng": GCJ-02 },
        "to": { "scenicAreaId": 2, "name": "虎谷风景区", "lat": GCJ-02, "lng": GCJ-02 },
        "distance": 1530.5,
        "time": 1280,
        "path": [...]                // GCJ-02 坐标序列
      },
      ...
    ],
    "visitOrder": [1, 2, 0]         // 访问顺序（0-based，表示先 targets[1]、再 targets[2]、最后 targets[0]）
  }
}
```

---

### 2.7 附近设施查询 `GET /api/navigation/facilities/nearby`

**用途**: 查询某位置附近的设施（按路网距离排序）。

```
请求:
  GET /api/navigation/facilities/nearby?lat=39.9124&lng=116.4039&transportMode=walk&radius=2000&facilityTypes=1,2&limit=10

参数:
  lat             Double   可选  纬度 (GCJ-02，与 nodeId 二选一)
  lng             Double   可选  经度
  nodeId          Long     可选  节点 ID（与坐标二选一）
  transportMode   String   可选  交通方式，影响可达性判断
  radius          Integer  可选  搜索半径米（默认 2000）
  facilityTypes   String   可选  设施类型过滤: 0=卫生间, 1=餐饮, 2=超市...
  limit           Integer  可选  最大返回数（默认 10）

响应 200:
{
  "code": 200,
  "data": {
    "sourceNodeId": 12345,
    "sourceName": "...",
    "sourceLat": 39.9124,           // GCJ-02
    "sourceLng": 116.4039,
    "facilities": [
      {
        "facilityId": 101,
        "name": "故宫餐厅",
        "facilityType": 1,          // 餐饮
        "subtype": "中式快餐",
        "latitude": 39.9156,        // GCJ-02
        "longitude": 116.3978,
        "roadDistance": 580.3,      // 路网实际路径距离(米)
        "straightDistance": 450.0,  // 直线距离(米)
        "walkTime": 435,            // 预计步行时间(秒)
        "rating": 4.5
      }
    ]
  }
}
```

**后端逻辑**: 同原方案，但起点解析统一使用 `resolveRouteInput()` 的坐标处理逻辑。

---

### 2.8 健康检查 `GET /api/navigation/health`

```
响应 200:
{
  "code": 200,
  "data": {
    "status": "ok",
    "nodeCount": 420223,
    "edgeCount": 263292,
    "scenicCount": 10,
    "spatialIndexReady": true
  }
}
```

---

### API 总览表

| # | 方法 | 路径 | 用途 | 新增/修改 |
|---|------|------|------|----------|
| 1 | GET | `/api/search` | 统一搜索景区+路网节点 | **新增** |
| 2 | GET | `/api/navigation/nearest-edge` | 地图点击→最近可达边 | **新增** |
| 3 | GET | `/api/navigation/scenic/{id}/access-nodes` | 景区多交通接入点 | **新增** |
| 4 | GET | `/api/navigation/scenic/{id}/poi-nodes` | 景区POI展示节点 | **新增** |
| 5 | POST | `/api/navigation/route` | 单目标路径规划 | **修改** |
| 6 | POST | `/api/navigation/multi-route` | 多目标路径规划 | **修改** |
| 7 | GET | `/api/navigation/facilities/nearby` | 附近设施查询 | **修改** |
| 8 | GET | `/api/navigation/health` | 健康检查 | 不变 |

> **拥挤度接口归属**: `GET /api/navigation/congestion/{id}` 当前存在于 navigation，但数据和聚合逻辑实际属于 scenic 模块（`t_crowd_level`）。本方案不修改此接口——应由 scenic 模块提供独立的拥挤度 REST 端点，navigation 仅作为内部消费者调用 `ScenicService.getCrowdLevelsByScenicArea()`。前端如需拥挤度数据，建议直接调用 scenic 模块的对应接口。

---

## 三、坐标转换方案

### 3.1 坐标系约定

| 位置 | 坐标系 | 说明 |
|------|--------|------|
| MySQL 数据库 | **WGS-84** | OSM 原始数据，GPS 坐标 |
| 后端计算 | **WGS-84** | 内部所有距离/路径计算使用 WGS-84 |
| Controller 入参 | **GCJ-02** | 前端高德地图发送的坐标 |
| Controller 出参 | **GCJ-02** | 返回给前端的所有坐标 |
| 前端渲染 | **GCJ-02** | 高德地图 API 原生坐标系 |

### 3.2 转换时机

```
前端发送请求 (GCJ-02)
    ↓ Controller.入口
gcj02ToWgs84() ──→ 后端 WGS-84 计算
    ↓ Controller.出口
wgs84ToGcj02() ──→ 前端收到 GCJ-02
```

### 3.3 工具类位置

`navigation/utils/CoordinateTransformUtil.java`

```java
public class CoordinateTransformUtil {
    // WGS-84 → GCJ-02（数据库 → 前端）
    public static Gcj02Coord wgs84ToGcj02(double lat, double lng);
    
    // GCJ-02 → WGS-84（前端 → 数据库）
    public static Wgs84Coord gcj02ToWgs84(double lat, double lng);
    
    // 批量转换
    public static List<Gcj02Coord> wgs84ToGcj02Batch(List<Wgs84Coord> coords);
    
    public record Gcj02Coord(double lat, double lng) {}
    public record Wgs84Coord(double lat, double lng) {}
}
```

**算法**: 中国国标 GCJ-02 偏移算法。北京地区典型偏移量约 300-500 米。

---

## 四、Navigation 内部实现说明

### 4.1 新增数据表

```sql
-- 景区 ↔ 路网接入点关联表
CREATE TABLE t_navigation_scenic_access (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    scenic_area_id  BIGINT NOT NULL,
    access_type     TINYINT NOT NULL,       -- 1=walk, 2=bike, 3=vehicle, 4=poi
    road_node_id    BIGINT NOT NULL,
    transport_types TINYINT NOT NULL,       -- 节点支持的 transport_type 位掩码
    rank_order      INT DEFAULT 0,          -- 优先级
    distance_to_scenic DECIMAL(10,2),
    is_primary      TINYINT DEFAULT 0,
    is_deleted      TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_scenic_access (scenic_area_id, access_type, road_node_id)
);

-- road_node 新增字段
ALTER TABLE t_navigation_road_node 
  ADD COLUMN display_for_scenic_id BIGINT DEFAULT NULL,
  ADD COLUMN geom GEOMETRY SRID 4326 
    GENERATED ALWAYS AS (ST_PointFromText(CONCAT('POINT(', latitude, ' ', longitude, ')'))) STORED,
  -- 注意：MySQL ST_PointFromText 在 SRID 4326 下期望 POINT(lat lng) 顺序
  ADD SPATIAL INDEX idx_spatial_geom (geom);
```

### 4.2 新增/修改文件清单

| 层 | 文件 | 说明 |
|----|------|------|
| **common** | *(无修改 — 使用已有 ScenicService API)* | 跨模块调用 scenic |
| **navigation/utils** | `CoordinateTransformUtil.java` | WGS-84 ↔ GCJ-02 转换 |
| **navigation/controller** | `SearchController.java` | 统一搜索端点 |
| **navigation/controller** | `NavigationController.java` | 修改：nearest-edge + 灵活输入 + access-nodes + poi-nodes |
| **navigation/mapper** | `SpatialRoadNodeMapper.java` | 空间索引最近边查询 |
| **navigation/mapper** | `RoadNodeMapper.java` | 修改：增加按 scenic 的 POI 查询 |
| **navigation/mapper** | `ScenicAccessMapper.java` | 景区接入点 CRUD |
| **navigation/entity** | `ScenicAccess.java` | 景区接入点实体 |
| **navigation/service** | `ScenicAccessService.java` | 接入点解析逻辑 |
| **navigation/service/impl** | `PathPlanningServiceImpl.java` | **核心修改** |

### 4.3 路径规划核心算法改动

#### 4.3.1 边权计算（push-bike / auto-pedestrian 模型）

```java
/**
 * 计算边在指定交通方式下的权重
 * 
 * @param edge          路网边
 * @param transportMode 交通方式 (walk/bike/shuttle)
 * @param strategy      策略 (shortest_distance/shortest_time/avoid_crowd)
 * @param isAccessEgress 是否为起点/终点的步行 access/egress 段（仅 shuttle 模式使用）
 * @return 权重值（秒），INFINITY 表示不可达
 */
private double getEdgeWeight(RoadEdge edge, String transportMode, 
                              String strategy, boolean isAccessEgress) {
    switch (transportMode) {
        case "walk":
            if (!edge.isWalkable()) return INFINITY;
            return edge.getWalkTime();  // 步行时间（秒）

        case "bike":
            if (edge.getTransportType().allowsBike()) {
                // 正常骑行
                return getEdgeTimeByStrategy(edge, "bike", strategy);
            } else if (edge.getTransportType().allowsWalk()) {
                // push-bike: 步行速度 + 模式切换惩罚
                return edge.getWalkTime() + PUSH_BIKE_PENALTY_SECONDS;  // +30s
            }
            return INFINITY;

        case "shuttle":
            if (edge.getTransportType().allowsVehicle()) {
                return getEdgeTimeByStrategy(edge, "shuttle", strategy);
            }
            // 仅首尾 access/egress 阶段允许步行
            if (isAccessEgress && edge.getTransportType().allowsWalk()) {
                if (edge.getDistance() > MAX_ACCESS_EGRESS_DISTANCE) return INFINITY;
                return edge.getWalkTime() + AUTO_PEDESTRIAN_PENALTY_SECONDS;  // +60s
            }
            return INFINITY;
    }
    return INFINITY;
}
```

#### 4.3.2 速度/时间映射表（基于 GraphHopper 源码）

```java
// 步行速度常数
private static final double WALK_SPEED_MPS = 1.2;    // 4.3 km/h

// bike 模式速度 (km/h → m/s)
private static final Map<String, Double> BIKE_SPEEDS = Map.ofEntries(
    entry("cycleway",       5.0),   // 18 km/h
    entry("residential",    5.0),   // 18 km/h
    entry("unclassified",   5.0),
    entry("service",        5.0),
    entry("living_street",  3.3),   // 12 km/h
    entry("track",          3.3),   // 12 km/h
    entry("path",           1.7),   // 6 km/h (push)
    entry("footway",        1.1),   // 4 km/h (push)
    entry("pedestrian",     1.1),   // 4 km/h (push)
    entry("steps",          0.6)    // 2 km/h (push, very slow)
);

// shuttle 模式速度
private static final Map<String, Double> SHUTTLE_SPEEDS = Map.ofEntries(
    entry("motorway",      27.8),   // 100 km/h
    entry("trunk",         25.0),   // 90 km/h
    entry("primary",       19.4),   // 70 km/h
    entry("secondary",     16.7),   // 60 km/h
    entry("tertiary",      15.3),   // 55 km/h
    entry("residential",   13.9),   // 50 km/h
    entry("service",        8.3),   // 30 km/h
    entry("living_street",  2.8)    // 10 km/h
);
```

#### 4.3.3 输入解析（`resolveRouteInput`）

```java
/**
 * 统一解析导航起点/终点
 * 支持三种输入: scenicAreaId / edgeId+snapPos / 坐标(lat,lng)
 * 
 * @return 解析结果: 路网节点ID + 接入说明
 */
RouteInputResult resolveRouteInput(InputType type, Long scenicAreaId,
    Long edgeId, Double snapPos, Double lat, Double lng, String transportMode) {
    
    switch (type) {
        case SCENIC_AREA:
            // 查询景区接入点
            List<ScenicAccess> accesses = scenicAccessMapper
                .selectByScenicAndMode(scenicAreaId, transportMode);
            if (accesses.isEmpty()) {
                // 无接入点时，用空间索引找最近可达边
                ScenicItemRspVO scenic = scenicService.getScenicDetail(scenicAreaId);
                return resolveFromCoordinate(
                    scenic.getLatitude().doubleValue(),
                    scenic.getLongitude().doubleValue(),
                    transportMode);
            }
            // 选 rank_order 最小的接入点
            ScenicAccess best = accesses.stream()
                .min(Comparator.comparingInt(ScenicAccess::getRankOrder))
                .orElseThrow();
            return new RouteInputResult(best.getRoadNodeId(), best.getAccessType());

        case EDGE:
            // 边 + 投影位置 → 创建虚拟起始点
            RoadEdge edge = roadEdgeService.getById(edgeId);
            // 将边的两个端点作为可选的起始节点
            // 优先选距离投影点近的端点
            return resolveVirtualNode(edge, snapPos);

        case COORDINATE:
            // 坐标 → 空间索引找最近可达边
            return resolveFromCoordinate(lat, lng, transportMode);
    }
}
```

#### 4.3.4 邻接表构建（替代当前 N+1 查询）

```java
/**
 * 按景区范围加载邻接表到内存
 * 替代当前每条边的逐条数据库查询
 */
Map<Long, List<RoadEdge>> buildAdjacencyList(Long scenicAreaId, String transportMode) {
    // 1. 查询景区关联的所有路网节点
    List<Long> nodeIds = roadNodeMapper.selectIdsByScenicArea(scenicAreaId);
    
    // 2. 一次性查询这些节点的所有出边
    List<RoadEdge> edges = roadEdgeMapper.selectByFromNodeIds(nodeIds);
    
    // 3. 构建邻接表 Map<fromNodeId, List<RoadEdge>>
    // 4. 预过滤：标记每条边在当前 transportMode 下的可达性和权重
    return edges.stream()
        .filter(e -> getEdgeWeight(e, transportMode, strategy, false) < INFINITY)
        .collect(Collectors.groupingBy(RoadEdge::getFromNodeId));
}
```

### 4.4 SCC 计算（连通分量分析）

用于 nearest-edge 和路径规划的连通性判断。

> **性能关键**: SCC BFS 必须在**内存邻接表**上执行，禁止逐节点查数据库。对于 420K 节点的图，BFS 中每条边一次 DB 查询会导致超时（分钟级）。应先通过 `buildAdjacencyList()` 将指定景区的边全部加载到 `Map<Long, List<RoadEdge>>`，再在此内存结构上运行 BFS。

```java
/**
 * 计算指定节点所在连通分量的大小
 * 使用内存 BFS，只考虑指定 transport_mode 下的可达边
 * @param adjacencyList 预加载的内存邻接表（由 buildAdjacencyList() 构建）
 */
int computeSCCSize(Long startNodeId, String transportMode,
                   Map<Long, List<RoadEdge>> adjacencyList) {
    Set<Long> visited = new HashSet<>();
    Queue<Long> queue = new LinkedList<>();
    queue.add(startNodeId);
    visited.add(startNodeId);
    
    while (!queue.isEmpty()) {
        Long nodeId = queue.poll();
        List<RoadEdge> edges = adjacencyList.getOrDefault(nodeId, Collections.emptyList());
        for (RoadEdge edge : edges) {
            if (getEdgeWeight(edge, transportMode, "shortest_distance", false) < INFINITY) {
                if (visited.add(edge.getToNodeId())) {
                    queue.add(edge.getToNodeId());
                }
            }
        }
    }
    return visited.size();
}
```

### 4.5 预填充脚本逻辑

`数据库构建/populate_scenic_access.sql`:

```sql
-- 对每个景区，分类填充接入点
-- walk_entry: 景区 500m 内最近的可步行节点
-- bike_entry: 景区 1000m 内最近的 bike-capable 节点
-- vehicle_entry: 景区 2000m 内最近的 vehicle-capable 节点
-- poi_display: 景区 1000m 内 node_type=2 且有 name 的 POI 节点

-- 示例 (伪代码):
INSERT INTO t_navigation_scenic_access (scenic_area_id, access_type, road_node_id, ...)
SELECT 
    sa.id,
    1,  -- walk_entry
    (SELECT rn.id FROM t_navigation_road_node rn
     WHERE ST_Distance_Sphere(rn.geom, ST_PointFromText(CONCAT('POINT(', sa.latitude, ' ', sa.longitude, ')'), 4326))
           < 500
     AND rn.is_deleted = 0 AND rn.is_enabled = 1
     AND EXISTS (SELECT 1 FROM t_navigation_road_edge re 
                 WHERE re.is_deleted = 0 AND re.from_node_id = rn.id AND re.transport_type IN (1,4,5))
     ORDER BY ST_Distance_Sphere(rn.geom, ST_PointFromText(CONCAT('POINT(', sa.latitude, ' ', sa.longitude, ')'), 4326))
     LIMIT 3),
    1
FROM t_scenic_area sa WHERE sa.is_deleted = 0;
```

---

## 五、前端交互映射速查

| 前端操作 | API | 关键参数 |
|---------|-----|---------|
| 🔍 搜索框输入 | `GET /api/search` | keyword, types |
| 📍 点击地图 | `GET /api/navigation/nearest-edge` | lat, lng, transportMode |
| 📋 查看景区接入点 | `GET /api/navigation/scenic/{id}/access-nodes` | scenicAreaId |
| 🗺️ 加载景区 POI 标记 | `GET /api/navigation/scenic/{id}/poi-nodes` | scenicAreaId, types |
| ▶️ 开始导航（选景区） | `POST /api/navigation/route` | startScenicAreaId, endScenicAreaId, transportMode |
| ▶️ 开始导航（地图选点） | `POST /api/navigation/route` | startEdgeId, startSnapPos, endEdgeId, endSnapPos |
| ▶️ 开始导航（坐标） | `POST /api/navigation/route` | startLat, startLng, endLat, endLng |
| 🔀 多目标导航 | `POST /api/navigation/multi-route` | targets[], needReturn |
| 🍽️ 附近设施 | `GET /api/navigation/facilities/nearby` | lat, lng, radius, facilityTypes |

> **拥挤度**: 由 scenic 模块提供独立 REST 端点（当前 `GET /api/navigation/congestion/{id}` 为历史遗留，数据归属 scenic 的 `t_crowd_level`）。前端如需拥挤度数据，直接调用 scenic 模块接口，不经过 navigation。navigation 内部通过 `ScenicService.getCrowdLevelsByScenicArea()` 消费拥挤度数据用于 `avoid_crowd` 路径规划策略。

---

## 六、实现优先级

### Wave 0 — 清理（立即执行，无依赖）

- 删除数据库残留表 `t_navigation_crowd_level`（5 条陈旧数据）
- 删除残留视图 `v_navigation_realtime_congestion`
- 更新 `数据库构建/navigation_tables.sql` 表头注释（移除 crowd_level 的提及）

### Wave 1（可并行，无依赖）
  ├── CoordinateTransformUtil.java     # 坐标转换
  └── 数据库 schema 变更 + spatial index

Wave 2（依赖 Wave 1，可并行）:
  ├── 数据预填充脚本                   # 填充 scenic_access 表 + display_for_scenic_id（⚠️ 必须在 API 之前）
  ├── SearchController + 搜索服务      # 统一搜索 API
  ├── nearest-edge API + SCC 计算      # 地图点击 API
  └── ScenicAccessService + 接入点 API # 景区接入点（依赖预填充）

Wave 3（依赖 Wave 2）:
  ├── POI 展示 API                     # 景区 POI（依赖预填充）
  └── PathPlanningServiceImpl 改造     # 核心算法（push-bike / 灵活输入）

Wave 4（依赖 Wave 3）:
  ├── resolveRouteInput() 统一解析      # scenicAreaId/nodeId/edgeId/坐标 → 路网节点
  ├── 重写 route 端点                   # POST /api/navigation/route 支持4种输入
  ├── 重写 multi-route 端点             # POST /api/navigation/multi-route targets混搭
  ├── 修改 nearby 端点                  # GET /api/navigation/facilities/nearby 接受坐标
  └── 更新 health 端点                  # 加入 spatialIndexReady

Wave 5:
  └── 集成测试 + 性能验证              # 端到端测试
```
