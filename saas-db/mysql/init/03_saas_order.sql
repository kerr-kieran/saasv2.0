CREATE DATABASE IF NOT EXISTS `saas_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `saas_order`;

CREATE TABLE `saas_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Order ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT 'Order number',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT 'User ID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0=PENDING 1=PAID 2=SHIPPED 3=COMPLETED 4=CANCELLED',
  `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `discount_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `pay_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `pay_time` DATETIME DEFAULT NULL,
  `pay_channel` VARCHAR(32) DEFAULT NULL COMMENT 'WECHAT/ALIPAY',
  `shipping_name` VARCHAR(64) DEFAULT NULL,
  `shipping_phone` VARCHAR(20) DEFAULT NULL,
  `shipping_address` VARCHAR(512) DEFAULT NULL,
  `tracking_no` VARCHAR(64) DEFAULT NULL,
  `logistics_company` VARCHAR(64) DEFAULT NULL,
  `ship_time` DATETIME DEFAULT NULL,
  `complete_time` DATETIME DEFAULT NULL,
  `cancel_time` DATETIME DEFAULT NULL,
  `cancel_reason` VARCHAR(256) DEFAULT NULL,
  `remark` VARCHAR(512) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id_status` (`user_id`, `status`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order table';

CREATE TABLE `saas_order_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `order_no` VARCHAR(32) NOT NULL,
  `sku_id` BIGINT UNSIGNED NOT NULL,
  `spu_id` BIGINT UNSIGNED NOT NULL,
  `spu_name` VARCHAR(256) NOT NULL,
  `sku_spec` VARCHAR(256) DEFAULT NULL,
  `sku_image` VARCHAR(512) DEFAULT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `total_amount` DECIMAL(12,2) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order item table';
