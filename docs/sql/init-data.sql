-- -------------------------------------------
-- 文件名：init-data.sql
-- 描述：初始化数据
-- 日期：2025-02-04
-- 标签：数据库, 初始数据
-- -------------------------------------------

-- 初始化角色
INSERT INTO `role` (`name`, `code`, `description`, `created_at`, `updated_at`) VALUES
('管理员', 'ROLE_ADMIN', '系统管理员', NOW(), NOW()),
('普通用户', 'ROLE_USER', '普通用户', NOW(), NOW());

-- 初始化权限
INSERT INTO `permission` (`name`, `code`, `description`, `created_at`, `updated_at`) VALUES
-- 赛事权限
('查看赛事', 'tournament:view', '查看赛事信息', NOW(), NOW()),
('创建赛事', 'tournament:create', '创建新赛事', NOW(), NOW()),
('编辑赛事', 'tournament:edit', '编辑赛事信息', NOW(), NOW()),
('删除赛事', 'tournament:delete', '删除赛事', NOW(), NOW()),
('审核报名', 'tournament:audit', '审核赛事报名', NOW(), NOW()),
-- 用户权限
('查看用户', 'user:view', '查看用户信息', NOW(), NOW()),
('编辑用户', 'user:edit', '编辑用户信息', NOW(), NOW()),
('删除用户', 'user:delete', '删除用户', NOW(), NOW());

-- 角色权限关联
INSERT INTO `role_permission` (`role_id`, `permission_id`) 
SELECT r.id, p.id 
FROM role r, permission p 
WHERE r.code = 'ROLE_ADMIN';

INSERT INTO `role_permission` (`role_id`, `permission_id`) 
SELECT r.id, p.id 
FROM role r, permission p 
WHERE r.code = 'ROLE_USER' 
AND p.code IN ('tournament:view'); 