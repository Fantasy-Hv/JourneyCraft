CREATE DATABASE `journeycraft` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

       -- journeycraft.t_user definition

CREATE TABLE `t_user` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                          `username` varchar(50) NOT NULL COMMENT '用户名',
                          `password` varchar(128) NOT NULL COMMENT '密码',
                          `nickname` varchar(50) NOT NULL COMMENT '昵称',
                          `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
                          `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
                          `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                          `preferences` json DEFAULT NULL COMMENT '偏好设置',
                          `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
                          `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                          `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_user_username` (`username`),
                          UNIQUE KEY `uk_user_phone` (`phone`),
                          UNIQUE KEY `uk_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
