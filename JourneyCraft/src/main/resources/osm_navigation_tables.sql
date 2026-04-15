-- JourneyCraft 导航模块数据库设计 (基于OSM数据)
-- 版本: 1.0
-- 日期: 2026-04-11
-- 说明: 针对北京地区OSM PBF数据优化的路径规划数据库设计

-- 1. 地点表 (旅游系统优化版，包含景点属性)
CREATE TABLE IF NOT EXISTS place (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地点ID',
    name VARCHAR(100) NOT NULL COMMENT '地点名称',
    type TINYINT NOT NULL DEFAULT 0 COMMENT '类型: 0=景区, 1=校园, 2=旅馆, 3=饭店, 4=建筑, 5=区域',
    city VARCHAR(50) COMMENT '城市',
    address VARCHAR(200) COMMENT '详细地址',
    latitude DECIMAL(10, 8) COMMENT '纬度',
    longitude DECIMAL(11, 8) COMMENT '经度',
    description TEXT COMMENT '描述',
    rating DECIMAL(3, 2) DEFAULT 0.0 COMMENT '评分(0-5)',
    heat_score INT DEFAULT 0 COMMENT '热度分',
    visit_count INT DEFAULT 0 COMMENT '浏览数',
    ticket_price DECIMAL(10, 2) COMMENT '门票价格',
    booking_url VARCHAR(300) COMMENT '购票链接',
    opening_hours JSON COMMENT '开放时间(JSON格式)',
    images JSON COMMENT '图片URL数组(JSON格式)',
    contact_phone VARCHAR(100) COMMENT '联系电话',
    boundary_polygon POLYGON COMMENT '边界多边形(可选，用于区域范围)',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=营业 0=关闭',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_location (latitude, longitude),
    INDEX idx_status (status),
    INDEX idx_rating (rating),
    INDEX idx_heat (heat_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地点表(包含景点属性)';

-- 2. 建筑物表 (景点内的建筑，支持室内导航)
CREATE TABLE IF NOT EXISTS building (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '建筑物ID',
    place_id BIGINT NOT NULL COMMENT '所属景点ID(place.type=0)',
    name VARCHAR(100) NOT NULL COMMENT '建筑物名称',
    type TINYINT NOT NULL DEFAULT 4 COMMENT '类型: 0=教学楼, 1=图书馆, 2=食堂, 3=宿舍, 4=景点建筑, 5=旅馆, 6=饭店',
    floor_count INT DEFAULT 1 COMMENT '楼层数',
    latitude DECIMAL(10, 8) COMMENT '纬度',
    longitude DECIMAL(11, 8) COMMENT '经度',
    description TEXT COMMENT '描述',
    indoor_map JSON COMMENT '室内地图数据(JSON格式)',
    images JSON COMMENT '图片URL数组(JSON格式)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0=关闭, 1=启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_place (place_id),
    INDEX idx_type (type),
    INDEX idx_location (latitude, longitude),
    CONSTRAINT fk_building_place FOREIGN KEY (place_id) REFERENCES place(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='建筑物表';

-- 3. OSM路网节点表 (基于OSM Node)
CREATE TABLE IF NOT EXISTS road_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '节点ID(自增主键)',
    osm_id BIGINT NOT NULL COMMENT 'OSM节点ID',
    place_id BIGINT COMMENT '所属地点ID(可选)',
    building_id BIGINT COMMENT '所属建筑ID(可选, node_type=3时使用)',
    name VARCHAR(100) COMMENT '节点名称',
    node_type TINYINT NOT NULL DEFAULT 1 COMMENT '节点类型: 0=入口, 1=路口, 2=POI, 3=建筑入口/内部点, 4=拍照点, 5=OSM普通节点',
    latitude DECIMAL(10, 8) NOT NULL COMMENT '纬度',
    longitude DECIMAL(11, 8) NOT NULL COMMENT '经度',
    floor_number INT DEFAULT 1 COMMENT '楼层(室内导航用)',
    tags JSON COMMENT 'OSM标签(JSON格式，存储原始标签)',
    is_important TINYINT DEFAULT 0 COMMENT '是否重要节点: 0=否, 1=是',
    status TINYINT DEFAULT 1 COMMENT '状态: 0=禁用, 1=启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_osm_id (osm_id),
    INDEX idx_place (place_id),
    INDEX idx_building (building_id),
    INDEX idx_location (latitude, longitude),
    INDEX idx_type (node_type),
    INDEX idx_important (is_important),
    SPATIAL INDEX idx_spatial (latitude, longitude),
    CONSTRAINT fk_road_node_place FOREIGN KEY (place_id) REFERENCES place(id) ON DELETE SET NULL,
    CONSTRAINT fk_road_node_building FOREIGN KEY (building_id) REFERENCES building(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路网节点表';

-- 3. OSM路径段表 (基于OSM Way，拆分成边)
CREATE TABLE IF NOT EXISTS road_edge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '路径ID(自增主键)',
    osm_way_id BIGINT NOT NULL COMMENT 'OSM路径ID',
    from_node_id BIGINT NOT NULL COMMENT '起始节点ID',
    to_node_id BIGINT NOT NULL COMMENT '目标节点ID',
    name VARCHAR(100) COMMENT '路径名称',
    distance DECIMAL(10, 2) NOT NULL COMMENT '距离(米)',
    walk_time INT COMMENT '步行时间(秒)',
    bike_time INT COMMENT '自行车时间(秒)',
    shuttle_time INT COMMENT '电瓶车时间(秒)',
    transport_type TINYINT NOT NULL DEFAULT 0 COMMENT '通行方式: 0=未知, 1=仅步行, 2=仅自行车, 3=仅车辆, 4=步行+自行车, 5=全部',
    is_bidirectional TINYINT DEFAULT 1 COMMENT '是否双向: 0=否, 1=是',
    highway_type VARCHAR(50) COMMENT 'OSM道路类型(highway标签值)',
    surface VARCHAR(50) COMMENT '路面类型',
    max_speed INT COMMENT '最大速度(km/h)',
    tags JSON COMMENT 'OSM标签(JSON格式)',
    congestion_level DECIMAL(3, 2) DEFAULT 0.0 COMMENT '拥挤度(0-1)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0=禁用, 1=启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_edge_direction (from_node_id, to_node_id),
    INDEX idx_osm_way (osm_way_id),
    INDEX idx_from_node (from_node_id),
    INDEX idx_to_node (to_node_id),
    INDEX idx_transport (transport_type),
    INDEX idx_highway (highway_type),
    INDEX idx_status (status),
    CONSTRAINT fk_road_edge_from_node FOREIGN KEY (from_node_id) REFERENCES road_node(id) ON DELETE CASCADE,
    CONSTRAINT fk_road_edge_to_node FOREIGN KEY (to_node_id) REFERENCES road_node(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路径段表';

-- 4. 拥挤度记录表
CREATE TABLE IF NOT EXISTS crowd_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    node_id BIGINT NOT NULL COMMENT '节点ID',
    level TINYINT NOT NULL DEFAULT 0 COMMENT '拥挤等级: 0=舒适, 1=适中, 2=拥挤, 3=非常拥挤',
    crowd_count INT COMMENT '人数',
    capacity INT COMMENT '容量',
    source TINYINT DEFAULT 1 COMMENT '数据来源: 0=传感器, 1=用户上报, 2=预测值, 3=OSM实时',
    recorded_at DATETIME NOT NULL COMMENT '记录时间',
    predicted_at DATETIME COMMENT '预测时间(未来)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_node (node_id),
    INDEX idx_recorded (recorded_at),
    INDEX idx_source (source),
    CONSTRAINT fk_crowd_level_node FOREIGN KEY (node_id) REFERENCES road_node(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拥挤度记录表';

-- 5. 拍照点表 (可选)
CREATE TABLE IF NOT EXISTS photo_spot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '拍照点ID',
    place_id BIGINT NOT NULL COMMENT '所属地点ID',
    node_id BIGINT COMMENT '关联节点ID',
    name VARCHAR(100) NOT NULL COMMENT '拍照点名称',
    target_name VARCHAR(100) COMMENT '拍摄目标',
    recommended_angle VARCHAR(50) COMMENT '推荐角度',
    best_time VARCHAR(100) COMMENT '最佳时间',
    description TEXT COMMENT '描述',
    sample_image VARCHAR(255) COMMENT '示例图片URL',
    rating DECIMAL(3, 2) DEFAULT 0.0 COMMENT '评分',
    check_in_count INT DEFAULT 0 COMMENT '打卡次数',
    latitude DECIMAL(10, 8) COMMENT '纬度(拍照点位置)',
    longitude DECIMAL(11, 8) COMMENT '经度(拍照点位置)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0=禁用, 1=启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_place (place_id),
    INDEX idx_node (node_id),
    INDEX idx_location (latitude, longitude),
    INDEX idx_rating (rating),
    CONSTRAINT fk_photo_spot_place FOREIGN KEY (place_id) REFERENCES place(id) ON DELETE CASCADE,
    CONSTRAINT fk_photo_spot_node FOREIGN KEY (node_id) REFERENCES road_node(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拍照点表';

-- 6. OSM导入记录表 (用于跟踪数据导入状态)
CREATE TABLE IF NOT EXISTS osm_import_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小(字节)',
    region VARCHAR(100) COMMENT '区域(如北京)',
    import_type VARCHAR(50) COMMENT '导入类型: node, way, relation',
    total_records INT DEFAULT 0 COMMENT '总记录数',
    success_count INT DEFAULT 0 COMMENT '成功数',
    failed_count INT DEFAULT 0 COMMENT '失败数',
    error_message TEXT COMMENT '错误信息',
    started_at DATETIME NOT NULL COMMENT '开始时间',
    finished_at DATETIME COMMENT '结束时间',
    status VARCHAR(20) DEFAULT 'processing' COMMENT '状态: processing, completed, failed',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_file (file_name),
    INDEX idx_status (status),
    INDEX idx_region (region),
    INDEX idx_started (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OSM导入记录表';

-- 7. 路径规划缓存表 (可选，用于优化性能)
CREATE TABLE IF NOT EXISTS route_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '缓存ID',
    start_node_id BIGINT NOT NULL COMMENT '起始节点ID',
    end_node_id BIGINT NOT NULL COMMENT '目标节点ID',
    transport_type TINYINT NOT NULL COMMENT '通行方式',
    route_nodes JSON NOT NULL COMMENT '路径节点ID列表(JSON数组)',
    total_distance DECIMAL(10, 2) NOT NULL COMMENT '总距离(米)',
    total_time INT NOT NULL COMMENT '总时间(秒)',
    calculated_at DATETIME NOT NULL COMMENT '计算时间',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    hit_count INT DEFAULT 0 COMMENT '命中次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_route_query (start_node_id, end_node_id, transport_type),
    INDEX idx_expires (expires_at),
    INDEX idx_hit_count (hit_count),
    CONSTRAINT fk_route_cache_start_node FOREIGN KEY (start_node_id) REFERENCES road_node(id) ON DELETE CASCADE,
    CONSTRAINT fk_route_cache_end_node FOREIGN KEY (end_node_id) REFERENCES road_node(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路径规划缓存表';

-- 初始化数据：插入北京区域和景点记录
INSERT INTO place (id, name, type, city, latitude, longitude, description, rating, heat_score, visit_count, ticket_price, opening_hours, images, contact_phone) VALUES
(1, '北京市', 5, '北京', 39.9042, 116.4074, '中国首都，政治文化中心', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, '故宫博物院', 0, '北京', 39.916345, 116.397155, '中国明清两代的皇家宫殿，旧称紫禁城，是中国古代宫廷建筑之精华', 4.9, 9800, 100000, 60.00, 
    '{"monday": {"open": "08:30", "close": "17:00"}, "tuesday": {"open": "08:30", "close": "17:00"}, "wednesday": {"open": "08:30", "close": "17:00"}, "thursday": {"open": "08:30", "close": "17:00"}, "friday": {"open": "08:30", "close": "17:00"}, "saturday": {"open": "08:30", "close": "17:00"}, "sunday": {"open": "08:30", "close": "17:00"}}',
    '["http://example.com/gugong1.jpg", "http://example.com/gugong2.jpg"]',
    '010-85007422'),
(3, '颐和园', 0, '北京', 39.999982, 116.275465, '中国清朝时期皇家园林，前身为清漪园，是以昆明湖、万寿山为基址，以杭州西湖为蓝本的大型山水园林', 4.8, 8500, 80000, 30.00,
    '{"monday": {"open": "06:30", "close": "18:00"}, "tuesday": {"open": "06:30", "close": "18:00"}, "wednesday": {"open": "06:30", "close": "18:00"}, "thursday": {"open": "06:30", "close": "18:00"}, "friday": {"open": "06:30", "close": "18:00"}, "saturday": {"open": "06:30", "close": "18:00"}, "sunday": {"open": "06:30", "close": "18:00"}}',
    '["http://example.com/yhey1.jpg", "http://example.com/yhey2.jpg"]',
    '010-62881144')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    type = VALUES(type),
    city = VALUES(city),
    latitude = VALUES(latitude),
    longitude = VALUES(longitude),
    description = VALUES(description),
    rating = VALUES(rating),
    heat_score = VALUES(heat_score),
    visit_count = VALUES(visit_count),
    ticket_price = VALUES(ticket_price),
    opening_hours = VALUES(opening_hours),
    images = VALUES(images),
    contact_phone = VALUES(contact_phone),
    updated_at = CURRENT_TIMESTAMP;

-- 创建函数：计算两点间距离(米)
DELIMITER //
CREATE FUNCTION IF NOT EXISTS calculate_distance(
    lat1 DECIMAL(10,8), lon1 DECIMAL(11,8),
    lat2 DECIMAL(10,8), lon2 DECIMAL(11,8)
) RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    -- 使用Haversine公式计算球面距离
    DECLARE R DECIMAL(10,2) DEFAULT 6371000.0; -- 地球半径(米)
    DECLARE dlat DECIMAL(10,8);
    DECLARE dlon DECIMAL(11,8);
    DECLARE a DECIMAL(20,16);
    DECLARE c DECIMAL(20,16);
    DECLARE distance DECIMAL(10,2);
    
    SET dlat = RADIANS(lat2 - lat1);
    SET dlon = RADIANS(lon2 - lon1);
    SET a = SIN(dlat/2) * SIN(dlat/2) + 
            COS(RADIANS(lat1)) * COS(RADIANS(lat2)) * 
            SIN(dlon/2) * SIN(dlon/2);
    SET c = 2 * ATAN2(SQRT(a), SQRT(1-a));
    SET distance = R * c;
    
    RETURN distance;
END//
DELIMITER ;

-- 创建视图：重要节点视图
CREATE OR REPLACE VIEW v_important_nodes AS
SELECT 
    rn.id,
    rn.osm_id,
    rn.name,
    rn.node_type,
    rn.latitude,
    rn.longitude,
    p.name as place_name,
    p.type as place_type,
    CASE rn.node_type
        WHEN 0 THEN '入口'
        WHEN 1 THEN '路口'
        WHEN 2 THEN 'POI'
        WHEN 3 THEN '设施'
        WHEN 4 THEN '拍照点'
        ELSE '普通节点'
    END as node_type_name
FROM road_node rn
LEFT JOIN place p ON rn.place_id = p.id
WHERE rn.is_important = 1 AND rn.status = 1;

-- 创建视图：可步行路径视图
CREATE OR REPLACE VIEW v_walkable_edges AS
SELECT 
    re.id,
    re.osm_way_id,
    re.from_node_id,
    re.to_node_id,
    re.name,
    re.distance,
    re.walk_time,
    re.transport_type,
    re.highway_type,
    fn.latitude as from_lat,
    fn.longitude as from_lon,
    tn.latitude as to_lat,
    tn.longitude as to_lon
FROM road_edge re
JOIN road_node fn ON re.from_node_id = fn.id
JOIN road_node tn ON re.to_node_id = tn.id
WHERE re.status = 1 
AND (re.transport_type = 1 OR re.transport_type = 4 OR re.transport_type = 5) -- 可步行
AND re.distance > 0;

-- 注释说明
/*
表设计说明:
1. road_node表存储OSM节点，osm_id为OSM原始ID，需要建立唯一索引
2. road_edge表存储OSM路径段，每条边连接两个节点，osm_way_id为OSM原始路径ID
3. transport_type枚举值说明:
   0: 未知 (需要从OSM标签推断)
   1: 仅步行 (footway, pedestrian, steps等)
   2: 仅自行车 (cycleway)
   3: 仅车辆 (motorway, trunk, primary等)
   4: 步行+自行车 (path, living_street等)
   5: 全部 (residential, unclassified等)
4. OSM标签处理: tags字段存储JSON格式的OSM标签，便于查询和过滤
5. 空间索引: road_node表建立空间索引，支持地理位置查询
6. 性能优化: route_cache表缓存常用路径查询结果

OSM数据导入流程:
1. 解析PBF文件，提取Node和Way
2. Node导入road_node表，存储osm_id、经纬度、tags
3. Way拆分成Edge，计算两点间距离，推断transport_type
4. 根据tags标记重要节点(node_type)和道路类型(highway_type)
*/

-- 索引优化建议
-- ALTER TABLE road_node ADD SPATIAL INDEX idx_spatial (latitude, longitude);
-- ALTER TABLE place ADD SPATIAL INDEX idx_boundary (boundary_polygon);