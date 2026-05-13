# Git 规范

## 分支策略

```
main ←── develop ←── feature/xxx
  │                   ├── feature/user-login
  │                   ├── feature/product-list
  │                   └── feature/order-create
  │
  └── hotfix/xxx (从 main 拉，合回 main + develop)
```

| 分支 | 用途 |
|------|------|
| main | 生产分支，只接受 develop 的 PR |
| develop | 开发主分支 |
| feature/* | 功能分支，从 develop 拉 |
| hotfix/* | 紧急修复，从 main 拉 |

## Commit Message 格式

```
<type>: <简短描述>

<详细说明（可选）>
```

**Type**: feat / fix / refactor / docs / style / test / chore

**示例**:
```
feat: 新增用户登录接口

- 实现JWT token生成与验证
- 添加登录限流
- 集成Redis token缓存
```

## 禁止

- `git push --force` 到 main/develop
- 提交包含密码/token的配置文件
- 提交 node_modules/、target/ 等构建产物
