# 支付服务 API

Base Path: `/api/payment`

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /pay | 发起支付 | 是 |
| POST | /callback/{channel} | 支付回调(支付宝/微信) | 否(验签) |
| GET | /query/{payNo} | 查询支付状态 | 是 |
| POST | /refund | 发起退款 | 是(管理员) |
| GET | /refund/{refundNo} | 查询退款状态 | 是 |
