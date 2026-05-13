# VM 网络配置

## 网络模式

| 模式 | 说明 | 适用场景 |
|------|------|---------|
| NAT | VM 通过宿主机 IP 访问外网，宿主机可访问 VM | 本地开发测试 |
| Bridged | VM 独立 IP，与宿主机同级 | 生产/团队共享 |
| Host-Only | VM 与宿主机私有网络，不对外 | 隔离测试 |

> 默认使用 **NAT** 模式。

## IP 分配

| 方式 | 说明 |
|------|------|
| DHCP | VM 自动获取 IP，通过 `vmrun getGuestIPAddress` 查询 |
| 静态IP | 用户指定 IP，agent 直接使用该 IP 连接 |

## 端口映射

### NAT 模式（宿主机端口 → VM 端口）

| 宿主机端口 | VM 端口 | 服务 |
|-----------|---------|------|
| 5173 | 5173 | Admin 前端 |
| 5174 | 5174 | Mall 前端 |
| 8080 | 8080 | API Gateway |
| 8848 | 8848 | Nacos |
| 3306 | 3306 | MySQL |
| 6379 | 6379 | Redis |

### 桥接模式

VM 直接拥有独立 IP，无需端口映射。防火墙需开放以下端口：
- 80, 443 (Nginx)
- 8080 (Gateway)
- 5173, 5174 (前端 Dev Server)
- 8848 (Nacos)

## 内部网络 (Docker)

VM 内部 Docker 容器通过 `saas-net` bridge 网络互相通信，各服务端口：

| 服务 | 容器端口 |
|------|---------|
| saas-gateway | 8080 |
| saas-user-service | 8081 |
| saas-product-service | 8082 |
| saas-order-service | 8083 |
| saas-inventory-service | 8084 |
| saas-member-service | 8085 |
| saas-payment-service | 8086 |
| saas-admin-service | 8087 |
