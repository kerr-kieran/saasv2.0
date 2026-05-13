-- ===================================================
-- Database: saas_user
-- Service: saas-user-service
-- ===================================================
CREATE DATABASE IF NOT EXISTS `saas_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `saas_user`;

CREATE TABLE `saas_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `username` VARCHAR(64) NOT NULL COMMENT 'Username',
  `password` VARCHAR(256) NOT NULL COMMENT 'Password (BCrypt)',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Phone (AES encrypted)',
  `email` VARCHAR(128) DEFAULT NULL COMMENT 'Email',
  `nickname` VARCHAR(64) DEFAULT NULL COMMENT 'Nickname',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT 'Avatar URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 1=active 0=disabled',
  `last_login_time` DATETIME DEFAULT NULL COMMENT 'Last login time',
  `last_login_ip` VARCHAR(64) DEFAULT NULL COMMENT 'Last login IP',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Soft delete: 0=no 1=yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User table';
