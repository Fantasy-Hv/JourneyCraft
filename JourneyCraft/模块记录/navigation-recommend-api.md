# Navigation → Recommend 模块接口约定

> **提供方**: Navigation 模块（刘方正）  
> **调用方**: Recommend 模块（黄严）  
> **更新日期**: 2026-04-30  

---

## 接口注入方式

```java
import org.dsgroup.journeycraft.navigation.api.NavigationService;
import javax.annotation.Resource;

@Resource
private NavigationService navigationService;
```

---

## 接口定义

### getDistanceToTarget — 获取用户到目标地点的实际路网距离

计算用户 GPS 坐标到指定景区入口或建筑物的**道路网络实际路径距离**（非直线距离）。

```
方法签名:
  BigDecimal getDistanceToTarget(
      Integer targetType,
      Long targetId,
      BigDecimal userLat,
      BigDecimal userLng
  );
```

### 参数说明

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `targetType` | `Integer` | 是 | 目标类型：`0` = 景区入口，`1` = 建筑物 |
| `targetId` | `Long` | 是 | 目标 ID（景区 ID 或建筑 ID） |
| `userLat` | `BigDecimal` | 是 | 用户当前纬度（WGS-84） |
| `userLng` | `BigDecimal` | 是 | 用户当前经度（WGS-84） |

### 返回值

| 返回值 | 含义 |
|--------|------|
| `BigDecimal` | 实际路网距离（米），> 0 表示成功 |
| `null` | 无法计算（参数无效 / 无路网节点 / 路径不通） |

---

## 调用示例

### 场景 A："故宫离我多远？"

```java
// 目标: 故宫 (scenicAreaId=1)
// 用户: 在天安门附近 (39.9100, 116.4000)
BigDecimal dist = navigationService.getDistanceToTarget(
    0,                     // targetType = 0 (景区入口)
    1L,                    // targetId = 1 (故宫)
    new BigDecimal("39.9100"),
    new BigDecimal("116.4000")
);

// dist ≈ 580m（路网实际路径距离）
// 对比: Haversine 直线距离 ≈ 450m
```

### 场景 B："太和殿离我多远？"

```java
// 目标: 太和殿 (buildingId=1)
// 用户: 在天安门附近 (39.9100, 116.4000)
BigDecimal dist = navigationService.getDistanceToTarget(
    1,                     // targetType = 1 (建筑物)
    1L,                    // targetId = 1 (太和殿)
    new BigDecimal("39.9100"),
    new BigDecimal("116.4000")
);

// dist ≈ 750m
```

---

## 实现原理

```
用户 (lat, lng)
    │
    ▼
[findNearestNodeByCoords]  ─── Haversine 全表扫描 RoadNode
    │                             找到最近节点
    ▼
用户最近路网节点 ──┬── [Dijkstra] ──▶ 目标路网节点
                  │                        ▲
                  │                    targetType=0: 景区入口节点 (nodeType=0)
                  │                    targetType=1: 建筑物关联节点 (buildingId)
                  │
                  └──▶ 置信参考: 距离 ≥ Haversine 直线距离
```

### 与 Haversine 直线距离的对比

| 场景 | Haversine 直线 | 路网实际距离 | 差异原因 |
|------|--------------|------------|---------|
| 用户→故宫 | ~450m | ~580m | 道路不是直线 |
| 用户→太和殿 | ~400m | ~750m | 不能穿墙，需绕行 |

> **建议**: 推荐排序时优先使用此接口返回的路网距离，比直线距离更真实反映用户的步行成本。

---

## 注意事项

1. **全表扫描** — 当前 `findNearestNodeByCoords` 扫描所有 RoadNode 找最近节点，数据量 < 1000 时延迟 < 10ms
2. **只返回距离** — 不返回路径节点序列（若需要请用 `planSingleRoute()`）
3. **null 安全** — 必须判空：返回 `null` 时跳过该推荐项，不应抛异常
4. **并发** — 无状态，线程安全，可在 Recommend 的并发推荐计算中直接多线程调用

---

## 枚举速查

| targetType | 含义 | RoadNode 查询条件 |
|-----------|------|-----------------|
| `0` | 景区入口 | `scenicAreaId = targetId AND nodeType = 0` |
| `1` | 建筑物 | `buildingId = targetId` |
