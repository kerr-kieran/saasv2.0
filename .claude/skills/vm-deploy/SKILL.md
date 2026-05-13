---
name: vm-deploy
description: VM Deploy — 调用 VMware vmrun CLI 创建/管理虚拟机，一键部署 SaaS 项目到 VM 中。触发词："deploy to VM"、"create VM"、"vm deploy"、"部署到虚拟机"、"一键部署"。
context: project
---

# VM Deploy — VMware 虚拟机部署

你是 "VM Deploy" agent，负责通过 VMware Workstation 的 `vmrun` CLI 创建虚拟机、初始化环境、部署项目。

## 触发方式
- 用户说 "deploy to VM"、"create VM"、"vm deploy"、"部署到虚拟机"、"一键部署"
- 或用户直接提供 IP/主机名要求部署

## 数据来源

### 上下文文件（必读）
1. `context/部署配置/VM配置.md` — vmrun路径、模板VM路径、默认规格、SSH凭据
2. `context/部署配置/VM网络配置.md` — 网络模式、IP分配、端口映射
3. `context/部署配置/docker-compose配置.md` — 服务编排
4. `context/环境配置/生产环境.md` — 生产规格

### 项目文件
- `saas-deploy/docker/docker-compose/docker-compose.base.yml` — 中间件编排
- `saas-deploy/docker/docker-compose/docker-compose.services.yml` — 服务编排
- `saas-db/` — 数据库初始化脚本

### 外部工具
- **vmrun**: VMware Workstation CLI
- **ssh / scp**: 远程连接和文件传输
- **sshpass**: 非交互式SSH密码输入（可选）

## 工作流程

### Step 1: 读取配置 + 收集用户输入
读取 VM配置.md 和 VM网络配置.md，了解模板VM路径、默认规格和网络设置。

如果用户未提供完整信息，询问：
> VM Deploy ready. 请确认：
> 1. VM名称：{新VM的名字}
> 2. IP地址：{目标IP}（留空则从 DHCP 自动获取）
> 3. 规格：CPU核心数/内存GB/磁盘GB（默认 4C8G 100G）
> 4. 使用模板：{模板VM名称}

### Step 2: 创建或复用 VM

**A. 从模板克隆新VM**
```bash
# 克隆
vmrun clone "{template.vmx}" "{new_vm_path}/{name}.vmx" full -cloneName="{name}"

# 设置CPU和内存（需要先转换为小写或找到正确参数）
# 通过修改 vmx 文件来调整规格
echo 'numvcpus = "4"' >> "{new_vm_path}/{name}.vmx"
echo 'memsize = "8192"' >> "{new_vm_path}/{name}.vmx"

# 无GUI启动
vmrun start "{new_vm_path}/{name}.vmx" nogui
```

**B. 使用已有 VM**
```bash
# 列出所有VM
vmrun list
# 启动
vmrun start "{existing_vm_path}/{name}.vmx" nogui
```

### Step 3: 等待 VM 就绪

```bash
# 轮询获取IP（最多等60秒）
for i in $(seq 1 12); do
  IP=$(vmrun getGuestIPAddress "{vm_path}/{name}.vmx" 2>/dev/null)
  if [ -n "$IP" ]; then
    echo "VM IP: $IP"
    break
  fi
  sleep 5
done

# 也可以使用用户指定的静态IP
# 检查SSH是否就绪
for i in $(seq 1 30); do
  if nc -z ${TARGET_IP} 22 2>/dev/null; then
    echo "SSH ready"
    break
  fi
  sleep 2
done
```

### Step 4: 初始化 VM 环境

SSH 连接到 VM 并安装 Docker：

```bash
# 方式1：使用 SSH key
ssh -o StrictHostKeyChecking=no root@${TARGET_IP} "bash -s" << 'EOF'
  # 安装 Docker
  curl -fsSL https://get.docker.com | sh
  systemctl enable docker --now
  
  # 安装 Docker Compose
  curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
    -o /usr/local/bin/docker-compose
  chmod +x /usr/local/bin/docker-compose
  
  # 创建部署目录
  mkdir -p /opt/saas/{db,deploy}
  
  # 配置防火墙（如果开启）
  firewall-cmd --permanent --add-port=80/tcp 2>/dev/null
  firewall-cmd --permanent --add-port=8080/tcp 2>/dev/null
  firewall-cmd --reload 2>/dev/null || true
EOF

# 方式2：使用 sshpass（如果配置了密码）
# sshpass -p "${SSH_PASSWORD}" ssh -o StrictHostKeyChecking=no root@${TARGET_IP} ...
```

### Step 5: 上传项目文件

```bash
# 上传部署配置
scp -o StrictHostKeyChecking=no \
  saas-deploy/docker/docker-compose/docker-compose.base.yml \
  saas-deploy/docker/docker-compose/docker-compose.services.yml \
  root@${TARGET_IP}:/opt/saas/deploy/

# 上传数据库脚本
scp -o StrictHostKeyChecking=no -r \
  saas-db/mysql \
  root@${TARGET_IP}:/opt/saas/db/
```

### Step 6: 部署服务

```bash
# Step 6a: 先启动中间件
ssh -o StrictHostKeyChecking=no root@${TARGET_IP} \
  "cd /opt/saas/deploy && docker-compose -f docker-compose.base.yml up -d"

# Step 6b: 等待中间件就绪
echo "Waiting for MySQL..."
ssh root@${TARGET_IP} \
  "for i in $(seq 1 30); do docker exec saas-mysql-master mysqladmin ping -h localhost --silent 2>/dev/null && break; sleep 2; done"
echo "Waiting for Redis..."
ssh root@${TARGET_IP} \
  "for i in $(seq 1 10); do docker exec saas-redis redis-cli ping 2>/dev/null | grep -q PONG && break; sleep 1; done"
echo "Waiting for Nacos..."
sleep 15

# Step 6c: 启动业务服务（构建前端 + 后端镜像并启动）
ssh -o StrictHostKeyChecking=no root@${TARGET_IP} \
  "cd /opt/saas/deploy && docker-compose -f docker-compose.services.yml up -d --build"

# Step 6d: 检查服务状态
ssh root@${TARGET_IP} "docker-compose -f /opt/saas/deploy/docker-compose.services.yml ps"
```

### Step 7: 输出结果

```markdown
# VM 部署完成

## VM 信息
- VM名称：{name}
- IP地址：{ip}
- 规格：{CPU}核 / {RAM}MB / {DISK}GB

## 访问地址
| 服务 | 地址 |
|------|------|
| Admin 后台 | http://{ip}:5173 |
| C端商城 | http://{ip}:5174 |
| API网关 | http://{ip}:8080 |
| Nacos控制台 | http://{ip}:8848/nacos (nacos/nacos) |

## 常用命令
```bash
# SSH 登录
ssh root@{ip}

# 查看服务状态
docker-compose -f /opt/saas/deploy/docker-compose.yml ps

# 查看日志
docker-compose -f /opt/saas/deploy/docker-compose.yml logs -f

# 重启服务
docker-compose -f /opt/saas/deploy/docker-compose.yml restart
```

## 管理VM
```bash
# 停止VM
vmrun stop "{vm_path}/{name}.vmx" soft

# 挂起VM
vmrun suspend "{vm_path}/{name}.vmx" soft

# 启动VM
vmrun start "{vm_path}/{name}.vmx" nogui
```
```

### Step 8: 存档
保存部署日志到 `context/开发日志/YYYY-MM-DD/运维日志.md`。

## 错误处理

### vmrun 未找到
```bash
# 添加到 PATH
export PATH="$PATH:C:/Program Files (x86)/VMware/VMware Workstation"
```

### SSH 连接超时
- 检查 VM 是否安装 openssh-server
- 检查防火墙是否开放 22 端口
- 尝试使用 `vmrun runProgramInGuest` 作为备选方案安装 SSH

### Docker 启动失败
- 检查 VM 是否支持虚拟化嵌套
- `docker info` 查看详细错误
- CentOS 7 需要 `yum install -y yum-utils` 以及添加 Docker 源

## 注意事项
- vmrun 路径需要根据实际安装位置调整
- 首次部署时间约 5-10 分钟（克隆VM + 安装Docker + 拉取镜像）
- 生产环境密码必须通过环境变量传入，不要硬编码在脚本中
- VM 模板建议安装好 VMware Tools 和 openssh-server
- 如果是 NAT 网络模式，需要配置端口转发才能从外部访问
