-- JourneyCraft history module schema
-- 作用：记录用户浏览历史、搜索历史和导航历史

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS t_navigation_history;
DROP TABLE IF EXISTS t_search_history;
DROP TABLE IF EXISTS t_view_history;

-- ============================================
-- 浏览历史表
-- ============================================
CREATE TABLE t_view_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    target_type TINYINT NOT NULL COMMENT '目标类型：0景点 1校园 2日记 3设施',
    target_id BIGINT NOT NULL COMMENT '目标 ID',
    target_name VARCHAR(100) NOT NULL COMMENT '目标名称',
    view_duration INT DEFAULT 0 COMMENT '浏览时长 (秒)',
    view_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_view_history_user_id (user_id),
    KEY idx_view_history_target_id (target_id),
    KEY idx_view_history_view_time (view_time),
    KEY idx_view_history_user_time (user_id, view_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='浏览历史表';

-- ============================================
-- 搜索历史表
-- ============================================
CREATE TABLE t_search_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    keyword VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    search_type TINYINT NOT NULL COMMENT '搜索类型：0景点 1校园 2日记 3美食',
    result_count INT DEFAULT 0 COMMENT '结果数量',
    clicked_id BIGINT DEFAULT NULL COMMENT '点击的目标 ID',
    searched_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_search_history_user_id (user_id),
    KEY idx_search_history_searched_at (searched_at),
    KEY idx_search_history_user_time (user_id, searched_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='搜索历史表';

-- ============================================
-- 导航历史表
-- ============================================
CREATE TABLE t_navigation_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    scenic_area_id BIGINT NOT NULL COMMENT '景区 ID',
    start_node_id BIGINT NOT NULL COMMENT '起点节点 ID',
    end_node_id BIGINT NOT NULL COMMENT '终点节点 ID',
    path_nodes JSON DEFAULT NULL COMMENT '路径节点序列 (存储节点id，关联导航路线表)',
    transport_mode TINYINT NOT NULL COMMENT '交通方式：1步行 2自行车 3电瓶车 4公共交通',
    actual_time BIGINT DEFAULT 0 COMMENT '实际用时 (秒)',
    is_completed TINYINT NOT NULL DEFAULT 0 COMMENT '是否完成：0否 1是',
    navigated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '导航时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_navigation_history_user_id (user_id),
    KEY idx_navigation_history_scenic_area_id (scenic_area_id),
    KEY idx_navigation_history_navigated_at (navigated_at),
    KEY idx_navigation_history_user_time (user_id, navigated_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='导航历史表';

SET FOREIGN_KEY_CHECKS = 1;
