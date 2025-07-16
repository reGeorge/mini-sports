#!/bin/bash

set -e

echo "=== 一键部署：mini-sports 项目 ==="

# 1. 构建前端
cd sports-h5
if [ -f package.json ]; then
  echo "[1/4] 构建前端..."
  npm install && npm run build
else
  echo "未找到 sports-h5/package.json，跳过前端构建"
fi
cd ..

# 2. 构建后端
cd sports-server
if [ -f pom.xml ]; then
  echo "[2/4] 构建后端..."
  mvn clean package -DskipTests
else
  echo "未找到 sports-server/pom.xml，跳过后端构建"
fi
cd ..

# 3. 启动 Docker 服务
if [ -f docker-compose.yml ]; then
  echo "[3/4] 启动 Docker 服务..."
  docker-compose up -d --build
else
  echo "未找到 docker-compose.yml，无法启动 Docker 服务"
  exit 1
fi

# 4. 检查服务状态
sleep 5
echo "[4/4] 当前容器状态："
docker-compose ps

echo "=== 部署完成！==="
echo "如需停止服务，可执行 ./stop-services.sh" 