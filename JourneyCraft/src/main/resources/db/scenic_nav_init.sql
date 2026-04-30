-- JourneyCraft Scenic + Navigation 联合建表脚本
-- 用于测试附近设施接口
-- 执行方式: source scenic_nav_init.sql

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 创建景区表
-- ============================================
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
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1营业 0关闭',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景区/校园表';

-- 插入测试景区数据（ID=1，与Navigation测试数据一致）
INSERT INTO t_scenic_area (id, name, type, city, latitude, longitude, rating, heat_score, status) VALUES
(1, '故宫博物院', 0, '北京', 39.9163, 116.3971, 4.90, 9999, 1);

-- ============================================
-- 2. 创建建筑表
-- ============================================
DROP TABLE IF EXISTS t_building;
CREATE TABLE t_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区 ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    type TINYINT NOT NULL DEFAULT 0 COMMENT '类型：0教学楼 1图书馆 2食堂 3宿舍 4景点建筑',
    floor_count INT NOT NULL DEFAULT 0 COMMENT '楼层数',
    latitude DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
    description TEXT COMMENT '描述',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_building_scenic_area FOREIGN KEY (scenic_area_id) REFERENCES t_scenic_area(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='建筑物表';

-- 插入测试建筑数据
INSERT INTO t_building (id, scenic_area_id, name, type, floor_count, latitude, longitude) VALUES
(1, 1, '太和殿', 4, 2, 39.9165, 116.3973),
(2, 1, '乾清宫', 4, 2, 39.9166, 116.3975);

-- ============================================
-- 3. 创建设施表（核心表）
-- ============================================
DROP TABLE IF EXISTS t_facility;
CREATE TABLE t_facility (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    scenic_area_id BIGINT NOT NULL COMMENT '所属景区 ID',
    building_id BIGINT DEFAULT NULL COMMENT '所属建筑 ID',
    name VARCHAR(100) NOT NULL COMMENT '设施名称',
    type INT NOT NULL COMMENT '设施类型: 0=卫生间 1=餐饮 2=超市 3=停车场 4=售票处 5=游客中心 6=医疗点 7=ATM 8=自动贩卖机 9=摆渡车站 10=自行车租赁',
    subtype VARCHAR(50) DEFAULT NULL COMMENT '子类型（菜系等）',
    latitude DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
    floor_number INT DEFAULT 1 COMMENT '所在楼层',
    description TEXT COMMENT '描述',
    rating DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '评分',
    price_range VARCHAR(20) DEFAULT NULL COMMENT '价格区间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1营业 0关闭',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_facility_scenic_area FOREIGN KEY (scenic_area_id) REFERENCES t_scenic_area(id) ON DELETE CASCADE,
    CONSTRAINT fk_facility_building FOREIGN KEY (building_id) REFERENCES t_building(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设施表';

-- 插入测试设施数据
-- 注意：设施ID要与RoadNode的facility_id关联，以便测试附近设施功能
INSERT INTO t_facility (id, scenic_area_id, building_id, name, type, subtype, latitude, longitude, floor_number, rating, price_range) VALUES
(1, 1, 1, '太和殿卫生间', 0, NULL, 39.9165, 116.3973, 1, 4.50, NULL),
(2, 1, 1, '太和殿茶歇', 1, '中餐', 39.9165, 116.3973, 1, 4.20, '50-100'),
(3, 1, 2, '乾清宫卫生间', 0, NULL, 39.9166, 116.3975, 1, 4.30, NULL),
(4, 1, NULL, '午门游客中心', 5, NULL, 39.9161, 116.3970, 1, 4.80, NULL),
(5, 1, NULL, '神武门便利店', 8, NULL, 39.9169, 116.3974, 1, 4.00, '10-30');

-- ============================================
-- 4. 更新RoadNode的facility_id关联
-- ============================================
-- 为部分RoadNode关联设施（用于测试附近设施功能）
-- 假设 RoadNode 表中有 facility_id 字段
-- 这里假设节点2关联设施1（太和殿卫生间），节点3关联设施2（太和殿茶歇）
-- 注意：需要根据实际RoadNode数据调整

-- 先查看当前RoadNode数据
SELECT id, name, facility_id FROM t_navigation_road_node LIMIT 10;

-- 更新关联（如果RoadNode有facility_id字段）
UPDATE t_navigation_road_node SET facility_id = 1 WHERE id = 2;
UPDATE t_navigation_road_node SET facility_id = 2 WHERE id = 3;
UPDATE t_navigation_road_node SET facility_id = 3 WHERE id = 4;
UPDATE t_navigation_road_node SET facility_id = 4 WHERE id = 5;
UPDATE t_navigation_road_node SET facility_id = 5 WHERE id = 6;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 验证查询
-- ============================================
SELECT '=== 景区表 ===' AS '';
SELECT * FROM t_scenic_area;

SELECT '=== 建筑表 ===' AS '';
SELECT * FROM t_building;

SELECT '=== 设施表 ===' AS '';
SELECT * FROM t_facility;
