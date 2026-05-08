# Navigation 模块 — 前端开发手册

> **版本**: v1.0 | **日期**: 2026-05-08 | **后端模块**: navigation（刘方正）  
> **坐标系**: 前端收发均为 **GCJ-02**（高德地图原生），后端内部完成 WGS-84 ↔ GCJ-02 转换

---

## 一、API 速查

| # | 方法 | 路径 | 用途 |
|---|------|------|------|
| 1 | GET | `/api/search` | 搜索景区 + 路网节点 |
| 2 | GET | `/api/navigation/nearest-edge` | 地图点击找最近道路 |
| 3 | GET | `/api/navigation/scenic/{id}/access-nodes` | 景区各交通方式入口 |
| 4 | GET | `/api/navigation/scenic/{id}/poi-nodes` | 景区内 POI 标记 |
| 5 | POST | `/api/navigation/route` | 单目标导航 |
| 6 | POST | `/api/navigation/multi-route` | 多目标导航 |
| 7 | GET | `/api/navigation/facilities/nearby` | 附近设施（路网距离） |
| 8 | GET | `/api/navigation/health` | 健康检查 |

---

## 二、接口详细规格

### 2.1 搜索 `GET /api/search`

```
GET /api/search?keyword=故宫&types=scenic,node&limit=10&page=1
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 模糊匹配，**为空时只返回景区**（按热度排序，前 10 条）。路网节点无热度字段，空 keyword 不返回 |
| types | String | 否 | scenic,node（逗号分隔，默认全部） |
| limit | Integer | 否 | 每页数量（默认 10） |
| page | Integer | 否 | 页码（默认 1） |

**响应** — 分两组返回：

```json
{
  "scenicResults": [{
    "id": 1, "name": "故宫博物院", "type": "scenic",
    "scenicType": 0,            // 0=景区 1=校园
    "city": "北京",
    "latitude": 39.9124, "longitude": 116.4039,
    "rating": 4.9, "heatScore": 9800
  }],
    "nodeResults": [{
      "id": 419685, "name": "西关环岛", "type": "road_node",
      "nodeType": 1,
      "latitude": 40.0791, "longitude": 116.3479,
      "transportTypes": [1, 4, 5]
    }],
  "total": 15, "page": 1, "size": 10
}
```

> **前端提示**: `transportTypes` 不含用户当前模式 → 该节点不可作为该模式的起终点，UI 上做弱化展示。连通性详情（`connectedComponentSize`）通过 `nearest-edge` 接口获取，搜索结果因性能原因不提供此字段。

---

### 2.2 地图点击找路 `GET /api/navigation/nearest-edge`

```
GET /api/navigation/nearest-edge?lat=39.9124&lng=116.4039&transportMode=bike&radius=2000
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| lat / lng | Double | 是 | 点击坐标 (GCJ-02) |
| transportMode | String | 否 | walk / bike / shuttle（默认 walk） |
| radius | Integer | 否 | 搜索半径米（默认 1000，最大 5000） |

**响应**：

```json
{
  "edgeId": 262800,
  "snapLat": 39.9128, "snapLng": 116.4045,   // 投影到边上的精确位置
  "snapPosition": 0.35,                       // 在边上的比例 (0~1)
  "distance": 12.3,                           // 点击位置到投影点的距离
  "edgeInfo": {
    "fromNodeId": 418811, "toNodeId": 418812,
    "highwayType": "service",
    "transportModes": ["walk", "bike"],    // 该边支持的交通方式（后端翻译）
    "name": null
  },
  "connectedComponentSize": 15623,
  "confidence": "high",                       // high / medium / low / none
  "alternatives": [                           // confidence 非 high 时提供
    { "type": "nearest_walkable", "nodeId": 67890, "distance": 850 },
    { "type": "nearest_bikeable",  "nodeId": 67891, "distance": 1200 }
  ]
}
```

> **前端行为提示**:
> - `confidence: "high"` → 直接放标记，可作起终点
> - `confidence: "low"` → 弱化标记 + 同时展示 `alternatives` 备选点
> - `confidence: "none"` → 提示"附近无可用道路"
> - 起终点传参：用 `edgeId + snapPosition`（从 nearest-edge 返回），或用搜索结果里的 `nodeId`

---

### 2.3 景区接入点 `GET /api/navigation/scenic/{id}/access-nodes`

```
GET /api/navigation/scenic/1/access-nodes?maxDistance=1500
```

**响应** — 按交通方式分组：

```json
{
  "scenicAreaId": 1,
  "scenicAreaName": "故宫博物院",
  "scenicCenter": { "latitude": 39.9163, "longitude": 116.3971 },
  "accessNodes": {
    "walk": [
      { "nodeId": 12345, "name": "东门入口", "nodeType": 0,
        "latitude": 39.9170, "longitude": 116.3980,
        "distance": 120, "isPrimary": true }
    ],
    "bike": [
      { "nodeId": 67890, "distance": 350 },
      { "nodeId": 12345, "distance": 120, "viaWalk": true, "walkDistance": 120 }
    ],
    "shuttle": [
      { "nodeId": 99999, "name": "故宫停车场", "distance": 580 }
    ]
  }
}
```

> **关键**: `shuttle` 数组为空 → 前端应**禁用 shuttle 按钮**。`bike` 同理。`viaWalk: true` 表示该入口需先步行一段才能开始骑行，UI 上可提示。

---

### 2.4 景区 POI `GET /api/navigation/scenic/{id}/poi-nodes`

```
GET /api/navigation/scenic/1/poi-nodes?types=restaurant,toilet&limit=50
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| types | String | 否 | 过滤类型（逗号分隔），如 restaurant,toilet,parking |
| limit | Integer | 否 | 最大返回数（默认 50） |

**响应**：

```json
[{
  "nodeId": 54321, "name": "故宫餐厅",
  "nodeType": 2,
  "poiType": "restaurant",       // 可用作图标选择
  "latitude": 39.9156, "longitude": 116.3978,
    "description": "中式快餐",       // ⚠️ 仅关联了 t_facility 的节点有值，OSM 原生 POI 通常为 null
    "facilityId": null
}]
```

> **POI 类型**（可供前端图标映射）: `restaurant`, `toilet`, `parking`, `fast_food`, `cafe`, `bank`, `hospital`, `pharmacy`, `supermarket`, `bicycle_rental`, `charging_station`, `cinema`, `swimming_pool`, `kindergarten`, `shelter` 等。

---

### 2.5 路径规划 `POST /api/navigation/route`

**起点/终点支持四种输入**（`inputType` 对应为 `scenicArea` / `node` / `edge` / `coordinate`）：

| 输入方式 | 参数 | 来源 |
|---------|------|------|
| 景区 ID | `startScenicAreaId` / `endScenicAreaId` | 搜索结果 scenicResults[].id |
| 路网节点 ID | `startNodeId` / `endNodeId` | 搜索结果 nodeResults[].id |
| 边 + 投影 | `startEdgeId` + `startSnapPos` | nearest-edge 返回 |
| 坐标 | `startLat` + `startLng` | 前端 GPS / 地图点击 |

```json
{
  "startScenicAreaId": 1,
  "endLat": 40.2710, "endLng": 116.1430,
  "transportMode": "bike",
  "strategy": "shortest_time",
  "algorithm": "astar"
}
```

| 参数 | 可选值 | 说明 |
|------|--------|------|
| transportMode | walk / bike / shuttle | 见下方交通方式说明 |
| strategy | shortest_distance / shortest_time / avoid_crowd | 默认 shortest_distance |
| algorithm | dijkstra / astar | 默认 astar |

**响应**：

```json
{
  "routeId": 42,
  "totalDistance": 1530.5, "estimatedTime": 1280,
  "transportMode": "bike", "strategy": "shortest_distance",
  "segments": [
    { "type": "push_bike", "distance": 120, "time": 108, "nodeIds": [12345] },
    { "type": "bike",      "distance": 1350, "time": 270, "nodeIds": [12346, ...] }
  ],
  "startInfo": { "inputType": "scenicArea", "scenicAreaName": "故宫博物院",
                 "resolvedNodeId": 12345, "latitude": 39.9170, "longitude": 116.3980 },
  "endInfo":   { "inputType": "coordinate", "resolvedNodeId": 67890,
                 "latitude": 40.2710, "longitude": 116.1430 },
  "path": [
    { "lat": 39.9170, "lng": 116.3980, "segmentType": "push_bike" },
    { "lat": 39.9165, "lng": 116.3978, "segmentType": "bike" }
  ]
}
```

**segment.type 含义**（前端用此决定线条样式）：

| type | 含义 | 建议样式 |
|------|------|---------|
| walk | 纯步行段 | 灰色实线 |
| bike | 骑行段 | 绿色实线 |
| push_bike | 推行段 | 绿色虚线 + 步行图标 |
| shuttle | 车行段 | 蓝色实线 |
| walk_access / walk_egress | 车行首尾步行段 | 灰色虚线 |

> **`path` 数组**: 坐标序列 (GCJ-02)，直接给高德 `Polyline` 绘制。`segmentType` 用于分段着色。

**交通方式说明**：

| 模式 | 行为 | 前端提示 |
|------|------|---------|
| walk | 纯步行 4 km/h | — |
| bike | 优先骑行 18 km/h，必要时推车 4 km/h | 路线可能含 `push_bike` 段 |
| shuttle | 优先车行 30 km/h，首尾各 ≤500m 步行 | 路线可能含 `walk_access/egress` 段 |

---

### 2.6 多目标导航 `POST /api/navigation/multi-route`

```json
{
  "startScenicAreaId": 1,
  "targets": [
    { "scenicAreaId": 2 },
    { "nodeId": 67890 },
    { "lat": 40.2200, "lng": 116.2280 }
  ],
  "transportMode": "walk",
  "strategy": "shortest_distance",
  "needReturn": true
}
```

**响应**：

```json
{
  "totalDistance": 5200.5, "totalTime": 4680,
  "visitOrder": [1, 2, 0],
  "segments": [
    { "order": 1, "distance": 1530.5, "time": 1280,
      "from": { "nodeId": 12345, "name": "东门入口", "lat": ..., "lng": ... },
      "to": { "scenicAreaId": 2, "name": "虎谷风景区", "lat": ..., "lng": ... },
      "path": [...] }
  ]
}
```

> **`visitOrder`**: TSP 优化后的访问顺序。`[1,2,0]` 表示先访问 targets[1]、再 targets[2]、最后 targets[0]。

---

### 2.7 附近设施 `GET /api/navigation/facilities/nearby`

```
GET /api/navigation/facilities/nearby?lat=39.9124&lng=116.4039&transportMode=walk&facilityTypes=0,1&radius=2000&limit=10
```

| 参数 | 说明 |
|------|------|
| lat / lng | 中心点 (GCJ-02)，与 nodeId 二选一 |
| facilityTypes | 设施类型: 0=卫生间 1=餐饮 2=超市 3=停车场 4=售票处 5=游客中心 6=医疗点 7=ATM 8=贩卖机 9=摆渡车站 10=自行车租赁 |
| radius | 搜索半径米（默认 2000） |

**响应**：

```json
{
  "sourceNodeId": 12345,
  "facilities": [{
    "facilityId": 101, "name": "故宫餐厅",
    "facilityType": 1, "subtype": "中式快餐",
    "latitude": 39.9156, "longitude": 116.3978,
    "roadDistance": 580.3,       // 路网实际路径距离 ← 按此排序
    "straightDistance": 450.0,
    "walkTime": 435,             // 预计步行时间(秒)
    "rating": 4.5
  }]
}
```

---

### 2.8 健康检查 `GET /api/navigation/health`

```json
{ "status": "ok", "nodeCount": 420223, "edgeCount": 263292,
  "scenicCount": 10, "spatialIndexReady": true }
```

---

## 三、前端交互约定

### 3.1 典型用户流程

```
搜索 → 选景区 → 看POI + 接入点 → 选交通方式 → 地图点起点 → 导航 → 看路线
```

### 3.2 导航起终点来源矩阵

| 用户操作 | 传给 route API 的参数 |
|---------|---------------------|
| 搜索列表点了景区 | `startScenicAreaId` |
| 搜索列表点了路网节点 | `startNodeId` |
| 地图点了一个位置 | `startEdgeId` + `startSnapPos`（从 nearest-edge 拿到） |
| 直接用 GPS | `startLat` + `startLng` |

### 3.3 交通方式可用性判断

调 `access-nodes` 后，根据返回判断：

```
accessNodes.walk.length    > 0 → 步行按钮可用（理论上始终可用）
accessNodes.bike.length    > 0 → 骑行按钮可用
accessNodes.shuttle.length > 0 → 电瓶车按钮可用
```

数组为空 → 按钮置灰。

### 3.4 连通性提示

- 搜索结果: `transportTypes` 不含用户当前 `transportMode` → 灰色展示 + ⚠️
- `nearest-edge` 返回 `connectedComponentSize < 1000` → 弱化标记（未连通主干网）
- `nearest-edge` 返回 `confidence: "low"` → 半透明标记 + 展示备选点

### 3.5 路线绘制

- `path` 数组直接给高德 `Polyline`
- 按 `segmentType` 分段着色（参见 §2.5 中的样式建议表）
- 起终点标记：绿色 🟢 起点 / 红色 🔴 终点

---

## 四、状态管理建议

前端建议维护以下核心状态：

| 状态 | 类型 | 来源 |
|------|------|------|
| `transportMode` | `"walk" \| "bike" \| "shuttle"` | 用户选择 |
| `startPoint` | `{ type, scenicAreaId?, nodeId?, edgeId?, snapPos?, lat?, lng? }` | 搜索 / nearest-edge / GPS |
| `endPoint` | 同上 | 同上 |
| `waypoints` | `StartPoint[]` | 多次选点 |
| `currentScenic` | `{ id, name, center, accessNodes, poiNodes }` | access-nodes + poi-nodes |
| `currentRoute` | route API 响应 | route / multi-route |

---

## 五、错误处理约定

所有接口返回统一格式 `{ code, message, data }`：

| code | 含义 | 前端处理 |
|------|------|---------|
| 200 | 成功 | — |
| 400 | 参数错误 | 提示 message |
| 404 | 无结果（如 nearest-edge 找不到） | 提示"附近无可用道路" |
| 500 | 服务器错误 | 提示"服务异常，稍后重试" |

---

## 六、坐标约定（再强调）

| 环节 | 坐标系 |
|------|--------|
| 前端发送 | **GCJ-02**（高德原生） |
| 前端接收 | **GCJ-02**（后端已转换） |
| 前端渲染 | **GCJ-02**（直接给高德 SDK） |

**前端不需要做任何坐标转换。**
