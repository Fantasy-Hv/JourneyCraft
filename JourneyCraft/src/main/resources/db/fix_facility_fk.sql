-- 修复 RoadNode 外键约束
-- 将 facility_id 外键从 t_temp_facility 改为 t_facility

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 先删除旧的 t_temp_facility 表（如果存在且已迁移完数据）
-- DROP TABLE IF EXISTS t_temp_facility;

-- 2. 查看当前外键名称
-- SHOW CREATE TABLE t_navigation_road_node;

-- 3. 删除旧的外键约束
ALTER TABLE t_navigation_road_node DROP FOREIGN KEY t_navigation_road_node_ibfk_3;

-- 4. 添加新的外键约束指向 t_facility
ALTER TABLE t_navigation_road_node 
    ADD CONSTRAINT t_navigation_road_node_ibfk_3 
    FOREIGN KEY (facility_id) REFERENCES t_facility(id) ON DELETE SET NULL;

SET FOREIGN_KEY_CHECKS = 1;

-- 5. 验证
SHOW CREATE TABLE t_navigation_road_node;
