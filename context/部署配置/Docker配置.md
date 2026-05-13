# Docker 配置

## 镜像命名规范

```
{registry}/saas/{service}:{version}
```

例: `registry.example.com/saas/gateway:1.0.0`

## Dockerfile 模板

### Java 服务
```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
```

### 前端
```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx-default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

## 最佳实践
- 使用多阶段构建减小镜像体积
- 使用非root用户运行容器
- 所有镜像配置 HEALTHCHECK
- 日志输出到 stdout/stderr（不要在容器内写日志文件）
