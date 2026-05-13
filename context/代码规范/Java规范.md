# Java 编码规范

基于阿里巴巴 Java 开发手册。

## 命名

- 类名: PascalCase (`OrderService`)
- 方法/变量: camelCase (`createOrder`, `orderNo`)
- 常量: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- 包名: 小写 (`com.saas.order`)

## 项目结构

```
com.saas.{service}/
├── controller/     # REST 控制器
├── service/        # 业务接口
│   └── impl/       # 业务实现
├── mapper/         # MyBatis Mapper
├── entity/         # 数据库实体
├── dto/            # 数据传输对象
├── vo/             # 视图对象
├── config/         # 配置类
└── mq/             # 消息生产/消费
```

## 核心规则

- 禁止在 Controller 中写业务逻辑
- Service 层方法不超过 80 行
- 金额使用 `BigDecimal`
- 时间使用 `LocalDateTime`
- 使用 `@Transactional` 控制事务
- 使用 `@Valid` 做参数校验
- 异常分 BusinessException / SystemException
- 日志使用 `@Slf4j` + log.info/warn/error
- 魔法值使用常量或枚举
