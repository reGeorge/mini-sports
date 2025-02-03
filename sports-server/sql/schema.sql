# 创建新文件
-- 创建用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL,
    avatar_url VARCHAR(255),
    phone VARCHAR(20) NOT NULL,
    credential VARCHAR(255) NOT NULL,
    points INT DEFAULT 0,
    level VARCHAR(20) DEFAULT 'BEGINNER',
    status INT DEFAULT 1,
    id_type VARCHAR(20),
    id_number VARCHAR(50),
    address VARCHAR(255),
    grip_style VARCHAR(50),
    racket_config VARCHAR(100),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 创建角色表
CREATE TABLE role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建权限表
CREATE TABLE permission (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户角色关联表
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- 创建角色权限关联表
CREATE TABLE role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
); 