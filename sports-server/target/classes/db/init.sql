-- 创建数据库
CREATE DATABASE IF NOT EXISTS sports_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE sports_db;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    nickname VARCHAR(50) COMMENT '用户昵称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    credential VARCHAR(100) COMMENT '密码',
    points INT DEFAULT 0 COMMENT '积分',
    level VARCHAR(20) DEFAULT 'BEGINNER' COMMENT '技术等级',
    status TINYINT DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户认证表
CREATE TABLE IF NOT EXISTS user_auth (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    identity_type VARCHAR(20) NOT NULL COMMENT '认证类型',
    identifier VARCHAR(100) NOT NULL COMMENT '认证标识',
    credential VARCHAR(100) COMMENT '认证凭证',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_type_identifier (identity_type, identifier)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户认证表'; 


-- 角色表
CREATE TABLE role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE permission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色权限关联表
CREATE TABLE role_permission (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
);

-- 用户角色关联表
CREATE TABLE user_role (
    user_id BIGINT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);