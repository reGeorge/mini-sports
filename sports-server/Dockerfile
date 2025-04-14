# 使用Maven构建应用
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app

# 配置Maven使用阿里云镜像源
COPY settings.xml /usr/share/maven/conf/settings.xml

COPY pom.xml .
# 先下载依赖，利用Docker缓存
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# 使用JDK8镜像
FROM openjdk:8-jre-slim

WORKDIR /app

# 安装wget
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8088

# 添加健康检查 - 增加间隔和超时时间
HEALTHCHECK --interval=60s --timeout=10s --start-period=120s \
    CMD wget -q -O /dev/null http://localhost:8088/actuator/health || exit 1

# 启动应用时增加JVM参数，加快启动速度
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:MaxRAMFraction=1", "-jar", "app.jar"] 