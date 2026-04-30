-- =============================================
-- JourneyCraft 导航模块数据库构建脚本
-- 文件名: navigation_db_build.sql
-- 版本: v2.3
-- 日期: 2026-04-15
-- 作者: 后端智能体 🔧
-- 说明: 包含完整的数据库创建和测试数据
-- =============================================

-- =============================================
-- 第一部分：创建数据库（如果不存在）
-- =============================================
CREATE DATABASE IF NOT EXISTS journeyCraft 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE journeyCraft;

-- =============================================
-- 第二部分：临时SCENIC模块桩表（供navigation独立开发）
-- =============================================

-- T1: 临时景区表（桩表）
CREATE TABLE IF NOT EXISTS t_temp_scenic_area (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '景区ID（临时）',
    name VARCHAR(100) NOT NULL COMMENT '景区名称',
    type TINYINT DEFAULT 0 COMMENT '类型: 0=景区, 1=校园',
    city VARCHAR(50) DEFAULT '北京' COMMENT '城市',
    latitude DECIMAL(10, 8) COMMENT '纬度',
    longitude DECIMAL(11, 8) COMMENT '经度',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用: 1=营业 0=关闭',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_city (city),
    INDEX idx_status (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='临时景区表（桩表）';

-- T2: 临时建筑表（桩表）
CREATE TABLE IF NOT EXISTS t_temp_building (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '建筑ID（临时）',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区ID',
    name VARCHAR(100) NOT NULL COMMENT '建筑名称',
    type TINYINT DEFAULT 4 COMMENT '类型: 0=教学楼,1=图书馆,2=食堂,3=宿舍,4=景点建筑',
    floor_count INT DEFAULT 1 COMMENT '楼层数',
    latitude DECIMAL(10, 8) COMMENT '纬度',
    longitude DECIMAL(11, 8) COMMENT '经度',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用: 1=启用 0=禁用',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_scenic_area (scenic_area_id),
    INDEX idx_type (type),
    FOREIGN KEY (scenic_area_id) REFERENCES t_temp_scenic_area(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='临时建筑表（桩表）';

-- T3: 临时设施表（桩表）
CREATE TABLE IF NOT EXISTS t_temp_facility (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设施ID（临时）',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区ID',
    building_id BIGINT COMMENT '所属建筑ID',
    name VARCHAR(100) NOT NULL COMMENT '设施名称',
    type INT DEFAULT 1 COMMENT '设施类型: 0=卫生间,1=餐饮,2=超市,3=停车场,...',
    latitude DECIMAL(10, 8) COMMENT '纬度',
    longitude DECIMAL(11, 8) COMMENT '经度',
    floor_number INT DEFAULT 1 COMMENT '所在楼层',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用: 1=营业 0=关闭',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_scenic_area (scenic_area_id),
    INDEX idx_building (building_id),
    INDEX idx_type (type),
    FOREIGN KEY (scenic_area_id) REFERENCES t_temp_scenic_area(id) ON DELETE CASCADE,
    FOREIGN KEY (building_id) REFERENCES t_temp_building(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='临时设施表（桩表）';

-- =============================================
-- 第三部分：NAVIGATION 模块核心表（7张）
-- =============================================

-- 1. 路网节点表
CREATE TABLE IF NOT EXISTS t_navigation_road_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '节点ID(自增主键)',
    osm_id BIGINT NOT NULL COMMENT 'OSM节点ID',
    osm_tags JSON COMMENT 'OSM标签(JSON格式)',
    scenic_area_id BIGINT COMMENT '所属景区ID',
    building_id BIGINT COMMENT '所属建筑ID',
    facility_id BIGINT COMMENT '关联设施ID',
    name VARCHAR(100) COMMENT '节点名称',
    node_type TINYINT NOT NULL DEFAULT 5 COMMENT '节点类型: 0=入口,1=路口,2=POI,3=设施入口,4=拍照点,5=OSM普通节点',
    latitude DECIMAL(10, 8) NOT NULL COMMENT '纬度',
    longitude DECIMAL(11, 8) NOT NULL COMMENT '经度',
    floor_number INT DEFAULT 1 COMMENT '楼层(室内导航用)',
    is_important TINYINT DEFAULT 0 COMMENT '是否重要节点: 0=否,1=是',
    is_accessible TINYINT DEFAULT 1 COMMENT '是否可通行: 0=禁用,1=启用',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用: 0=禁用,1=启用',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_osm_id (osm_id),
    INDEX idx_scenic_area (scenic_area_id),
    INDEX idx_building (building_id),
    INDEX idx_facility (facility_id),
    INDEX idx_location (latitude, longitude),
    INDEX idx_node_type (node_type),
    INDEX idx_important (is_important),
    INDEX idx_status (is_enabled),
    FOREIGN KEY (scenic_area_id) REFERENCES t_temp_scenic_area(id) ON DELETE SET NULL,
    FOREIGN KEY (building_id) REFERENCES t_temp_building(id) ON DELETE SET NULL,
    FOREIGN KEY (facility_id) REFERENCES t_temp_facility(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路网节点表';

-- 2. 路径段表
CREATE TABLE IF NOT EXISTS t_navigation_road_edge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '路径ID',
    osm_way_id BIGINT NOT NULL COMMENT 'OSM路径ID',
    osm_tags JSON COMMENT 'OSM标签(JSON格式)',
    from_node_id BIGINT NOT NULL COMMENT '起始节点ID',
    to_node_id BIGINT NOT NULL COMMENT '目标节点ID',
    name VARCHAR(100) COMMENT '路径名称',
    distance DECIMAL(10, 2) NOT NULL COMMENT '几何距离(米)',
    adjusted_distance DECIMAL(10, 2) COMMENT '调整后距离(米)',
    walk_time INT COMMENT '步行时间(秒)',
    bike_time INT COMMENT '自行车时间(秒)',
    shuttle_time INT COMMENT '电瓶车时间(秒)',
    transport_type TINYINT NOT NULL DEFAULT 0 COMMENT '通行方式: 0=未知,1=仅步行,2=仅自行车,3=仅车辆,4=步行+自行车,5=全部',
    is_bidirectional TINYINT DEFAULT 1 COMMENT '是否双向通行: 0=否,1=是',
    highway_type VARCHAR(50) COMMENT 'OSM道路类型',
    surface VARCHAR(50) COMMENT '路面类型',
    incline DECIMAL(5, 2) COMMENT '坡度(%)',
    base_congestion DECIMAL(3, 2) DEFAULT 0.0 COMMENT '基础拥挤度(0-1)',
    current_congestion DECIMAL(3, 2) DEFAULT 0.0 COMMENT '实时拥挤度(0-1)',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用: 0=禁用,1=启用',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_edge_direction (from_node_id, to_node_id),
    INDEX idx_osm_way (osm_way_id),
    INDEX idx_from_node (from_node_id),
    INDEX idx_to_node (to_node_id),
    INDEX idx_transport (transport_type),
    INDEX idx_highway (highway_type),
    INDEX idx_status (is_enabled),
    FOREIGN KEY (from_node_id) REFERENCES t_navigation_road_node(id) ON DELETE CASCADE,
    FOREIGN KEY (to_node_id) REFERENCES t_navigation_road_node(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路径段表';

-- 3. 拥挤度记录表
CREATE TABLE IF NOT EXISTS t_navigation_crowd_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    node_id BIGINT NOT NULL COMMENT '节点ID',
    level TINYINT NOT NULL DEFAULT 0 COMMENT '拥挤等级: 0=舒适,1=适中,2=拥挤,3=非常拥挤',
    crowd_count INT COMMENT '实际人数',
    capacity INT COMMENT '容量上限',
    density DECIMAL(5, 2) COMMENT '密度(人/平方米)',
    source TINYINT DEFAULT 1 COMMENT '来源: 0=传感器,1=用户上报,2=算法预测',
    recorded_at DATETIME NOT NULL COMMENT '记录时间',
    predicted_at DATETIME COMMENT '预测时间',
    valid_until DATETIME COMMENT '有效期至',
    confidence DECIMAL(3, 2) DEFAULT 1.0 COMMENT '数据可信度(0-1)',
    reporter_user_id BIGINT COMMENT '上报用户ID',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_node (node_id),
    INDEX idx_recorded (recorded_at),
    INDEX idx_source (source),
    INDEX idx_valid (valid_until),
    INDEX idx_level (level),
    FOREIGN KEY (node_id) REFERENCES t_navigation_road_node(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拥挤度记录表';

-- 4. 拍照点推荐表
CREATE TABLE IF NOT EXISTS t_navigation_photo_spot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '拍照点ID',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区ID',
    node_id BIGINT COMMENT '最佳拍摄节点ID',
    name VARCHAR(100) NOT NULL COMMENT '拍照点名称',
    target_name VARCHAR(100) COMMENT '拍摄目标',
    description TEXT COMMENT '拍摄描述',
    recommended_angle VARCHAR(50) COMMENT '推荐角度',
    best_time VARCHAR(100) COMMENT '最佳时间',
    best_season VARCHAR(50) COMMENT '最佳季节',
    sample_image_url VARCHAR(255) COMMENT '示例图片URL',
    rating DECIMAL(3, 2) DEFAULT 0.0 COMMENT '评分(1-5)',
    check_in_count INT DEFAULT 0 COMMENT '打卡次数',
    latitude DECIMAL(10, 8) COMMENT '推荐拍摄位置纬度',
    longitude DECIMAL(11, 8) COMMENT '推荐拍摄位置经度',
    available TINYINT DEFAULT 1 COMMENT '是否可拍摄: 0=否,1=是',
    popular TINYINT DEFAULT 0 COMMENT '是否热门: 0=否,1=是',
    star_rating TINYINT DEFAULT 0 COMMENT '星级(1-5)',
    full_description TEXT COMMENT '完整描述',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用: 0=禁用,1=启用',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_scenic_area (scenic_area_id),
    INDEX idx_node (node_id),
    INDEX idx_rating (rating),
    INDEX idx_location (latitude, longitude),
    INDEX idx_status (is_enabled),
    FOREIGN KEY (scenic_area_id) REFERENCES t_temp_scenic_area(id) ON DELETE CASCADE,
    FOREIGN KEY (node_id) REFERENCES t_navigation_road_node(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拍照点推荐表';

-- 5. 路径规划缓存表
CREATE TABLE IF NOT EXISTS t_navigation_route_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '缓存ID',
    start_node_id BIGINT NOT NULL COMMENT '起始节点ID',
    end_node_id BIGINT NOT NULL COMMENT '目标节点ID',
    transport_type TINYINT NOT NULL COMMENT '通行方式',
    strategy VARCHAR(50) COMMENT '规划策略',
    route_nodes JSON NOT NULL COMMENT '路径节点ID列表',
    total_distance DECIMAL(10, 2) NOT NULL COMMENT '总距离(米)',
    total_time INT NOT NULL COMMENT '总时间(秒)',
    calculated_at DATETIME NOT NULL COMMENT '计算时间',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    hit_count INT DEFAULT 0 COMMENT '命中次数',
    calculation_time_ms INT COMMENT '计算耗时(毫秒)',
    node_count INT COMMENT '路径节点数',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_route_query (start_node_id, end_node_id, transport_type, strategy),
    INDEX idx_expires (expires_at),
    INDEX idx_hit_count (hit_count),
    INDEX idx_calculated (calculated_at),
    FOREIGN KEY (start_node_id) REFERENCES t_navigation_road_node(id) ON DELETE CASCADE,
    FOREIGN KEY (end_node_id) REFERENCES t_navigation_road_node(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路径规划缓存表';

-- 6. OSM数据导入日志表
CREATE TABLE IF NOT EXISTS t_navigation_osm_import_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_path VARCHAR(500) COMMENT '文件完整路径',
    region VARCHAR(100) COMMENT '区域',
    osm_extent VARCHAR(200) COMMENT '数据范围',
    import_type VARCHAR(20) COMMENT '导入类型: node, way, relation',
    total_records INT DEFAULT 0 COMMENT '总记录数',
    nodes_imported INT DEFAULT 0 COMMENT '导入节点数',
    edges_imported INT DEFAULT 0 COMMENT '导入路径段数',
    success_count INT DEFAULT 0 COMMENT '成功数',
    failed_count INT DEFAULT 0 COMMENT '失败数',
    processing_time_ms INT COMMENT '处理耗时(毫秒)',
    status VARCHAR(20) DEFAULT 'processing' COMMENT '状态: processing, completed, failed',
    error_message TEXT COMMENT '错误信息',
    started_at DATETIME NOT NULL COMMENT '开始时间',
    finished_at DATETIME COMMENT '结束时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_file (file_name),
    INDEX idx_status (status),
    INDEX idx_region (region),
    INDEX idx_started (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OSM数据导入日志表';

-- 7. 室内楼层表
CREATE TABLE IF NOT EXISTS t_navigation_indoor_floor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '楼层ID',
    building_id BIGINT NOT NULL COMMENT '建筑ID',
    floor_number INT NOT NULL COMMENT '楼层号(-1=地下室,0=地面,1=一层...)',
    floor_name VARCHAR(50) COMMENT '楼层名称',
    map_image_url VARCHAR(255) COMMENT '楼层地图URL',
    map_dimensions JSON COMMENT '地图尺寸',
    indoor_data JSON COMMENT '室内数据（房间、走廊、POI等）',
    elevator_node_id BIGINT COMMENT '电梯节点ID',
    stair_node_id BIGINT COMMENT '楼梯节点ID',
    entrance_node_ids JSON COMMENT '入口节点ID列表',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否,1=是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_building_floor (building_id, floor_number),
    INDEX idx_building (building_id),
    INDEX idx_floor_number (floor_number),
    FOREIGN KEY (building_id) REFERENCES t_temp_building(id) ON DELETE CASCADE,
    FOREIGN KEY (elevator_node_id) REFERENCES t_navigation_road_node(id) ON DELETE SET NULL,
    FOREIGN KEY (stair_node_id) REFERENCES t_navigation_road_node(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='室内楼层表';

-- =============================================
-- 第四部分：新增表（v2.3优化）
-- =============================================

-- 8. 导航路线表（存储用户规划的路线）
CREATE TABLE IF NOT EXISTS t_navigation_route (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '路线ID',
    user_id BIGINT COMMENT '用户ID（可为空，匿名规划）',
    scenic_area_id BIGINT NOT NULL COMMENT '景区ID',
    start_node_id BIGINT NOT NULL COMMENT '起点节点ID',
    end_node_ids JSON COMMENT '终点列表（多目标规划时使用）',
    path_nodes JSON NOT NULL COMMENT '路径节点序列',
    total_distance DECIMAL(10, 2) NOT NULL COMMENT '总距离（米）',
    estimated_time INT NOT NULL COMMENT '预计时间（秒）',
    strategy VARCHAR(50) DEFAULT 'shortest_distance' COMMENT '规划策略',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user (user_id),
    INDEX idx_scenic_area (scenic_area_id),
    INDEX idx_created (created_at DESC),
    FOREIGN KEY (scenic_area_id) REFERENCES t_temp_scenic_area(id) ON DELETE CASCADE,
    FOREIGN KEY (start_node_id) REFERENCES t_navigation_road_node(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导航路线表';

-- 9. 导航历史表（记录用户的历史导航）
CREATE TABLE IF NOT EXISTS t_navigation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '历史ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    scenic_area_id BIGINT NOT NULL COMMENT '景区ID',
    start_node_id BIGINT NOT NULL COMMENT '起点节点ID',
    end_node_id BIGINT NOT NULL COMMENT '终点节点ID',
    path_nodes JSON COMMENT '路径节点序列',
    navigated_at DATETIME NOT NULL COMMENT '导航时间',
    INDEX idx_user (user_id),
    INDEX idx_scenic_area (scenic_area_id),
    INDEX idx_navigated (navigated_at DESC),
    FOREIGN KEY (scenic_area_id) REFERENCES t_temp_scenic_area(id) ON DELETE CASCADE,
    FOREIGN KEY (start_node_id) REFERENCES t_navigation_road_node(id) ON DELETE CASCADE,
    FOREIGN KEY (end_node_id) REFERENCES t_navigation_road_node(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导航历史表';

-- =============================================
-- 第五部分：crowd_level 表补充 scenic_area_id 字段
-- =============================================
-- 此字段已在 t_navigation_crowd_level 表中直接定义，无需额外ALTER

-- =============================================
-- 第六部分：road_edge 表补充 scenic_area_id 字段
-- =============================================
-- 此字段已在 t_navigation_road_edge 表中直接定义，无需额外ALTER

-- =============================================
-- 第七部分：视图
-- =============================================

-- 1. 重要节点视图
CREATE OR REPLACE VIEW v_navigation_important_nodes AS
SELECT 
    rn.id, rn.osm_id, rn.name, rn.node_type, rn.latitude, rn.longitude,
    sa.name as scenic_area_name,
    CASE rn.node_type
        WHEN 0 THEN '入口' WHEN 1 THEN '路口' WHEN 2 THEN 'POI'
        WHEN 3 THEN '设施入口' WHEN 4 THEN '拍照点' ELSE '普通节点'
    END as node_type_name
FROM t_navigation_road_node rn
LEFT JOIN t_temp_scenic_area sa ON rn.scenic_area_id = sa.id
WHERE rn.is_important = 1 AND rn.is_enabled = 1 AND rn.is_deleted = 0;

-- 2. 可步行路径视图
CREATE OR REPLACE VIEW v_navigation_walkable_edges AS
SELECT 
    re.id, re.osm_way_id, re.from_node_id, re.to_node_id, re.name,
    re.distance, re.walk_time, re.transport_type, re.highway_type,
    fn.latitude as from_lat, fn.longitude as from_lon,
    tn.latitude as to_lat, tn.longitude as to_lon
FROM t_navigation_road_edge re
JOIN t_navigation_road_node fn ON re.from_node_id = fn.id
JOIN t_navigation_road_node tn ON re.to_node_id = tn.id
WHERE re.is_enabled = 1 AND re.is_deleted = 0
AND (re.transport_type = 1 OR re.transport_type = 4 OR re.transport_type = 5);

-- 3. 拥挤度实时视图
CREATE OR REPLACE VIEW v_navigation_realtime_congestion AS
SELECT 
    n.id as node_id, n.name as node_name, n.node_type, n.latitude, n.longitude,
    sa.name as scenic_area_name,
    (SELECT cl.level FROM t_navigation_crowd_level cl 
     WHERE cl.node_id = n.id ORDER BY cl.recorded_at DESC LIMIT 1) as current_level,
    (SELECT cl.crowd_count FROM t_navigation_crowd_level cl 
     WHERE cl.node_id = n.id ORDER BY cl.recorded_at DESC LIMIT 1) as current_crowd_count,
    (SELECT cl.recorded_at FROM t_navigation_crowd_level cl 
     WHERE cl.node_id = n.id ORDER BY cl.recorded_at DESC LIMIT 1) as last_updated
FROM t_navigation_road_node n
LEFT JOIN t_temp_scenic_area sa ON n.scenic_area_id = sa.id
WHERE n.is_enabled = 1 AND n.is_deleted = 0;

-- =============================================
-- 第八部分：函数
-- =============================================

DELIMITER //
CREATE FUNCTION IF NOT EXISTS calculate_distance(
    lat1 DECIMAL(10,8), lon1 DECIMAL(11,8),
    lat2 DECIMAL(10,8), lon2 DECIMAL(11,8)
) RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE R DECIMAL(10,2) DEFAULT 6371000.0;
    DECLARE dlat DECIMAL(10,8);
    DECLARE dlon DECIMAL(11,8);
    DECLARE a DECIMAL(20,16);
    DECLARE c DECIMAL(20,16);
    DECLARE distance DECIMAL(10,2);
    
    SET dlat = RADIANS(lat2 - lat1);
    SET dlon = RADIANS(lon2 - lon1);
    SET a = SIN(dlat/2) * SIN(dlat/2) + 
            COS(RADIANS(lat1)) * COS(RADIANS(lat2)) * SIN(dlon/2) * SIN(dlon/2);
    SET c = 2 * ATAN2(SQRT(a), SQRT(1-a));
    SET distance = R * c;
    
    RETURN distance;
END//
DELIMITER ;

-- =============================================
-- 第九部分：事件（定期清理）
-- =============================================

DELIMITER //
CREATE EVENT IF NOT EXISTS ev_navigation_cleanup
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    DELETE FROM t_navigation_route_cache WHERE expires_at < NOW();
    DELETE FROM t_navigation_crowd_level 
    WHERE recorded_at < DATE_SUB(NOW(), INTERVAL 30 DAY) AND source IN (0, 1);
    DELETE FROM t_navigation_osm_import_log 
    WHERE started_at < DATE_SUB(NOW(), INTERVAL 90 DAY);
    OPTIMIZE TABLE t_navigation_route_cache;
    OPTIMIZE TABLE t_navigation_crowd_level;
END//
DELIMITER ;

-- =============================================
-- 第十部分：测试数据
-- =============================================

-- 插入景区数据
INSERT INTO t_temp_scenic_area (id, name, type, city, latitude, longitude, is_enabled, is_deleted) VALUES
(1, '故宫博物院', 0, '北京', 39.916345, 116.397155, 1, 0);

-- 插入建筑数据
INSERT INTO t_temp_building (id, scenic_area_id, name, type, floor_count, latitude, longitude, is_enabled, is_deleted) VALUES
(1, 1, '太和殿', 4, 3, 39.917000, 116.397500, 1, 0),
(2, 1, '乾清宫', 4, 2, 39.916500, 116.397000, 1, 0),
(3, 1, '养心殿', 4, 2, 39.915800, 116.396500, 1, 0);

-- 插入路网节点数据（模拟故宫游览路线）
INSERT INTO t_navigation_road_node (id, osm_id, scenic_area_id, name, node_type, latitude, longitude, floor_number, is_important, is_accessible, is_enabled, is_deleted) VALUES
(1, 100001, 1, '午门入口', 0, 39.916100, 116.397000, 1, 1, 1, 1, 0),
(2, 100002, 1, '午门内广场', 1, 39.916200, 116.397100, 1, 1, 1, 1, 0),
(3, 100003, 1, '太和门', 1, 39.916400, 116.397200, 1, 1, 1, 1, 0),
(4, 100004, 1, '太和门广场', 1, 39.916500, 116.397300, 1, 0, 1, 1, 0),
(5, 100005, 1, '太和殿', 2, 39.916800, 116.397500, 1, 1, 1, 1, 0),
(6, 100006, 1, '中和殿岔路口', 1, 39.916700, 116.397400, 1, 0, 1, 1, 0),
(7, 100007, 1, '中和殿', 2, 39.916600, 116.397300, 1, 0, 1, 1, 0),
(8, 100008, 1, '保和殿', 2, 39.916500, 116.397100, 1, 0, 1, 1, 0),
(9, 100009, 1, '乾清门', 1, 39.916300, 116.396800, 1, 1, 1, 1, 0),
(10, 100010, 1, '乾清宫', 2, 39.916200, 116.396500, 1, 1, 1, 1, 0),
(11, 100011, 1, '交泰殿岔路', 1, 39.916100, 116.396300, 1, 0, 1, 1, 0),
(12, 100012, 1, '养心殿', 2, 39.915800, 116.396200, 1, 1, 1, 1, 0),
(13, 100013, 1, '神武门出口', 0, 39.915600, 116.397000, 1, 1, 1, 1, 0);

-- 插入路径段数据
INSERT INTO t_navigation_road_edge (id, osm_way_id, scenic_area_id, from_node_id, to_node_id, name, distance, walk_time, bike_time, shuttle_time, transport_type, is_bidirectional, is_enabled, is_deleted) VALUES
(1, 200001, 1, 1, 2, '午门通道', 80.0, 60, 30, 45, 5, 1, 1, 0),
(2, 200002, 1, 2, 1, '午门通道', 80.0, 60, 30, 45, 5, 1, 1, 0),
(3, 200003, 1, 2, 3, '中间御道', 150.0, 120, 50, 90, 5, 1, 1, 0),
(4, 200004, 1, 3, 2, '中间御道', 150.0, 120, 50, 90, 5, 1, 1, 0),
(5, 200005, 1, 3, 4, '太和门广场', 60.0, 45, 20, 35, 5, 1, 1, 0),
(6, 200006, 1, 4, 3, '太和门广场', 60.0, 45, 20, 35, 5, 1, 1, 0),
(7, 200007, 1, 4, 5, '太和殿前广场', 200.0, 150, 60, 120, 5, 1, 1, 0),
(8, 200008, 1, 5, 4, '太和殿前广场', 200.0, 150, 60, 120, 5, 1, 1, 0),
(9, 200009, 1, 4, 6, '西侧通道', 120.0, 90, 40, 70, 5, 1, 1, 0),
(10, 200010, 1, 6, 4, '西侧通道', 120.0, 90, 40, 70, 5, 1, 1, 0),
(11, 200011, 1, 6, 7, '中和殿前', 50.0, 40, 15, 30, 5, 1, 1, 0),
(12, 200012, 1, 7, 6, '中和殿前', 50.0, 40, 15, 30, 5, 1, 1, 0),
(13, 200013, 1, 7, 8, '保和殿前', 80.0, 60, 25, 50, 5, 1, 1, 0),
(14, 200014, 1, 8, 7, '保和殿前', 80.0, 60, 25, 50, 5, 1, 1, 0),
(15, 200015, 1, 8, 4, '东侧返回通道', 100.0, 75, 30, 60, 5, 1, 1, 0),
(16, 200016, 1, 9, 4, '北向通道', 180.0, 135, 55, 100, 5, 1, 1, 0),
(17, 200017, 1, 4, 9, '北向通道', 180.0, 135, 55, 100, 5, 1, 1, 0),
(18, 200018, 1, 9, 10, '乾清宫前', 100.0, 75, 30, 60, 5, 1, 1, 0),
(19, 200019, 1, 10, 9, '乾清宫前', 100.0, 75, 30, 60, 5, 1, 1, 0),
(20, 200020, 1, 10, 11, '西侧通道', 80.0, 60, 25, 50, 5, 1, 1, 0),
(21, 200021, 1, 11, 10, '西侧通道', 80.0, 60, 25, 50, 5, 1, 1, 0),
(22, 200022, 1, 11, 12, '养心殿前', 60.0, 45, 20, 35, 5, 1, 1, 0),
(23, 200023, 1, 12, 11, '养心殿前', 60.0, 45, 20, 35, 5, 1, 1, 0),
(24, 200024, 1, 1, 13, '东部长廊', 300.0, 240, 100, 180, 5, 1, 1, 0),
(25, 200025, 1, 13, 1, '东部长廊', 300.0, 240, 100, 180, 5, 1, 1, 0),
(26, 200026, 1, 13, 9, '北侧通道', 150.0, 120, 50, 90, 5, 1, 1, 0),
(27, 200027, 1, 9, 13, '北侧通道', 150.0, 120, 50, 90, 5, 1, 1, 0);

-- 插入拍照点数据
INSERT INTO t_navigation_photo_spot (id, scenic_area_id, name, target_name, description, recommended_angle, best_time, best_season, rating, check_in_count, latitude, longitude, is_enabled, is_deleted) VALUES
(1, 1, '太和殿全景拍摄点', '太和殿', '拍摄太和殿全景的最佳位置', '南向45度', '上午9-11点', '春季', 4.8, 5000, 39.916900, 116.397600, 1, 0),
(2, 1, '中和殿拍摄点', '中和殿', '中和殿侧面拍摄', '东向', '上午10-12点', '四季', 4.5, 3000, 39.916700, 116.397400, 1, 0),
(3, 1, '乾清宫拍摄点', '乾清宫', '乾清宫正门', '南向', '下午2-4点', '秋季', 4.6, 3500, 39.916300, 116.396600, 1, 0),
(4, 1, '神武门拍摄点', '神武门', '神武门远景', '北向', '日落前1小时', '秋季', 4.7, 4500, 39.915700, 116.397100, 1, 0);

-- 插入拥挤度数据
INSERT INTO t_navigation_crowd_level (id, node_id, level, crowd_count, capacity, density, source, recorded_at, is_deleted) VALUES
(1, 1, 1, 50, 200, 0.25, 1, NOW(), 0),
(2, 3, 2, 120, 200, 0.60, 1, NOW(), 0),
(3, 5, 3, 180, 200, 0.90, 1, NOW(), 0),
(4, 10, 1, 30, 150, 0.20, 1, NOW(), 0),
(5, 13, 2, 80, 200, 0.40, 1, NOW(), 0);

-- =============================================
-- 第十一部分：验证查询
-- =============================================

SELECT '=== Navigation数据库构建完成 ===' as message;

-- 显示所有表
SHOW TABLES LIKE '%navigation%';
SHOW TABLES LIKE '%temp_%';

-- 验证数据
SELECT '验证数据统计:' as info;
SELECT '景区数据:' as info; SELECT COUNT(*) as count FROM t_temp_scenic_area;
SELECT '建筑数据:' as info; SELECT COUNT(*) as count FROM t_temp_building;
SELECT '路网节点:' as info; SELECT COUNT(*) as count FROM t_navigation_road_node;
SELECT '路径段:' as info; SELECT COUNT(*) as count FROM t_navigation_road_edge;
SELECT '拍照点:' as info; SELECT COUNT(*) as count FROM t_navigation_photo_spot;
SELECT '拥挤度:' as info; SELECT COUNT(*) as count FROM t_navigation_crowd_level;

-- 显示节点列表
SELECT id, name, node_type, latitude, longitude FROM t_navigation_road_node ORDER BY id;

-- =============================================
-- 脚本结束
-- =============================================
