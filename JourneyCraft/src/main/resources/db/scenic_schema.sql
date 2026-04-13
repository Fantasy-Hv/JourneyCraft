-- JourneyCraft scenic module schema
-- 作用：为 scenic 模块和后续 navigation 模块提供基础景点数据表

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS t_crowd_level;
DROP TABLE IF EXISTS t_facility;
DROP TABLE IF EXISTS t_building;
DROP TABLE IF EXISTS t_scenic_area;

CREATE TABLE t_scenic_area (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
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
    contact_phone VARCHAR(100) DEFAULT NULL COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1营业 0关闭',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_scenic_area_type_city_status (type, city, status),
    KEY idx_scenic_area_city (city),
    KEY idx_scenic_area_name (name),
    KEY idx_scenic_area_heat_score (heat_score),
    KEY idx_scenic_area_rating (rating)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='景区/校园表';

CREATE TABLE t_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区 ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    type TINYINT NOT NULL DEFAULT 0 COMMENT '类型：0教学楼 1图书馆 2食堂 3宿舍 4景点建筑',
    floor_count INT NOT NULL DEFAULT 0 COMMENT '楼层数',
    latitude DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
    description TEXT COMMENT '描述',
    indoor_map JSON DEFAULT NULL COMMENT '室内地图数据',
    images JSON DEFAULT NULL COMMENT '图片列表',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_building_scenic_area_id (scenic_area_id),
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
    name VARCHAR(100) NOT NULL COMMENT '名称',
    type INT NOT NULL COMMENT '设施类型',
    subtype VARCHAR(50) DEFAULT NULL COMMENT '子类型',
    facility_class TINYINT DEFAULT NULL COMMENT '餐饮分类',
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
    KEY idx_facility_type (type),
    KEY idx_facility_name (name),
    CONSTRAINT fk_facility_scenic_area
        FOREIGN KEY (scenic_area_id) REFERENCES t_scenic_area(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_facility_building
        FOREIGN KEY (building_id) REFERENCES t_building(id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='设施表';

CREATE TABLE t_crowd_level (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区 ID',
    node_id BIGINT NOT NULL COMMENT '路网节点 ID',
    level INT NOT NULL COMMENT '拥挤等级',
    crowd_count INT NOT NULL DEFAULT 0 COMMENT '拥挤人数',
    recorded_at DATETIME NOT NULL COMMENT '记录时间',
    source INT NOT NULL DEFAULT 1 COMMENT '来源：1用户上报 2系统采集',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_crowd_level_scenic_area_id (scenic_area_id),
    KEY idx_crowd_level_node_id (node_id),
    KEY idx_crowd_level_recorded_at (recorded_at),
    CONSTRAINT fk_crowd_level_scenic_area
        FOREIGN KEY (scenic_area_id) REFERENCES t_scenic_area(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='拥挤度记录表';

SET FOREIGN_KEY_CHECKS = 1;
