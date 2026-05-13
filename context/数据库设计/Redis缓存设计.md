# Redis 缓存设计

## Key 命名规范

格式：`{服务缩写}:{业务域}:{标识}`

所有 Key 必须设置 TTL，不允许永久 Key。

## Key 清单

| Key Pattern | 类型 | TTL | 用途 |
|------------|------|-----|------|
| user:token:{token} | String (JSON) | 7d | 用户登录token缓存 |
| user:refresh:{refreshToken} | String | 30d | 刷新token |
| product:detail:{spuId} | String (JSON) | 1h | 商品详情SPU+SKU列表 |
| product:category:tree | String (JSON) | 24h | 全部分类树 |
| product:sku:{skuId} | String (JSON) | 30min | SKU基本信息 |
| order:status:{orderNo} | String | 10min | 订单状态(热点) |
| inventory:stock:{skuId} | String (int) | 5min | 库存数量(热点) |
| lock:inventory:{skuId} | String | 10s | 库存扣减分布式锁 |
| lock:order:{orderNo} | String | 30s | 订单幂等锁 |
| member:cart:{userId} | Hash | 30d | 购物车 skuId→quantity |
| member:profile:{userId} | String (JSON) | 1h | 会员信息 |
| member:points:{userId} | String (int) | 5min | 积分余额 |

## 缓存策略

### 读策略
- **先查缓存 → 缓存未命中查DB → 回写缓存**
- 缓存穿透防护：空值缓存(TTL 1min) + 布隆过滤器(后续)

### 写策略
- **Cache Aside**：先更新DB，再删除缓存
- 或：先更新DB，再异步更新缓存（MQ保证最终一致性）

### 热点Key
- 库存Key(inventory:stock)：本地缓存 + 定时刷新，减少Redis压力
- 分类树(product:category:tree)：本地缓存 1h

## 集群配置

- 模式：Redis Cluster (3主3从)
- 分片算法：CRC16 % 16384
- 连接池：Lettuce (Spring Boot默认)
