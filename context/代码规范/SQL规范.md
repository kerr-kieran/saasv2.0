# SQL 编码规范

## 命名

- 表名: `saas_{模块}_{实体}` (`saas_order`, `saas_order_item`)
- 字段名: snake_case (`order_no`, `create_time`)
- 索引名: `idx_{字段}` (普通索引), `uk_{字段}` (唯一索引)
- 主键: `id`，BIGINT UNSIGNED AUTO_INCREMENT

## 字段规范

- 主键: BIGINT UNSIGNED NOT NULL AUTO_INCREMENT
- 金额: DECIMAL(12,2)
- 状态: TINYINT + COMMENT 枚举值说明
- 时间: DATETIME + DEFAULT CURRENT_TIMESTAMP
- 逻辑删除: `is_deleted` TINYINT DEFAULT 0
- 必备字段: `id, create_time, update_time, is_deleted`
- 字符集: utf8mb4, 排序规则: utf8mb4_unicode_ci

## 索引规范

- 每表不超过 5 个索引
- 联合索引遵循最左前缀
- 高选择性字段优先
- 避免在索引列上使用函数
- 禁止使用外键约束

## 禁止

- `SELECT *`（必须指定字段）
- 存储过程、触发器
- 外键约束（应用层保证）
- 大事务（>5秒）
