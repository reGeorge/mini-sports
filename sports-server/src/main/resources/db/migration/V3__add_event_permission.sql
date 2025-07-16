-- 添加赛事报名权限
INSERT INTO permission (name, code, description, created_at) VALUES 
('赛事报名', 'game:register', '允许报名参加赛事', NOW());

-- 为普通用户角色添加赛事报名权限
INSERT INTO role_permission (role_id, permission_id) 
SELECT 
    (SELECT id FROM role WHERE code = 'ROLE_USER'),
    (SELECT id FROM permission WHERE code = 'game:register');

-- 为管理员角色添加赛事报名权限
INSERT INTO role_permission (role_id, permission_id) 
SELECT 
    (SELECT id FROM role WHERE code = 'ROLE_ADMIN'),
    (SELECT id FROM permission WHERE code = 'game:register'); 