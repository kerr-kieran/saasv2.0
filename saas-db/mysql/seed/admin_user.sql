-- Default admin user: admin / admin123
USE `saas_admin`;

INSERT INTO `saas_admin_user` (`username`, `password`, `real_name`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'Administrator', 1);

INSERT INTO `saas_role` (`name`, `code`, `description`) VALUES
('Super Admin', 'ROLE_SUPER_ADMIN', 'All permissions'),
('Admin', 'ROLE_ADMIN', 'Most permissions'),
('Operator', 'ROLE_OPERATOR', 'Basic operations');

INSERT INTO `saas_user_role` (`user_id`, `role_id`) VALUES (1, 1);
