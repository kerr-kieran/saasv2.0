# VM 配置

## vmrun 路径

```
E:\-VM\vmrun.exe
```

> 已确认：VMware Workstation Pro 17 安装在 `E:\-VM\`

## 模板 VM

| 属性 | 值 |
|------|-----|
| 模板名称 | saas-template |
| 模板路径 | `C:\Users\白茶\VMware\VMs\saas-template\saas-template.vmx` |
| OS | Ubuntu 22.04 LTS Server |
| 当前 IP | 192.168.10.129 (NAT DHCP) |
| 登录用户 | saas |
| 登录密码 | saas123 |
| SSH Key | `~/.ssh/id_rsa` (已配置) |

> 模板VM已预装：openssh-server + Docker + Docker Compose + VMware Tools。
> Docker 镜像加速使用 DaoCloud: `docker.m.daocloud.io`

## 已部署中间件

| 服务 | 端口 | 状态 |
|------|------|------|
| MySQL 8.0 | 3306 | 运行中 (root/root123) |
| Redis 7 | 6379 | 运行中 (密码: redis123) |
| Nacos 2.2 | 8848 | 运行中 (nacos/nacos) |

## 默认规格

| 参数 | 默认值 | 说明 |
|------|--------|------|
| CPU | 4核 | `numvcpus = "4"` |
| 内存 | 8192 MB | `memsize = "8192"` |
| 磁盘 | 100 GB | 克隆时从模板继承 |
| 网络 | NAT | 默认使用NAT，可改为桥接 |

## VM 存储路径

```
C:\Users\白茶\VMware\VMs\   （新VM默认创建在此）
```

## SSH 配置

- SSH Key 已配置，无需密码: `ssh saas@<IP>`
- 备用密码认证: saas/saas123
- 首次连接: `-o StrictHostKeyChecking=no`
