# Docker配置排坑记录

## 配置流程

1. **初始配置审查**
   - 检查了`docker-compose.yml`中的三个服务：frontend、backend和database
   - 确认服务之间的依赖关系和网络配置
   - 审查初始数据卷配置

2. **数据库映射调整**
   - 将MySQL端口从3306映射到13306，避免与本地MySQL冲突
   - 修改配置：`"13306:3306"`

3. **数据持久化配置**
   - 配置挂载卷`mysql_data:/var/lib/mysql`
   - 指定本地路径`./mysql-data`作为持久化目录

4. **构建与启动服务**
   - 使用`docker-compose up --build`构建和启动所有服务
   - 检查服务启动状态和日志

## 问题记录

### 1. 数据库初始化脚本不执行

**问题描述**：  
Docker容器启动后，数据库表结构没有创建，数据库是空的。

**原因分析**：  
- 使用了持久化卷挂载到空目录，导致MySQL跳过了初始化脚本的执行
- 当挂载外部目录到`/var/lib/mysql`时，如果目录已存在，MySQL会认为数据库已初始化

**解决方案**：
1. 临时移除volume挂载配置
2. 重新构建并启动容器，让初始化脚本执行
3. 完成初始化后再恢复volume配置

```yaml
# 临时注释掉
# volumes:
#   - mysql_data:/var/lib/mysql
```

### 2. 初始化SQL脚本错误

**问题描述**：  
即使去掉持久化卷，初始化脚本仍然执行失败，容器启动后又自动退出。

**原因分析**：
1. `schema.sql`中的`points_record`表依赖了不存在的`points_rule`表
2. `init_data.sql`第一行使用了`#`注释，MySQL不识别

**解决方案**：
1. 添加缺失的`points_rule`表定义：
```sql
-- 创建积分规则表
CREATE TABLE points_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '规则名称',
    type VARCHAR(50) NOT NULL COMMENT '规则类型',
    points INT NOT NULL COMMENT '积分值',
    description VARCHAR(200) COMMENT '规则描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '积分规则表';
```

2. 修改`init_data.sql`注释：
```sql
-- 创建新文件
-- 初始化角色数据
```

### 3. 数据持久化与初始化冲突

**问题描述**：  
需要在保证数据持久化的同时，确保首次运行时能执行初始化脚本。

**解决方案**：
1. 删除已有的卷和容器：`docker-compose down -v`
2. 移除旧的数据目录：`rm -rf mysql-data && mkdir mysql-data`
3. 恢复持久化配置，启动服务：`docker-compose up -d`

## 最终成果

成功配置了完整的Docker环境，包括：

1. **前端服务(frontend)**
   - 基于Nginx镜像
   - 端口映射：80:80

2. **后端服务(backend)**
   - 基于JDK8镜像
   - 端口映射：8088:8088
   - 环境配置指向Docker内部数据库

3. **数据库服务(database)**
   - 基于MySQL 8.0
   - 端口映射：13306:3306（避免与本地冲突）
   - 数据持久化到`./mysql-data`
   - 成功执行初始化脚本

## 管理命令备忘

```bash
# 启动所有服务
docker-compose up -d

# 重新构建并启动
docker-compose up --build

# 停止所有服务
docker-compose down

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs [服务名]

# 连接数据库
mysql -h127.0.0.1 -P13306 -uroot -p123456 sports_db
```
