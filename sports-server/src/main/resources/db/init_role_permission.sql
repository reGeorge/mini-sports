-- 清空现有数据
DELETE FROM role_permission;
DELETE FROM user_role;
DELETE FROM permission;
DELETE FROM role;

-- 初始化角色
INSERT INTO role (id, name, description, created_at) VALUES
(1, 'ADMIN', '系统管理员', NOW()),
(2, 'USER', '普通用户', NOW());

-- 初始化权限
INSERT INTO permission (id, name, code, description, created_at) VALUES
-- 用户管理权限
(1, '用户查看', 'user:view', '查看用户列表和详情', NOW()),
(2, '用户编辑', 'user:edit', '编辑用户信息', NOW()),
(3, '用户删除', 'user:delete', '删除用户', NOW()),

-- 角色权限管理
(11, '角色查看', 'role:view', '查看角色列表和详情', NOW()),
(12, '角色编辑', 'role:edit', '编辑角色信息', NOW()),
(13, '角色删除', 'role:delete', '删除角色', NOW()),
(14, '权限分配', 'role:assign', '为角色分配权限', NOW()),

-- 赛事管理权限
(21, '赛事查看', 'game:view', '查看赛事列表和详情', NOW()),
(22, '赛事创建', 'game:create', '创建新赛事', NOW()),
(23, '赛事编辑', 'game:edit', '编辑赛事信息', NOW()),
(24, '赛事删除', 'game:delete', '删除赛事', NOW()),
(25, '赛事审核', 'game:audit', '审核赛事申请', NOW()),

-- 积分管理权限
(31, '积分查看', 'points:view', '查看积分记录', NOW()),
(32, '积分修改', 'points:edit', '修改用户积分', NOW()),
(33, '积分规则设置', 'points:rule', '设置积分规则', NOW()),

-- 系统管理权限
(41, '系统配置', 'system:config', '管理系统配置', NOW()),
(42, '操作日志', 'system:log', '查看操作日志', NOW());

-- 为管理员角色分配所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission;

-- 为普通用户分配基础权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 1),   -- 用户查看
(2, 21),  -- 赛事查看
(2, 31);  -- 积分查看

-- 创建权限码索引
ALTER TABLE permission ADD UNIQUE INDEX idx_permission_code (code); 