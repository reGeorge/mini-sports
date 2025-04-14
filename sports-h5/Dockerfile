# 使用官方Nginx Alpine版本（轻量级）
FROM nginx:1.24-alpine

# 添加作者信息
LABEL maintainer="mini-sports team"

# 删除默认的nginx静态文件
RUN rm -rf /usr/share/nginx/html/*

# 复制构建后的文件到nginx
COPY dist /usr/share/nginx/html

# 复制nginx配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 设置目录权限
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

# 暴露80端口
EXPOSE 80

# 添加健康检查
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget -q --spider http://localhost/ || exit 1

# 启动nginx
CMD ["nginx", "-g", "daemon off;"] 