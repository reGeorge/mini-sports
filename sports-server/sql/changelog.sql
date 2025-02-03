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