# API 测试规范

## 工具

- 手动: Postman / Thunder Client
- 自动化: Postman Collection + Newman
- CI 集成: `newman run collection.json`

## 测试集合

| Collection | 用途 |
|-----------|------|
| User API | 注册/登录/信息 CRUD |
| Product API | 商品查询/搜索/分类 |
| Order API | 下单/支付/取消/查询 |
| Inventory API | 库存查询/扣减/释放 |
| Member API | 会员/地址/积分 |
| Payment API | 支付/回调/退款 |

## 测试用例模板

每个接口至少覆盖:
- 正常请求 200
- 参数校验 400
- 未登录 401
- 无权限 403
- 资源不存在 404
