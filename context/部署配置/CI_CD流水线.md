# CI/CD 流水线

## Jenkins Pipeline 阶段

```
Checkout → Build Backend → Build Frontend → Test → Docker Build → Push → Deploy
```

## 触发条件

| 分支 | 触发 | 部署环境 |
|------|------|---------|
| dev | Push | 开发环境(自动) |
| main | PR merged | 测试环境(手动审批) |
| main | Tag v* | 生产环境(手动审批) |

## 环境变量

```
DOCKER_REGISTRY=registry.example.com/saas
SONAR_HOST=http://sonarqube:9000
```

## 通知

- 构建失败：飞书/钉钉 Webhook 通知
- 部署成功：发送变更日志到群聊
