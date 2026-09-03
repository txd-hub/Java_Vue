# ============================================
# Spring Boot 项目 Dockerfile
# 用于构建后端项目的 Docker 镜像
# ============================================

# 基础镜像：JDK 17（Alpine 版本，体积小）
FROM eclipse-temurin:17-jre-alpine

# 维护者信息
LABEL maintainer="demo-admin"

# 设置工作目录
WORKDIR /app

# 复制打包好的 jar 文件
# 注意：需要先执行 mvn package 打包
COPY target/demo-admin-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 参数优化（容器环境）
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
