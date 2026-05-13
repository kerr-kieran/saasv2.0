# ER 图说明

## 核心实体

```
┌──────────┐     ┌──────────────┐     ┌──────────┐
│  Category │◄────│     SPU      │────►│   SKU    │
│  (分类)   │     │  (标准商品)   │     │ (规格商品)│
└──────────┘     └──────┬───────┘     └────┬─────┘
                        │                  │
                        │            ┌─────▼──────┐
                        │            │  Inventory │
                        │            │   (库存)    │
                        │            └────────────┘
                        │
┌──────────┐     ┌──────▼───────┐     ┌──────────┐
│   User   │────►│    Order     │◄────│  Address │
│  (用户)  │     │   (订单)     │     │  (地址)  │
└────┬─────┘     └──────┬──────┘     └──────────┘
     │                  │
     │           ┌──────▼──────┐     ┌──────────┐
     │           │ OrderItem   │     │ Payment  │
     │           │ (订单明细)   │     │  (支付)   │
     │           └─────────────┘     └──────────┘
     │
┌────▼─────┐     ┌──────────┐     ┌──────────┐
│  Member  │────►│PointsLog │     │  Role    │
│  (会员)  │     │(积分流水) │     │  (角色)  │
└──────────┘     └──────────┘     └────┬─────┘
                                       │
                                  ┌────▼──────┐
                                  │Permission │
                                  │  (权限)   │
                                  └───────────┘
```

## 关系说明

| 关系 | 类型 | 说明 |
|------|------|------|
| Category → SPU | 1:N | 一个分类下有多个商品 |
| SPU → SKU | 1:N | 一个SPU有多个规格SKU |
| SKU → Inventory | 1:1 | 每个SKU对应一个库存记录 |
| User → Order | 1:N | 一个用户有多个订单 |
| Order → OrderItem | 1:N | 一个订单有多个订单明细 |
| Order → Payment | 1:1 | 一个订单对应一条支付记录 |
| User → Member | 1:1 | 每个用户对应一个会员档案 |
| Member → PointsLog | 1:N | 会员有多个积分变动记录 |
| User → Address | 1:N | 用户有多个收货地址 |
| User → Role | N:N | 用户-角色多对多 |
| Role → Permission | N:N | 角色-权限多对多 |

## 数据库分库

| 数据库名 | 归属服务 | 核心表 |
|----------|---------|--------|
| saas_user | saas-user-service | saas_user, saas_user_token |
| saas_product | saas-product-service | saas_category, saas_spu, saas_sku |
| saas_order | saas-order-service | saas_order, saas_order_item |
| saas_inventory | saas-inventory-service | saas_inventory, saas_warehouse, saas_stock_log |
| saas_member | saas-member-service | saas_member, saas_address, saas_points_log |
| saas_payment | saas-payment-service | saas_payment, saas_refund |
| saas_admin | saas-admin-service | saas_user, saas_role, saas_permission, saas_user_role, saas_role_permission |
