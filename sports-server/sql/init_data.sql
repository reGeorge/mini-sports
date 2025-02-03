# 创建新文件
-- 初始化角色数据
INSERT INTO role (id, name, code, description) VALUES
(1, '管理员', 'ROLE_ADMIN', '系统管理员'),
(2, '普通用户', 'ROLE_USER', '普通用户');

-- 初始化权限数据
INSERT INTO permission (name, code, description) VALUES
-- 用户管理权限
('查看用户', 'user:view', '查看用户信息'),
('编辑用户', 'user:edit', '编辑用户信息'),
('删除用户', 'user:delete', '删除用户'),

-- 角色权限管理
('查看角色', 'role:view', '查看角色信息'),
('编辑角色', 'role:edit', '编辑角色信息'),
('删除角色', 'role:delete', '删除角色'),
('分配角色', 'role:assign', '为用户分配角色'),

-- 赛事管理权限
('查看赛事', 'game:view', '查看赛事信息'),
('创建赛事', 'game:create', '创建新赛事'),
('编辑赛事', 'game:edit', '编辑赛事信息'),
('删除赛事', 'game:delete', '删除赛事'),
('审核赛事', 'game:audit', '审核赛事报名'),

-- 积分管理权限
('查看积分', 'points:view', '查看积分信息'),
('编辑积分', 'points:edit', '编辑用户积分'),
('积分规则', 'points:rule', '管理积分规则'),

-- 系统管理权限
('系统配置', 'system:config', '管理系统配置'),
('系统日志', 'system:log', '查看系统日志');

-- 为管理员角色分配所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission; 