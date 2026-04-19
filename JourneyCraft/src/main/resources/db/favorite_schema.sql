-- ============================================
-- 收藏模块数据库表结构
-- 创建时间: 2026-04-19
-- 说明: 包含收藏关系表和收藏夹表
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS journeycraft DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE journeycraft;

-- ============================================
-- 1. 收藏夹表 (t_collection)
-- 说明: 存储用户的收藏夹信息，每个用户有一个默认收藏夹
-- ============================================
DROP TABLE IF EXISTS `t_collection`;
CREATE TABLE `t_collection` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏夹ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `collection_name` VARCHAR(20) NOT NULL COMMENT '收藏夹名称',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_collection` (`user_id`, `collection_name`) COMMENT '用户ID和收藏夹名称联合唯一索引',
    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引，用于快速查询用户的收藏夹列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏夹表';

-- ============================================
-- 2. 收藏关系表 (t_favorite)
-- 说明: 存储用户收藏的对象信息，支持多种类型的收藏
-- ============================================
DROP TABLE IF EXISTS `t_favorite`;
CREATE TABLE `t_favorite` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `favorite_type` TINYINT(1) NOT NULL COMMENT '收藏类型: 0-景点, 1-校园, 2-建筑, 3-设施, 4-日记, 5-路线',
    `favorite_id` BIGINT(20) NOT NULL COMMENT '收藏对象ID',
    `collection_id` BIGINT(20) NOT NULL COMMENT '所属收藏夹ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_type_favorite` (`user_id`, `favorite_type`, `favorite_id`) COMMENT '防止重复收藏的联合唯一索引',
    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引，用于查询用户的收藏列表',
    KEY `idx_collection_id` (`collection_id`) COMMENT '收藏夹ID索引，用于查询指定收藏夹的收藏项',
    KEY `idx_favorite_type` (`favorite_type`) COMMENT '收藏类型索引，用于按类型过滤',
    KEY `idx_created_at` (`created_at`) COMMENT '创建时间索引，用于按时间排序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏关系表';

-- ============================================
-- 初始化数据：为现有用户创建默认收藏夹
-- 注意：实际使用时需要通过应用层逻辑为新注册用户自动创建默认收藏夹
-- ============================================

-- 示例：为已存在的用户创建默认收藏夹（可选，根据实际情况执行）
-- INSERT INTO `t_collection` (`user_id`, `collection_name`, `created_at`)
-- SELECT DISTINCT `user_id`, '默认收藏夹', NOW()
-- FROM `t_user`
-- WHERE `user_id` NOT IN (
--     SELECT `user_id` FROM `t_collection` WHERE `collection_name` = '默认收藏夹'
-- );

-- ============================================
-- 索引说明
-- ============================================
-- t_collection 表索引:
--   - uk_user_collection: 确保同一用户不能有同名收藏夹
--   - idx_user_id: 加速查询用户的所有收藏夹
--
-- t_favorite 表索引:
--   - uk_user_type_favorite: 防止用户对同一对象重复收藏
--   - idx_user_id: 加速查询用户的所有收藏
--   - idx_collection_id: 加速查询指定收藏夹的收藏项
--   - idx_favorite_type: 加速按类型过滤收藏
--   - idx_created_at: 加速按时间排序（最新收藏优先）

-- ============================================
-- 外键约束说明
-- ============================================
-- 考虑到性能和灵活性，这里不使用物理外键约束，而是通过应用层保证数据一致性：
-- 1. collection_id 必须在 t_collection 表中存在
-- 2. user_id 必须在 t_user 表中存在
-- 3. 删除收藏夹时，需要先将该收藏夹下的收藏移动到默认收藏夹
-- 4. 删除收藏对象时，需要级联删除相关的收藏记录

-- ============================================
-- 性能优化建议
-- ============================================
-- 1. 对于高频查询的场景，可以考虑添加 Redis 缓存：
--    - 用户的收藏夹列表
--    - 用户的热门收藏
--
-- 2. 定期清理无效收藏：
--    - 已被删除的景点/日记等对象的收藏记录
--    - 长期未访问的收藏（可选）
--
-- 3. 分表策略（当数据量超过千万级时考虑）：
--    - 按 user_id 进行哈希分表
--    - 或按 created_at 进行时间范围分表
