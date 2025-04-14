FROM mysql:8.0

# 设置环境变量
ENV MYSQL_DATABASE=sports_db
ENV MYSQL_ROOT_PASSWORD=123456
ENV TZ=Asia/Shanghai

# 复制初始化SQL脚本
COPY initSQL/schema.sql /docker-entrypoint-initdb.d/01-schema.sql
COPY initSQL/init_data.sql /docker-entrypoint-initdb.d/02-init-data.sql

# 复制自定义配置
COPY my.cnf /etc/mysql/conf.d/

# 设置数据库编码
CMD ["mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"] 