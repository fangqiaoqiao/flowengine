# 使用官方 Eclipse Temurin JRE 8 镜像作为基础镜像
FROM eclipse-temurin:8-jre

# 设置时区为中国时区（解决 Oracle 连接时的时区问题）
# 1. 安装 tzdata 包（如果镜像中没有）
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y tzdata

# 2. 设置环境变量 TZ 为中国时区
ENV TZ=Asia/Shanghai

# 3. 创建时区软链接（使系统时区生效）
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /workspace

# 将构建好的 JAR 文件复制到镜像中
COPY flowengine-1.0.jar flowengine-1.0.jar

# 暴露应用默认端口
EXPOSE 10085

# 运行应用
ENTRYPOINT ["java", "-jar", "flowengine-1.0.jar"]