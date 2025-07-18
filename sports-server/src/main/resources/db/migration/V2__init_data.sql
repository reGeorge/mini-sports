-- Flyway V2: 初始化基础数据

-- 初始化角色数据
INSERT IGNORE INTO role (name, code, description) VALUES
('系统管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限'),
('普通用户', 'ROLE_USER', '普通用户，基本权限'),
('裁判', 'ROLE_REFEREE', '比赛裁判，可以管理比赛');

-- 初始化权限数据
INSERT IGNORE INTO permission (name, code, description) VALUES
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

-- 确保赛事管理权限齐全
INSERT INTO permission (name, code, description) VALUES
('赛事报名', 'tournament:register', '报名参加赛事')
ON DUPLICATE KEY UPDATE code=code;

-- 初始化角色-权限关系
-- 管理员角色权限（拥有所有权限）
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_ADMIN';

-- 确保管理员角色拥有所有赛事相关权限
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_ADMIN' AND p.code IN (
  'tournament:create', 'tournament:edit', 'tournament:delete', 'tournament:view', 'tournament:register'
);

-- 裁判角色权限
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_REFEREE'
AND p.code IN ('match:score', 'match:manage', 'tournament:view', 'points:view');

-- 普通用户角色权限
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'ROLE_USER'
AND p.code IN ('tournament:view', 'points:view');

-- 初始化系统管理员账号
INSERT IGNORE INTO user (
    nickname, phone, credential, points, level, status,
    created_at, updated_at
) VALUES (
    'admin',
    '13800138000',
    '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', -- 密码：wb12345
    0,
    'ADMIN',
    1,
    NOW(),
    NOW()
);

-- 初始化15个普通用户账号，默认积分1500，密码：wb12345
INSERT IGNORE INTO user (nickname, phone, credential, points, level, status, created_at, updated_at) VALUES
('普通用户1', '13800000001', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户2', '13800000002', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户3', '13800000003', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户4', '13800000004', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户5', '13800000005', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户6', '13800000006', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户7', '13800000007', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户8', '13800000008', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户9', '13800000009', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户10', '13800000010', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户11', '13800000011', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户12', '13800000012', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户13', '13800000013', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户14', '13800000014', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW()),
('普通用户15', '13800000015', '$2a$10$2ADxBJkOuRqs29XS4Qi3uu78Oz8O0rChRgK50DKdy1eT1hT818dl6', 1500, 'BEGINNER', 1, NOW(), NOW());

-- 为普通用户分配普通用户角色
INSERT IGNORE INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM user u, role r WHERE r.code = 'ROLE_USER' AND u.nickname LIKE '普通用户%';

-- 为管理员分配角色
INSERT IGNORE INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user u, role r
WHERE u.nickname = 'admin'
AND r.code = 'ROLE_ADMIN'; 