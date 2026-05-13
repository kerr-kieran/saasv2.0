# 用户服务 API

Base Path: `/api/user`

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /register | 用户注册 | 否 |
| POST | /login | 用户登录，返回JWT | 否 |
| POST | /logout | 用户登出 | 是 |
| GET | /info | 获取当前用户信息 | 是 |
| PUT | /info | 更新用户信息 | 是 |
| PUT | /password | 修改密码 | 是 |
| POST | /refresh | 刷新token | 是 |
