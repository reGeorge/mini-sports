# 创建新文件
-- 初始化角色数据
INSERT INTO role (id, name, code, description) VALUES
(1, '管理员', 'ROLE_ADMIN', '系统管理员'),
(2, '普通用户', 'ROLE_USER', '普通用户'),
(3, '裁判员', 'ROLE_REFEREE', '比赛裁判'),
(4, '赛事管理员', 'ROLE_TOURNAMENT_MANAGER', '负责赛事的创建和管理')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    description = VALUES(description);

-- 初始化权限数据
INSERT INTO permission (name, code, description) 
VALUES
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
('查看赛事', 'tournament:view', '查看赛事信息'),
('创建赛事', 'tournament:create', '创建新赛事'),
('编辑赛事', 'tournament:edit', '编辑赛事信息'),
('删除赛事', 'tournament:delete', '删除赛事'),
('审核赛事', 'tournament:audit', '审核赛事报名'),
('赛事报名', 'tournament:register', '报名参加赛事'),

-- 比赛记录权限
('查看比赛', 'match:view', '查看比赛记录'),
('记录比分', 'match:score', '记录比赛比分'),
('编辑比赛', 'match:edit', '编辑比赛信息'),
('结束比赛', 'match:finish', '结束比赛'),

-- 积分管理权限
('查看积分', 'points:view', '查看积分信息'),
('编辑积分', 'points:edit', '编辑用户积分'),
('积分规则', 'points:rule', '管理积分规则'),

-- 系统管理权限
('系统配置', 'system:config', '管理系统配置'),
('系统日志', 'system:log', '查看系统日志')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    description = VALUES(description);

-- 为角色分配权限前先清除现有的关联
DELETE FROM role_permission WHERE role_id IN (1, 2, 3, 4);

-- 为管理员角色分配所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission;

-- 为普通用户分配基础权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 2, id FROM permission 
WHERE code IN ('tournament:view', 'tournament:register', 'match:view', 'points:view');

-- 为裁判分配相关权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 3, id FROM permission 
WHERE code IN ('tournament:view', 'match:view', 'match:score', 'match:edit', 'match:finish');

-- 为赛事管理员分配相关权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 4, id FROM permission 
WHERE code IN (
    'tournament:view', 
    'tournament:create', 
    'tournament:edit', 
    'tournament:delete', 
    'tournament:audit',
    'match:view',
    'match:edit',
    'match:finish'
); 