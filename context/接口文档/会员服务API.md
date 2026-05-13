# 会员服务 API

Base Path: `/api/member`

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | /profile | 获取会员信息 | 是 |
| PUT | /profile | 更新会员信息 | 是 |
| GET | /address | 地址列表 | 是 |
| POST | /address | 新增地址 | 是 |
| PUT | /address/{id} | 更新地址 | 是 |
| DELETE | /address/{id} | 删除地址 | 是 |
| PUT | /address/{id}/default | 设为默认地址 | 是 |
| GET | /points | 积分余额 | 是 |
| GET | /points/log/page | 积分流水 | 是 |
