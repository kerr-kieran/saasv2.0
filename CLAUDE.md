# CLAUDE.md — SaaS E-Commerce Platform (saasv2.0)

## Project Overview
SaaS 多租户电商平台。前端 Vue 3 + Element Plus（后台管理）+ Vue 3（C端商城），后端 Java SpringCloud 微服务，MySQL + Redis + RocketMQ 数据层。

## Subagents (9)

| Agent | Skill | Trigger Words |
|-------|-------|---------------|
| architect | `.claude/skills/architect/SKILL.md` | "design architecture", "microservice split", "tech stack", "architecture review" |
| backend-dev | `.claude/skills/backend-dev/SKILL.md` | "write API", "implement service", "SpringBoot", "@RestController" |
| frontend-dev | `.claude/skills/frontend-dev/SKILL.md` | "write page", "Vue component", "Element Plus", "frontend" |
| database-design | `.claude/skills/database-design/SKILL.md` | "create table", "DDL", "cache design", "SQL optimization", "index" |
| devops | `.claude/skills/devops/SKILL.md` | "deploy", "Docker", "Jenkins", "CI/CD", "nginx", "docker-compose" |
| tester | `.claude/skills/tester/SKILL.md` | "write test", "unit test", "integration test", "test case", "run tests" |
| code-reviewer | `.claude/skills/code-reviewer/SKILL.md` | "code review", "review", "check code" |
| debugger | `.claude/skills/debugger/SKILL.md` | "error", "bug", "debug", "排查", "启动失败" |
| vm-deploy | `.claude/skills/vm-deploy/SKILL.md` | "deploy to VM", "create VM", "vm deploy", "部署到虚拟机", "一键部署" |

## Context Rules
- 所有设计文档、规范、配置放在 `context/` 下
- 每个 agent 必须先读自己的 context 文件再生效
- 开发日志输出到 `context/开发日志/YYYY-MM-DD/`
- 业务文档用中文，代码标识符用英文

## Language
- 用户交互：中文
- 代码标识符：英文
- Commit message：中文

## Key Commands
- 后端编译：`mvn clean package -DskipTests`
- 后端单模块：`mvn clean compile -pl saas-xxx-service`
- 前端启动：`npm run dev` (in saas-frontend-admin or saas-frontend-mall)
- Docker 中间件：`docker-compose -f saas-deploy/docker/docker-compose/docker-compose.base.yml up -d`
- Docker 全部服务：`docker-compose -f saas-deploy/docker/docker-compose/docker-compose.yml up -d`
