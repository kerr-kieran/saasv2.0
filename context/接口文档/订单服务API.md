# 订单服务 API

Base Path: `/api/order`

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | / | 创建订单 | 是 |
| GET | /page | 用户订单分页列表 | 是 |
| GET | /{id} | 订单详情 | 是 |
| GET | /no/{orderNo} | 按订单号查询 | 是 |
| POST | /{id}/pay | 发起支付 | 是 |
| POST | /{id}/cancel | 取消订单 | 是 |
| GET | /status/{orderNo} | 查询订单状态 | 是 |
