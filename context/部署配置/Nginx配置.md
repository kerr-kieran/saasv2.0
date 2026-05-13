# Nginx 配置

## 核心配置

```nginx
upstream gateway {
    server saas-gateway:8080;
}

# 后台管理
server {
    listen 80;
    server_name admin.saas.local;
    root /usr/share/nginx/html/admin;
    index index.html;
    location / { try_files $uri $uri/ /index.html; }
    location /api/ {
        proxy_pass http://gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

# C端商城
server {
    listen 80;
    server_name mall.saas.local;
    root /usr/share/nginx/html/mall;
    index index.html;
    location / { try_files $uri $uri/ /index.html; }
    location /api/ {
        proxy_pass http://gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## SSL 配置（生产）

```nginx
server {
    listen 443 ssl http2;
    server_name admin.saas.com;
    ssl_certificate /etc/nginx/ssl/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
}
```
