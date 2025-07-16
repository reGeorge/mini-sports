-- Flyway V2: 初始化基础数据

-- 初始化角色数据
INSERT INTO role (name, code, description) VALUES
('系统管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限'),
('普通用户', 'ROLE_USER', '普通用户，基本权限'),
('裁判', 'ROLE_REFEREE', '比赛裁判，可以管理比赛');

-- 初始化权限数据
INSERT INTO permission (name, code, description) VALUES
-- 用户管理权限
('用户查看', 'user:view', '查看用户信息'),
('用户编辑', 'user:edit', '编辑用户信息'),
('用户删除', 'user:delete', '删除用户'),
-- 赛事管理权限
('赛事创建', 'tournament:create', '创建新赛事'),
('赛事编辑', 'tournament:edit', '编辑赛事信息'),
('赛事删除', 'tournament:delete', '删除赛事'),
('赛事查看', 'tournament:view', '查看赛事信息'),
-- 比赛管理权限
('比赛记分', 'match:score', '记录比赛比分'),
('比赛管理', 'match:manage', '管理比赛进程'),
-- 积分管理权限
('积分查看', 'points:view', '查看积分记录'),
('积分管理', 'points:manage', '管理用户积分');

-- 初始化角色-权限关系
-- 管理员角色权限（拥有所有权限）
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_ADMIN';

-- 裁判角色权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_REFEREE'
AND p.code IN ('match:score', 'match:manage', 'tournament:view', 'points:view');

-- 普通用户角色权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_USER'
AND p.code IN ('tournament:view', 'points:view');

-- 初始化系统管理员账号
INSERT INTO user (
    nickname, phone, credential, points, level, status,
    created_at, updated_at
) VALUES (
    'admin',
    '13800138000',
    '$2a$10$RHJPHxqHRF.kJDYFxDxvwuYw3VqJYfd0LHHHNtXNBiYHZFNB0hvDe', -- 密码：admin123
    0,
    'ADMIN',
    1,
    NOW(),
    NOW()
);

-- 为管理员分配角色
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user u, role r
WHERE u.nickname = 'admin'
AND r.code = 'ROLE_ADMIN'; 