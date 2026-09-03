#!/bin/bash
# ============================================
# Spring Boot 应用启动脚本
# 适用于 Linux 服务器部署
# ============================================

# ---------- 配置区域 ----------
JAR_NAME="demo-admin-1.0.0.jar"
JAR_PATH="/opt/${JAR_NAME}"
LOG_DIR="/opt/logs"
LOG_FILE="${LOG_DIR}/demo-admin.log"
PID_FILE="/opt/demo-admin.pid"

# JVM 参数（按服务器内存调整）
JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Spring Boot 启动参数
# 关键：通过 --spring.profiles.active=prod 指定生产环境配置
SPRING_OPTS="--spring.profiles.active=prod"

# ---------- 函数定义 ----------

# 停止旧进程
stop_app() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null 2>&1; then
            echo ">>> 正在停止旧进程 (PID: $PID) ..."
            kill "$PID"
            sleep 3
            # 强制停止（如果还没退出）
            if ps -p "$PID" > /dev/null 2>&1; then
                echo ">>> 旧进程未响应，强制终止 ..."
                kill -9 "$PID"
            fi
            echo ">>> 旧进程已停止"
        else
            echo ">>> PID $PID 对应的进程不存在，清理 PID 文件"
        fi
        rm -f "$PID_FILE"
    fi
}

# 启动应用
start_app() {
    # 检查 JAR 文件是否存在
    if [ ! -f "$JAR_PATH" ]; then
        echo ">>> 错误：JAR 文件不存在 - $JAR_PATH"
        exit 1
    fi

    # 检查 Java 是否可用
    if ! command -v java &> /dev/null; then
        echo ">>> 错误：未找到 java 命令，请确认 Java 已安装并配置环境变量"
        exit 1
    fi

    # 创建日志目录
    mkdir -p "$LOG_DIR"

    echo ">>> 启动应用 ..."
    echo ">>> JAR: $JAR_PATH"
    echo ">>> Profile: prod"
    echo ">>> JVM 参数: $JAVA_OPTS"
    echo ">>> 日志文件: $LOG_FILE"

    # 后台启动，输出重定向到日志文件
    nohup java $JAVA_OPTS -jar "$JAR_PATH" $SPRING_OPTS \
        > "$LOG_FILE" 2>&1 &

    # 记录 PID
    echo $! > "$PID_FILE"
    echo ">>> 应用已启动 (PID: $(cat $PID_FILE))"
    echo ">>> 等待服务就绪 ..."

    # 等待启动完成（检查端口 8080）
    for i in $(seq 1 30); do
        if curl -s -o /dev/null -w '%{http_code}' http://localhost:8080 > /dev/null 2>&1; then
            echo ">>> 服务启动成功！访问地址：http://$(hostname -I | awk '{print $1}'):8080"
            echo ">>> 接口文档：http://$(hostname -I | awk '{print $1}'):8080/doc.html"
            return 0
        fi
        sleep 1
    done

    echo ">>> 警告：服务可能未完全就绪，请查看日志：$LOG_FILE"
    return 1
}

# 查看状态
status_app() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null 2>&1; then
            echo ">>> 应用正在运行 (PID: $PID)"
        else
            echo ">>> 应用未运行（PID 文件存在但进程不存在）"
        fi
    else
        echo ">>> 应用未运行"
    fi
}

# ---------- 主流程 ----------
case "$1" in
    start)
        stop_app
        start_app
        ;;
    stop)
        stop_app
        ;;
    restart)
        stop_app
        start_app
        ;;
    status)
        status_app
        ;;
    *)
        echo "用法: $0 {start|stop|restart|status}"
        echo ""
        echo "  start    - 启动应用（先停止旧进程）"
        echo "  stop     - 停止应用"
        echo "  restart  - 重启应用"
        echo "  status   - 查看运行状态"
        exit 1
        ;;
esac
