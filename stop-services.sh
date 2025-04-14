#!/bin/bash

echo "=== 停止所有服务 ==="

# 停止docker容器
echo "停止Docker容器..."
docker-compose down

# 停止可能在运行的Java进程
echo "检查并停止Java进程..."
JAVA_PID=$(lsof -t -i:8088)
if [ ! -z "$JAVA_PID" ]; then
    echo "发现Java进程 (PID: $JAVA_PID)，正在停止..."
    kill $JAVA_PID
    sleep 2
fi

# 检查端口是否被释放
echo "检查端口状态..."
if lsof -i:8088 > /dev/null; then
    echo "警告: 端口8088仍然被占用，尝试强制结束进程..."
    lsof -t -i:8088 | xargs kill -9
fi

if lsof -i:80 > /dev/null; then
    echo "警告: 端口80仍然被占用，可能需要手动处理..."
fi

echo "=== 服务停止完成 ===" 