# Vue 编码规范

## 项目结构

```
src/
├── api/           # API 请求封装
├── components/    # 组件
├── views/         # 页面
├── router/        # 路由
├── store/         # Pinia 状态管理
├── utils/         # 工具函数
├── directives/    # 自定义指令
└── assets/        # 静态资源
```

## 命名

- 组件文件: PascalCase (`ProductList.vue`)
- 页面文件: camelCase 或 kebab-case
- 变量/函数: camelCase
- CSS class: kebab-case

## 核心规则

- 使用 Composition API (`<script setup>`)
- 状态管理用 Pinia
- CSS 使用 `scoped`
- API 调用通过 `api/` 层
- 组件 Props 定义类型和默认值
- 列表必须提供 `:key`
- `v-for` 和 `v-if` 不要同时使用
- 避免直接操作 DOM，使用 ref
