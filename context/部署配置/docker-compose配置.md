# Docker Compose 配置

## 文件分层

| 文件 | 用途 |
|------|------|
| docker-compose.base.yml | 中间件层 (MySQL/Redis/RocketMQ/Nacos/Nginx) |
| docker-compose.services.yml | 微服务层 (8个服务) |
| docker-compose.yml | 本地开发全栈 (base + services) |
| docker-compose.prod.yml | 生产环境覆盖 |

## 网络

所有服务加入 `saas-net` 网络，通过容器名互相通信。

## 数据卷

| 卷名 | 挂载点 | 用途 |
|------|--------|------|
| mysql-master-data | /var/lib/mysql | MySQL数据 |
| redis-data | /data | Redis持久化 |
| rocketmq-store | /home/rocketmq/store | RocketMQ存储 |

## 环境变量

敏感信息通过 `.env` 文件注入，不提交到Git：
```
MYSQL_ROOT_PASSWORD=xxx
REDIS_PASSWORD=xxx
JWT_SECRET=xxx
```
