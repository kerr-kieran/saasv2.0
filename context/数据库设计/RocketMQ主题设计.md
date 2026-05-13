# RocketMQ 主题设计

## 消息格式

所有消息体使用JSON，必须包含以下公共字段：

```json
{
  "messageId": "uuid",
  "timestamp": 1700000000000,
  "version": "1.0",
  "traceId": "trace-xxx",
  "data": { }
}
```

## Topic 清单

### ORDER_STATUS — 订单状态变更

| 属性 | 值 |
|------|-----|
| 生产者 | saas-order-service |
| 消费者 | saas-inventory-service, saas-member-service |
| 消息类型 | 普通消息 |

| Tag | 说明 | data内容 |
|-----|------|---------|
| paid | 已支付 | `{orderNo, userId, skuList, payAmount}` |
| shipped | 已发货 | `{orderNo, trackingNo, logisticsCompany}` |
| cancel | 已取消 | `{orderNo, userId, skuList, cancelReason}` |

### INVENTORY_DEDUCT — 库存扣减

| 属性 | 值 |
|------|-----|
| 生产者 | saas-order-service |
| 消费者 | saas-inventory-service |
| 消息类型 | 事务消息（半消息） |

| Tag | 说明 | data内容 |
|-----|------|---------|
| - | 库存扣减 | `{orderNo, items: [{skuId, quantity}]}` |

### INVENTORY_RELEASE — 库存释放

| 属性 | 值 |
|------|-----|
| 生产者 | saas-order-service |
| 消费者 | saas-inventory-service |
| 消息类型 | 普通消息 |

| Tag | 说明 | data内容 |
|-----|------|---------|
| - | 库存释放（取消订单/退款） | `{orderNo, reason, items: [{skuId, quantity}]}` |

### MEMBER_POINTS — 积分变更

| 属性 | 值 |
|------|-----|
| 生产者 | saas-order-service |
| 消费者 | saas-member-service |
| 消息类型 | 普通消息 |

| Tag | 说明 | data内容 |
|-----|------|---------|
| order_pay | 购物积分发放 | `{userId, orderNo, payAmount, points}` |
| order_refund | 退款积分扣回 | `{userId, orderNo, refundAmount, points}` |

## Consumer Group

| Consumer Group | 消费 Topic | 说明 |
|---------------|-----------|------|
| inventory-consumer | ORDER_STATUS:paid, ORDER_STATUS:cancel | 库存服务 |
| member-consumer | ORDER_STATUS:paid, MEMBER_POINTS | 会员服务 |
| inventory-deduct-consumer | INVENTORY_DEDUCT | 库存扣减消费者 |
| inventory-release-consumer | INVENTORY_RELEASE | 库存释放消费者 |

## 重试与DLQ

- 最大重试次数：16次
- 重试间隔：10s, 30s, 1m, 2m, 3m, 4m, 5m, 6m, 7m, 8m, 9m, 10m, 20m, 30m, 1h, 2h
- 死信队列（DLQ）：`%DLQ%{ConsumerGroup}`
- DLQ消息保留7天，人工处理后删除
