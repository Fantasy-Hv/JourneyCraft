-- JourneyCraft scenic module schema
-- 作用：为 scenic 模块和后续 navigation 模块提供基础景点数据表

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS t_crowd_level;
DROP TABLE IF EXISTS t_food_place;
DROP TABLE IF EXISTS t_facility;
DROP TABLE IF EXISTS t_building;
DROP TABLE IF EXISTS t_scenic_area;

CREATE TABLE t_scenic_area (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    node_id BIGINT DEFAULT NULL COMMENT '关联主导航节点 ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    type TINYINT NOT NULL COMMENT '类型：0景区 1校园',
    city VARCHAR(50) NOT NULL DEFAULT '' COMMENT '城市',
    address VARCHAR(200) NOT NULL DEFAULT '' COMMENT '详细地址',
    latitude DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
    description TEXT COMMENT '描述',
    rating DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '评分',
    heat_score INT NOT NULL DEFAULT 0 COMMENT '热度分',
    visit_count INT NOT NULL DEFAULT 0 COMMENT '浏览数',
    ticket_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '门票价格',
    booking_url VARCHAR(300) DEFAULT NULL COMMENT '购票链接',
    opening_hours JSON DEFAULT NULL COMMENT '开放时间',
    images JSON DEFAULT NULL COMMENT '图片列表',
    tags JSON DEFAULT NULL COMMENT '标签列表',
    contact_phone VARCHAR(100) DEFAULT NULL COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1营业 0关闭',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_scenic_area_node_id (node_id),
    KEY idx_scenic_area_type_city_status (type, city, status),
    KEY idx_scenic_area_city (city),
    KEY idx_scenic_area_name (name),
    KEY idx_scenic_area_heat_score (heat_score),
    KEY idx_scenic_area_rating (rating)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='景区/校园表';

CREATE TABLE t_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区 ID',
    node_id BIGINT DEFAULT NULL COMMENT '关联主导航节点 ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    type TINYINT NOT NULL DEFAULT 0 COMMENT '类型：0教学楼 1图书馆 2食堂 3宿舍 4景点建筑',
    floor_count INT NOT NULL DEFAULT 0 COMMENT '楼层数',
    latitude DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
    description TEXT COMMENT '描述',
    tags JSON DEFAULT NULL COMMENT '建筑标签（风格/内容元素）',
    indoor_map JSON DEFAULT NULL COMMENT '室内地图数据',
    images JSON DEFAULT NULL COMMENT '图片列表',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_building_scenic_area_id (scenic_area_id),
    KEY idx_building_node_id (node_id),
    KEY idx_building_type (type),
    KEY idx_building_name (name),
    CONSTRAINT fk_building_scenic_area
        FOREIGN KEY (scenic_area_id) REFERENCES t_scenic_area(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='建筑物表';

CREATE TABLE t_facility (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区 ID',
    building_id BIGINT DEFAULT NULL COMMENT '所属建筑 ID',
    node_id BIGINT DEFAULT NULL COMMENT '预留导航节点字段',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    type INT NOT NULL COMMENT '设施类型',
    subtype VARCHAR(50) DEFAULT NULL COMMENT '子类型',
    facility_class TINYINT DEFAULT NULL COMMENT '设施扩展分类（保留字段）',
    latitude DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
    floor_number INT DEFAULT NULL COMMENT '所在楼层',
    description TEXT COMMENT '描述',
    rating DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '评分',
    review_count INT NOT NULL DEFAULT 0 COMMENT '评价数',
    heat_score INT NOT NULL DEFAULT 0 COMMENT '热度分',
    price_range VARCHAR(20) DEFAULT NULL COMMENT '价格区间',
    opening_hours JSON DEFAULT NULL COMMENT '营业时间',
    images JSON DEFAULT NULL COMMENT '图片列表',
    contact_info VARCHAR(100) DEFAULT NULL COMMENT '联系电话',
    tags JSON DEFAULT NULL COMMENT '标签列表',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1营业 0关闭',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_facility_scenic_area_id (scenic_area_id),
    KEY idx_facility_building_id (building_id),
    KEY idx_facility_node_id (node_id),
    KEY idx_facility_type (type),
    KEY idx_facility_name (name),
    CONSTRAINT fk_facility_scenic_area
        FOREIGN KEY (scenic_area_id) REFERENCES t_scenic_area(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_facility_building
        FOREIGN KEY (building_id) REFERENCES t_building(id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='通用设施表';

-- 历史上餐饮类数据如果来自设施表，后续应迁移到 t_food_place
CREATE TABLE t_food_place (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区 ID',
    building_id BIGINT DEFAULT NULL COMMENT '所属建筑 ID',
    node_id BIGINT DEFAULT NULL COMMENT '预留导航节点字段',
    source_facility_id BIGINT DEFAULT NULL COMMENT '来源设施 ID（兼容迁移）',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    category VARCHAR(50) NOT NULL COMMENT '餐饮类型：餐厅/小吃/食堂/饮品/美食广场/外卖窗口',
    cuisine_type VARCHAR(50) DEFAULT NULL COMMENT '菜系/风味',
    price_level TINYINT DEFAULT NULL COMMENT '价格等级',
    avg_price DECIMAL(10,2) DEFAULT NULL COMMENT '人均价格',
    price_range VARCHAR(20) DEFAULT NULL COMMENT '价格区间',
    latitude DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
    floor_number INT DEFAULT NULL COMMENT '所在楼层',
    description TEXT COMMENT '描述',
    opening_hours JSON DEFAULT NULL COMMENT '营业时间',
    images JSON DEFAULT NULL COMMENT '图片列表',
    tags JSON DEFAULT NULL COMMENT '标签列表',
    contact_info VARCHAR(100) DEFAULT NULL COMMENT '联系电话',
    rating DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '评分',
    review_count INT NOT NULL DEFAULT 0 COMMENT '评价数',
    heat_score INT NOT NULL DEFAULT 0 COMMENT '热度分',
    recommend_score INT NOT NULL DEFAULT 0 COMMENT '推荐分',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1营业 0关闭',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_food_place_scenic_area_id (scenic_area_id),
    KEY idx_food_place_building_id (building_id),
    KEY idx_food_place_node_id (node_id),
    KEY idx_food_place_source_facility_id (source_facility_id),
    KEY idx_food_place_category (category),
    KEY idx_food_place_cuisine_type (cuisine_type),
    KEY idx_food_place_name (name),
    KEY idx_food_place_heat_score (heat_score),
    KEY idx_food_place_recommend_score (recommend_score),
    CONSTRAINT fk_food_place_scenic_area
        FOREIGN KEY (scenic_area_id) REFERENCES t_scenic_area(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_food_place_building
        FOREIGN KEY (building_id) REFERENCES t_building(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_food_place_source_facility
        FOREIGN KEY (source_facility_id) REFERENCES t_facility(id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='美食点表';

CREATE TABLE t_crowd_level (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区 ID',
    node_id BIGINT NOT NULL COMMENT '路网节点 ID',
    level INT NOT NULL COMMENT '拥挤等级',
    crowd_count INT DEFAULT NULL COMMENT '拥挤人数，可为空',
    capacity INT DEFAULT NULL COMMENT '容量上限，可为空',
    predicted_at DATETIME DEFAULT NULL COMMENT '预测时间，可为空',
    recorded_at DATETIME NOT NULL COMMENT '记录时间',
    source INT NOT NULL DEFAULT 1 COMMENT '来源：1用户上报 2系统采集',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_crowd_level_scenic_area_id (scenic_area_id),
    KEY idx_crowd_level_node_id (node_id),
    KEY idx_crowd_level_predicted_at (predicted_at),
    KEY idx_crowd_level_recorded_at (recorded_at),
    KEY idx_crowd_level_scenic_node_time (scenic_area_id, node_id, recorded_at),
    CONSTRAINT fk_crowd_level_scenic_area
        FOREIGN KEY (scenic_area_id) REFERENCES t_scenic_area(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='拥挤度记录表';

SET FOREIGN_KEY_CHECKS = 1;
