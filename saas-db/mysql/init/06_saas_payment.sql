CREATE DATABASE IF NOT EXISTS `saas_payment` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `saas_payment`;

CREATE TABLE `saas_payment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `pay_no` VARCHAR(32) NOT NULL COMMENT 'Payment number',
  `order_no` VARCHAR(32) NOT NULL COMMENT 'Order number',
  `user_id` BIGINT UNSIGNED NOT NULL,
  `amount` DECIMAL(12,2) NOT NULL,
  `channel` VARCHAR(32) NOT NULL COMMENT 'WECHAT/ALIPAY/BANK',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0=pending 1=success 2=failed 3=refunded',
  `third_pay_no` VARCHAR(64) DEFAULT NULL COMMENT 'Third-party payment number',
  `pay_time` DATETIME DEFAULT NULL,
  `callback_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_no` (`pay_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Payment table';

CREATE TABLE `saas_refund` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `refund_no` VARCHAR(32) NOT NULL,
  `pay_no` VARCHAR(32) NOT NULL,
  `order_no` VARCHAR(32) NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `amount` DECIMAL(12,2) NOT NULL,
  `reason` VARCHAR(256) DEFAULT NULL,
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0=pending 1=success 2=failed',
  `refund_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Refund table';
