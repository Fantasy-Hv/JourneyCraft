-- 收藏模块数据库表结构

-- 收藏关系表
CREATE TABLE IF NOT EXISTS `t_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `favorite_type` TINYINT NOT NULL COMMENT '收藏类型：0-景点 1-校园 2-建筑 3-设施 4-日记 5-路线',
    `favorite_id` BIGINT NOT NULL COMMENT '收藏对象 ID',
    `collection_id` BIGINT NOT NULL COMMENT '所属收藏夹 ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_type_target` (`user_id`, `favorite_type`, `favorite_id`) COMMENT '唯一索引：用户不能重复收藏同一对象',
    INDEX `idx_user_id` (`user_id`) COMMENT '用户 ID 索引',
    INDEX `idx_collection_id` (`collection_id`) COMMENT '收藏夹 ID 索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏关系表';

-- 收藏夹表
CREATE TABLE IF NOT EXISTS `t_collection` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏夹 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `collection_name` VARCHAR(20) NOT NULL COMMENT '收藏夹名称',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_name` (`user_id`, `collection_name`) COMMENT '唯一索引：用户收藏夹名称唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户 - 收藏夹关联表';
