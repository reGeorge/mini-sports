--  完整的数据库表结构

-- # 检查 MySQL 服务状态
-- mysql.server status

-- # 停止 MySQL 服务
-- mysql.server stop

-- # 重启 MySQL 服务
-- mysql.server restart


-- 创建用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    credential VARCHAR(255) NOT NULL COMMENT '密码凭证',
    points INT DEFAULT 0 COMMENT '积分',
    level VARCHAR(20) DEFAULT 'BEGINNER' COMMENT '等级',
    status INT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    id_type VARCHAR(20) COMMENT '证件类型',
    id_number VARCHAR(50) COMMENT '证件号码',
    address VARCHAR(255) COMMENT '地址',
    grip_style VARCHAR(50) COMMENT '握拍方式',
    racket_config VARCHAR(100) COMMENT '球拍配置',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间'
) COMMENT '用户表';

-- 创建角色表
CREATE TABLE role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL COMMENT '角色代码',
    description VARCHAR(255) COMMENT '角色描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '角色表';

-- 创建权限表
CREATE TABLE permission (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(50) NOT NULL COMMENT '权限代码',
    description VARCHAR(255) COMMENT '权限描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '权限表';

-- 创建用户角色关联表
CREATE TABLE user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id INT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
) COMMENT '用户角色关联表';

-- 创建角色权限关联表
CREATE TABLE role_permission (
    role_id INT NOT NULL COMMENT '角色ID',
    permission_id INT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
) COMMENT '角色权限关联表';

-- 创建赛事表
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
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (created_by) REFERENCES user(id)
) COMMENT '赛事表';

-- 创建赛事报名表
CREATE TABLE tournament_registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL COMMENT '赛事ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '报名状态：PENDING-待审核,APPROVED-已通过,REJECTED-已拒绝',
    payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付,PAID-已支付,REFUNDED-已退款',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT '赛事报名表';

-- 创建比赛阶段表
CREATE TABLE tournament_stage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL COMMENT '赛事ID',
    name VARCHAR(50) NOT NULL COMMENT '阶段名称',
    type VARCHAR(20) NOT NULL COMMENT '阶段类型：GROUP-小组赛,KNOCKOUT-淘汰赛',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待开始,ONGOING-进行中,FINISHED-已结束',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '结束时间',
    order_num INT NOT NULL DEFAULT 0 COMMENT '阶段顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (tournament_id) REFERENCES tournament(id)
) COMMENT '比赛阶段表';

-- 创建比赛分组表
CREATE TABLE tournament_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL COMMENT '赛事ID',
    stage_id BIGINT NOT NULL COMMENT '阶段ID',
    name VARCHAR(50) NOT NULL COMMENT '分组名称',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    FOREIGN KEY (stage_id) REFERENCES tournament_stage(id)
) COMMENT '比赛分组表';

-- 创建比赛记录表
CREATE TABLE match_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL COMMENT '赛事ID',
    stage_id BIGINT NOT NULL COMMENT '阶段ID',
    group_id BIGINT DEFAULT NULL COMMENT '分组ID',
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
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    FOREIGN KEY (stage_id) REFERENCES tournament_stage(id),
    FOREIGN KEY (group_id) REFERENCES tournament_group(id),
    FOREIGN KEY (player1_id) REFERENCES user(id),
    FOREIGN KEY (player2_id) REFERENCES user(id),
    FOREIGN KEY (winner_id) REFERENCES user(id),
    FOREIGN KEY (referee_id) REFERENCES user(id)
) COMMENT '比赛记录表';

-- 创建积分记录表
CREATE TABLE points_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    rule_id BIGINT NOT NULL COMMENT '规则ID',
    type VARCHAR(50) NOT NULL COMMENT '类型：WIN-胜利,LOSE-失败',
    points INT NOT NULL COMMENT '积分值',
    balance INT NOT NULL COMMENT '积分余额',
    description VARCHAR(200) COMMENT '描述',
    ref_id BIGINT COMMENT '关联ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (rule_id) REFERENCES points_rule(id)
) COMMENT '积分记录表';