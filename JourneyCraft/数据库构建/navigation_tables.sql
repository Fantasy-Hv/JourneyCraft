-- =============================================
-- JourneyCraft Navigation 模块 - 数据库表定义
-- 生成日期: 2026-04-30
-- 生成方式: 读取 MySQL journeycraft 数据库实际结构
-- 负责人: 刘方正
-- 包含 8 张表: road_node, road_edge, route,
--   photo_spot, indoor_floor,
--   route_cache, osm_import_log, history
-- =============================================

-- ----------------------------------------------------
-- Table: t_navigation_road_node
-- ----------------------------------------------------
DROP TABLE IF EXISTS `t_navigation_road_node`;
CREATE TABLE `t_navigation_road_node` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '节点ID(自增主键)',
  `osm_id` bigint NOT NULL COMMENT 'OSM节点ID',
  `osm_tags` json DEFAULT NULL COMMENT 'OSM标签(JSON格式，存储原始标签)',
  `scenic_area_id` bigint DEFAULT NULL COMMENT '所属景区ID',
  `building_id` bigint DEFAULT NULL COMMENT '所属建筑ID',
  `facility_id` bigint DEFAULT NULL COMMENT '关联设施ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点名称',
  `node_type` tinyint NOT NULL DEFAULT '5' COMMENT '节点类型: 0=入口,1=路口,2=POI,3=设施入口,4=拍照点,5=OSM普通节点',
  `latitude` decimal(10,8) NOT NULL COMMENT '纬度',
  `longitude` decimal(11,8) NOT NULL COMMENT '经度',
  `floor_number` int DEFAULT '1' COMMENT '楼层(室内导航用,1=地面层)',
  `is_important` tinyint DEFAULT '0' COMMENT '是否重要节点: 0=否,1=是(用于路径规划优化)',
  `is_accessible` tinyint DEFAULT '1' COMMENT '是否可通行: 0=禁用,1=启用',
  `is_enabled` tinyint DEFAULT '1' COMMENT '是否启用: 0=禁用,1=启用',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除: 0=否,1=是',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_osm_id` (`osm_id`),
  KEY `idx_scenic_area` (`scenic_area_id`),
  KEY `idx_building` (`building_id`),
  KEY `idx_facility` (`facility_id`),
  KEY `idx_location` (`latitude`,`longitude`),
  KEY `idx_node_type` (`node_type`),
  KEY `idx_important` (`is_important`),
  KEY `idx_status` (`is_enabled`),
  CONSTRAINT `t_navigation_road_node_ibfk_1` FOREIGN KEY (`scenic_area_id`) REFERENCES `t_scenic_area` (`id`) ON DELETE SET NULL,
  CONSTRAINT `t_navigation_road_node_ibfk_2` FOREIGN KEY (`building_id`) REFERENCES `t_building` (`id`) ON DELETE SET NULL,
  CONSTRAINT `t_navigation_road_node_ibfk_3` FOREIGN KEY (`facility_id`) REFERENCES `t_facility` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路网节点表(基于OSM节点，关联scenic模块实体)'
;
-- ----------------------------------------------------
-- Table: t_navigation_road_edge
-- ----------------------------------------------------
DROP TABLE IF EXISTS `t_navigation_road_edge`;
CREATE TABLE `t_navigation_road_edge` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '路径ID',
  `osm_way_id` bigint NOT NULL COMMENT 'OSM路径ID',
  `osm_tags` json DEFAULT NULL COMMENT 'OSM标签(JSON格式)',
  `from_node_id` bigint NOT NULL COMMENT '起始节点ID',
  `to_node_id` bigint NOT NULL COMMENT '目标节点ID',
  `scenic_area_id` bigint DEFAULT NULL COMMENT '所属景区ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路径名称',
  `distance` decimal(10,2) NOT NULL COMMENT '几何距离(米)',
  `adjusted_distance` decimal(10,2) DEFAULT NULL COMMENT '调整后距离(考虑坡度、障碍等)',
  `walk_time` int DEFAULT NULL COMMENT '步行时间',
  `bike_time` int DEFAULT NULL COMMENT '自行车时间',
  `shuttle_time` int DEFAULT NULL COMMENT '电瓶车时间',
  `transport_type` tinyint NOT NULL DEFAULT '0' COMMENT '通行方式: 0=未知,1=仅步行,2=仅自行车,3=仅车辆,4=步行+自行车,5=全部',
  `is_bidirectional` tinyint DEFAULT '1' COMMENT '是否双向通行: 0=否,1=是',
  `is_covered` tinyint DEFAULT '0' COMMENT '是否有遮挡: 0=无,1=有',
  `highway_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'OSM道路类型(highway标签值)',
  `surface` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路面类型',
  `incline` decimal(5,2) DEFAULT NULL COMMENT '坡度(%)',
  `base_congestion` decimal(3,2) DEFAULT '0.00' COMMENT '基础拥挤度(0-1)',
  `current_congestion` decimal(3,2) DEFAULT '0.00' COMMENT '实时拥挤度(0-1)',
  `is_enabled` tinyint DEFAULT '1' COMMENT '是否启用: 0=禁用,1=启用',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除: 0=否,1=是',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_edge_direction` (`from_node_id`,`to_node_id`),
  KEY `idx_osm_way` (`osm_way_id`),
  KEY `idx_from_node` (`from_node_id`),
  KEY `idx_to_node` (`to_node_id`),
  KEY `idx_transport` (`transport_type`),
  KEY `idx_highway` (`highway_type`),
  KEY `idx_status` (`is_enabled`),
  KEY `idx_scenic_area_id` (`scenic_area_id`),
  KEY `idx_edge_scenic_area` (`scenic_area_id`),
  CONSTRAINT `t_navigation_road_edge_ibfk_1` FOREIGN KEY (`from_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_navigation_road_edge_ibfk_2` FOREIGN KEY (`to_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路径段表(支持多种交通方式，存储实时拥挤度)'
;
-- ----------------------------------------------------
-- Table: t_navigation_route
-- ----------------------------------------------------
DROP TABLE IF EXISTS `t_navigation_route`;
CREATE TABLE `t_navigation_route` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '路线ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID(可为空，匿名规划)',
  `scenic_area_id` bigint NOT NULL COMMENT '景区ID',
  `start_node_id` bigint NOT NULL COMMENT '起点节点ID',
  `end_node_ids` json DEFAULT NULL COMMENT '终点列表(多目标规划)',
  `path_nodes` json NOT NULL COMMENT '路径节点序列',
  `total_distance` decimal(10,2) NOT NULL COMMENT '总距离(米)',
  `estimated_time` int NOT NULL COMMENT '预计时间(秒)',
  `transport_modes` json DEFAULT NULL COMMENT '交通方式组合',
  `strategy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规划策略(shortest_distance/shortest_time/avoid_crowd/scenic_route)',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_scenic_area` (`scenic_area_id`),
  KEY `idx_created` (`created_at`),
  KEY `start_node_id` (`start_node_id`),
  CONSTRAINT `t_navigation_route_ibfk_1` FOREIGN KEY (`scenic_area_id`) REFERENCES `t_scenic_area` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_navigation_route_ibfk_2` FOREIGN KEY (`start_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导航路线表(用户规划路线)'
;
-- ----------------------------------------------------
-- Table: t_navigation_photo_spot
-- ----------------------------------------------------
DROP TABLE IF EXISTS `t_navigation_photo_spot`;
CREATE TABLE `t_navigation_photo_spot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '拍照点ID',
  `scenic_area_id` bigint NOT NULL COMMENT '所属景区ID',
  `node_id` bigint DEFAULT NULL COMMENT '最佳拍摄节点ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '拍照点名称',
  `target_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拍摄目标(如"太和殿")',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '拍摄描述',
  `recommended_angle` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐角度(如"北向45度")',
  `best_time` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最佳时间(如"日落前1小时")',
  `best_season` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最佳季节',
  `sample_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '示例图片URL',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '评分(1-5)',
  `check_in_count` int DEFAULT '0' COMMENT '打卡次数',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '推荐拍摄位置纬度',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '推荐拍摄位置经度',
  `is_enabled` tinyint DEFAULT '1' COMMENT '是否启用: 0=禁用,1=启用',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除: 0=否,1=是',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_scenic_area` (`scenic_area_id`),
  KEY `idx_node` (`node_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_location` (`latitude`,`longitude`),
  KEY `idx_status` (`is_enabled`),
  CONSTRAINT `t_navigation_photo_spot_ibfk_1` FOREIGN KEY (`scenic_area_id`) REFERENCES `t_scenic_area` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_navigation_photo_spot_ibfk_2` FOREIGN KEY (`node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拍照点推荐表'
;
-- ----------------------------------------------------
-- Table: t_navigation_indoor_floor
-- ----------------------------------------------------
DROP TABLE IF EXISTS `t_navigation_indoor_floor`;
CREATE TABLE `t_navigation_indoor_floor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '楼层ID',
  `building_id` bigint NOT NULL COMMENT '建筑ID',
  `floor_number` int NOT NULL COMMENT '楼层号(-1=地下室,0=地面,1=一层...)',
  `floor_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '楼层名称(如"一层大厅")',
  `map_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '楼层地图URL',
  `map_dimensions` json DEFAULT NULL COMMENT '地图尺寸{width:100,height:100,scale:0.5}',
  `indoor_data` json DEFAULT NULL COMMENT '室内数据(房间、走廊等)',
  `elevator_node_id` bigint DEFAULT NULL COMMENT '电梯节点ID',
  `stair_node_id` bigint DEFAULT NULL COMMENT '楼梯节点ID',
  `entrance_node_ids` json DEFAULT NULL COMMENT '入口节点ID列表[JSON数组]',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除: 0=否,1=是',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_building_floor` (`building_id`,`floor_number`),
  KEY `idx_building` (`building_id`),
  KEY `idx_floor_number` (`floor_number`),
  KEY `elevator_node_id` (`elevator_node_id`),
  KEY `stair_node_id` (`stair_node_id`),
  CONSTRAINT `t_navigation_indoor_floor_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `t_building` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_navigation_indoor_floor_ibfk_2` FOREIGN KEY (`elevator_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE SET NULL,
  CONSTRAINT `t_navigation_indoor_floor_ibfk_3` FOREIGN KEY (`stair_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='室内楼层表'
;
-- ----------------------------------------------------
-- scenic 模块与导航主节点的外键绑定
-- 说明：scenic_schema.sql 需先执行，再执行本脚本
-- ----------------------------------------------------
ALTER TABLE `t_scenic_area`
  ADD CONSTRAINT `fk_scenic_area_node`
  FOREIGN KEY (`node_id`) REFERENCES `t_navigation_road_node` (`id`)
  ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `t_building`
  ADD CONSTRAINT `fk_building_node`
  FOREIGN KEY (`node_id`) REFERENCES `t_navigation_road_node` (`id`)
  ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `t_facility`
  ADD CONSTRAINT `fk_facility_node`
  FOREIGN KEY (`node_id`) REFERENCES `t_navigation_road_node` (`id`)
  ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `t_crowd_level`
  ADD CONSTRAINT `fk_crowd_level_node`
  FOREIGN KEY (`node_id`) REFERENCES `t_navigation_road_node` (`id`)
  ON DELETE CASCADE ON UPDATE CASCADE;
-- ----------------------------------------------------
-- Table: t_navigation_route_cache
-- ----------------------------------------------------
DROP TABLE IF EXISTS `t_navigation_route_cache`;
CREATE TABLE `t_navigation_route_cache` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '缓存ID',
  `start_node_id` bigint NOT NULL COMMENT '起始节点ID',
  `end_node_id` bigint NOT NULL COMMENT '目标节点ID',
  `transport_type` tinyint NOT NULL COMMENT '通行方式',
  `strategy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规划策略(shortest_distance/shortest_time/avoid_crowd)',
  `route_nodes` json NOT NULL COMMENT '路径节点ID列表[JSON数组]',
  `total_distance` decimal(10,2) NOT NULL COMMENT '总距离(米)',
  `total_time` int NOT NULL COMMENT '总时间(秒)',
  `calculated_at` datetime NOT NULL COMMENT '计算时间',
  `expires_at` datetime NOT NULL COMMENT '过期时间',
  `hit_count` int DEFAULT '0' COMMENT '命中次数',
  `calculation_time_ms` int DEFAULT NULL COMMENT '计算耗时(毫秒)',
  `node_count` int DEFAULT NULL COMMENT '路径节点数',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除: 0=否,1=是',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_query` (`start_node_id`,`end_node_id`,`transport_type`,`strategy`),
  KEY `idx_expires` (`expires_at`),
  KEY `idx_hit_count` (`hit_count`),
  KEY `idx_calculated` (`calculated_at`),
  KEY `end_node_id` (`end_node_id`),
  CONSTRAINT `t_navigation_route_cache_ibfk_1` FOREIGN KEY (`start_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_navigation_route_cache_ibfk_2` FOREIGN KEY (`end_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路径规划缓存表'
;
-- ----------------------------------------------------
-- Table: t_navigation_osm_import_log
-- ----------------------------------------------------
DROP TABLE IF EXISTS `t_navigation_osm_import_log`;
CREATE TABLE `t_navigation_osm_import_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `region` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区域(如"北京西城区")',
  `osm_extent` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据范围[min_lon,min_lat,max_lon,max_lat]',
  `import_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '导入类型: node, way, relation',
  `total_records` int DEFAULT '0' COMMENT '总记录数',
  `success_count` int DEFAULT '0' COMMENT '成功数',
  `failed_count` int DEFAULT '0' COMMENT '失败数',
  `nodes_imported` int DEFAULT '0' COMMENT '导入节点数',
  `edges_imported` int DEFAULT '0' COMMENT '导入路径段数',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'processing' COMMENT '状态: processing, completed, failed',
  `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `started_at` datetime NOT NULL COMMENT '开始时间',
  `finished_at` datetime DEFAULT NULL COMMENT '结束时间',
  `processing_time_ms` int DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除: 0=否,1=是',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_file` (`file_name`),
  KEY `idx_status` (`status`),
  KEY `idx_region` (`region`),
  KEY `idx_started` (`started_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OSM数据导入日志表'
;
-- ----------------------------------------------------
-- Table: t_navigation_history
-- ----------------------------------------------------
DROP TABLE IF EXISTS `t_navigation_history`;
CREATE TABLE `t_navigation_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `scenic_area_id` bigint NOT NULL COMMENT '景区ID',
  `start_node_id` bigint NOT NULL COMMENT '起点节点ID',
  `end_node_id` bigint NOT NULL COMMENT '终点节点ID',
  `path_nodes` json DEFAULT NULL COMMENT '路径节点序列',
  `transport_mode` tinyint DEFAULT NULL COMMENT '交通方式: 1=步行, 2=自行车, 3=电瓶车',
  `actual_time` int DEFAULT NULL COMMENT '实际用时(秒)',
  `is_completed` tinyint DEFAULT '1' COMMENT '是否完成: 0=未完成, 1=已完成',
  `navigated_at` datetime NOT NULL COMMENT '导航时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_scenic_area` (`scenic_area_id`),
  KEY `idx_navigated` (`navigated_at`),
  KEY `start_node_id` (`start_node_id`),
  KEY `end_node_id` (`end_node_id`),
  CONSTRAINT `t_navigation_history_ibfk_1` FOREIGN KEY (`scenic_area_id`) REFERENCES `t_temp_scenic_area` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_navigation_history_ibfk_2` FOREIGN KEY (`start_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_navigation_history_ibfk_3` FOREIGN KEY (`end_node_id`) REFERENCES `t_navigation_road_node` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导航历史表'
;
