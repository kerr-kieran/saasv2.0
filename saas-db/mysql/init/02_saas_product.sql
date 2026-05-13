-- ===================================================
-- Database: saas_product
-- Service: saas-product-service
-- ===================================================
CREATE DATABASE IF NOT EXISTS `saas_product` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `saas_product`;

CREATE TABLE `saas_category` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
  `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Parent category ID',
  `name` VARCHAR(64) NOT NULL COMMENT 'Category name',
  `level` TINYINT NOT NULL DEFAULT 1 COMMENT 'Level: 1/2/3',
  `sort` INT NOT NULL DEFAULT 0 COMMENT 'Sort order',
  `icon` VARCHAR(512) DEFAULT NULL COMMENT 'Icon URL',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Product category';

CREATE TABLE `saas_spu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SPU ID',
  `spu_no` VARCHAR(32) NOT NULL COMMENT 'Product code',
  `name` VARCHAR(256) NOT NULL COMMENT 'Product name',
  `category_id` BIGINT UNSIGNED NOT NULL COMMENT 'Category ID',
  `brand` VARCHAR(128) DEFAULT NULL COMMENT 'Brand',
  `description` TEXT COMMENT 'Description',
  `main_image` VARCHAR(512) DEFAULT NULL COMMENT 'Main image URL',
  `images` JSON DEFAULT NULL COMMENT 'Product images',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1=on 0=off',
  `min_price` DECIMAL(10,2) DEFAULT NULL,
  `max_price` DECIMAL(10,2) DEFAULT NULL,
  `sales` INT NOT NULL DEFAULT 0 COMMENT 'Sales count',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_spu_no` (`spu_no`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  FULLTEXT KEY `ft_name_desc` (`name`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Product SPU';

CREATE TABLE `saas_sku` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `sku_no` VARCHAR(32) NOT NULL COMMENT 'SKU code',
  `spu_id` BIGINT UNSIGNED NOT NULL COMMENT 'SPU ID',
  `spec_name` VARCHAR(256) DEFAULT NULL COMMENT 'Spec name',
  `spec_data` JSON DEFAULT NULL COMMENT 'Spec JSON',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Price',
  `cost_price` DECIMAL(10,2) DEFAULT NULL COMMENT 'Cost price',
  `image` VARCHAR(512) DEFAULT NULL COMMENT 'SKU image',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1=active 0=inactive',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_no` (`sku_no`),
  KEY `idx_spu_id` (`spu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Product SKU';
