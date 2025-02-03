# 创建新文件
-- 版本变更记录

-- V1.0.0 (2024-01-20)
-- 初始版本
-- 1. 创建基础表结构
-- 2. 初始化角色和权限数据

-- V1.0.1 (2024-01-25)
-- 用户表增强
ALTER TABLE user ADD COLUMN grip_style VARCHAR(50) COMMENT '握拍方式';
ALTER TABLE user ADD COLUMN racket_config VARCHAR(100) COMMENT '球拍配置';

-- V1.0.2 (2024-01-30)
-- 角色增强
ALTER TABLE role ADD COLUMN code VARCHAR(50) COMMENT '角色代码';
UPDATE role SET 
    name = '管理员',
    code = 'ROLE_ADMIN',
    description = '系统管理员'
WHERE id = 1;
UPDATE role SET 
    name = '普通用户',
    code = 'ROLE_USER',
    description = '普通用户'
WHERE id = 2;

-- V1.0.3 (2024-02-01)
-- 赛事功能
-- 1. 创建赛事相关表
CREATE TABLE tournament (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL COMMENT '赛事标题',
    description TEXT COMMENT '赛事描述',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    location VARCHAR(255) COMMENT '比赛地点',
    max_players INT NOT NULL COMMENT '最大参与人数',
    current_players INT DEFAULT 0 COMMENT '当前参与人数',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '赛事状态：DRAFT-草稿,REGISTERING-报名中,ONGOING-进行中,FINISHED-已结束',
    type VARCHAR(20) NOT NULL COMMENT '比赛类型：SINGLES-单打,DOUBLES-双打,TEAM-团体',
    level VARCHAR(20) COMMENT '赛事级别',
    entry_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '报名费用',
    created_by BIGINT NOT NULL COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE tournament_registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL COMMENT '赛事ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '报名状态：PENDING-待审核,APPROVED-已通过,REJECTED-已拒绝',
    payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付,PAID-已支付,REFUNDED-已退款',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE match_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL COMMENT '赛事ID',
    player1_id BIGINT NOT NULL COMMENT '选手1ID',
    player2_id BIGINT NOT NULL COMMENT '选手2ID',
    player1_score INT DEFAULT 0 COMMENT '选手1得分',
    player2_score INT DEFAULT 0 COMMENT '选手2得分',
    winner_id BIGINT COMMENT '获胜者ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '比赛状态：PENDING-待开始,ONGOING-进行中,FINISHED-已结束',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    round INT COMMENT '比赛轮次',
    court_number VARCHAR(20) COMMENT '场地编号',
    referee_id BIGINT COMMENT '裁判ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    FOREIGN KEY (player1_id) REFERENCES user(id),
    FOREIGN KEY (player2_id) REFERENCES user(id),
    FOREIGN KEY (winner_id) REFERENCES user(id),
    FOREIGN KEY (referee_id) REFERENCES user(id)
);

-- 2. 添加裁判角色
INSERT INTO role (name, code, description) VALUES ('裁判员', 'ROLE_REFEREE', '比赛裁判');

-- 3. 添加赛事相关权限
INSERT INTO permission (name, code, description) VALUES
('查看赛事', 'tournament:view', '查看赛事信息'),
('创建赛事', 'tournament:create', '创建新赛事'),
('编辑赛事', 'tournament:edit', '编辑赛事信息'),
('删除赛事', 'tournament:delete', '删除赛事'),
('审核赛事', 'tournament:audit', '审核赛事报名'),
('赛事报名', 'tournament:register', '报名参加赛事'),
('查看比赛', 'match:view', '查看比赛记录'),
('记录比分', 'match:score', '记录比赛比分'),
('编辑比赛', 'match:edit', '编辑比赛信息'),
('结束比赛', 'match:finish', '结束比赛'); 