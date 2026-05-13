# SaaS E-Commerce Platform (saasv2.0)

多租户 SaaS 电商平台。

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend Admin | Vue 3 + Element Plus + Vite |
| Frontend Mall | Vue 3 + Vite |
| Backend | Java 17 + Spring Boot + Spring Cloud |
| Database | MySQL 8.0 (主从) |
| Cache | Redis 7 (集群) |
| Message Queue | RocketMQ 5.1 |
| Registry/Config | Nacos 2.2 |
| Gateway | Spring Cloud Gateway |
| Deployment | Docker + Jenkins + Nginx |

## Quick Start

### Prerequisites
- JDK 17
- Maven 3.9+
- Node.js 18 LTS
- Docker Desktop

### Start Middleware
```bash
docker-compose -f saas-deploy/docker/docker-compose/docker-compose.base.yml up -d
```

### Start Backend
```bash
cd saas-backend
mvn clean install -DskipTests
# Start services individually or use docker-compose
```

### Start Frontend (Admin)
```bash
cd saas-frontend-admin
npm install
npm run dev
```

### Start Frontend (Mall)
```bash
cd saas-frontend-mall
npm install
npm run dev
```

## Project Structure

```
saasv2.0/
├── saas-frontend-admin/   # PC 后台管理系统
├── saas-frontend-mall/    # PC C 端商城
├── saas-backend/          # Java 微服务
├── saas-db/               # 数据库脚本
├── saas-deploy/           # 部署配置
├── context/               # 设计文档 & Agent 上下文
└── docs/                  # 项目文档
```

## Subagents

8 个 Claude Code agent 辅助开发：architect / backend-dev / frontend-dev / database-design / devops / tester / code-reviewer / debugger
