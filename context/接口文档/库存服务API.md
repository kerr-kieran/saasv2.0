# 库存服务 API

Base Path: `/api/inventory`

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | /{skuId} | 查询SKU库存 | 是(内部) |
| POST | /batch | 批量查询库存 | 是(内部) |
| POST | /deduct | 扣减库存(内部调用/MQ) | 是(内部) |
| POST | /release | 释放库存(内部调用/MQ) | 是(内部) |
| GET | /log/page | 库存流水查询(后台) | 是(管理员) |
