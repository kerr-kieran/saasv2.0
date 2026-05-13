package com.saas.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.common.Result;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import com.saas.order.dto.CreateOrderDTO;
import com.saas.order.dto.OrderQueryDTO;
import com.saas.order.entity.Order;
import com.saas.order.entity.OrderItem;
import com.saas.order.mapper.OrderItemMapper;
import com.saas.order.mapper.OrderMapper;
import com.saas.order.mq.OrderProducer;
import com.saas.order.service.OrderService;
import com.saas.order.vo.OrderItemVO;
import com.saas.order.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RestTemplate restTemplate;

    @Autowired(required = false)
    private OrderProducer orderProducer;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                            StringRedisTemplate stringRedisTemplate, RestTemplate restTemplate) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO create(Long userId, CreateOrderDTO dto) {
        // 生成订单号: UUID-based, 取前20位
        String orderNo = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String lockKey = "lock:order:" + orderNo;

        // Redis分布式锁，保证幂等
        Boolean lockAcquired = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new BusinessException(ResultCode.CONFLICT);
        }

        try {
            // 1. 扣减库存 — 调用库存服务
            List<Map<String, Object>> deductItems = new ArrayList<>();
            for (CreateOrderDTO.OrderItemDTO item : dto.getItems()) {
                Map<String, Object> deductItem = new HashMap<>();
                deductItem.put("skuId", item.getSkuId());
                deductItem.put("quantity", item.getQuantity());
                deductItems.add(deductItem);
            }

            try {
                Result<?> stockResult = restTemplate.postForObject(
                        "http://saas-inventory-service/api/inventory/deduct",
                        deductItems,
                        Result.class);
                if (stockResult == null || stockResult.getCode() != 200) {
                    throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
                }
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("调用库存服务失败", e);
                throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
            }

            // 2. 计算金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            // 模拟从产品服务获取价格: 这里直接从请求构建, 实际上应调用产品服务
            // 简化处理: 假设商品价格在库存服务返回中携带, 此处用默认价格
            // 实际场景中需要调用 saas-product-service 获取 SKU 价格信息

            // 3. 创建订单
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setStatus(0); // PENDING -> 创建后直接模拟支付成功为PAID
            order.setTotalAmount(totalAmount);
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setPayAmount(totalAmount);
            order.setRemark(dto.getRemark());
            orderMapper.insert(order);

            // 4. 创建订单项
            for (CreateOrderDTO.OrderItemDTO item : dto.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setOrderNo(orderNo);
                orderItem.setSkuId(item.getSkuId());
                orderItem.setQuantity(item.getQuantity());
                // 以下字段实际应从产品服务获取
                orderItem.setSpuId(0L);
                orderItem.setSpuName("");
                orderItem.setPrice(BigDecimal.ZERO);
                orderItem.setTotalAmount(BigDecimal.ZERO);
                orderItemMapper.insert(orderItem);
            }

            // 5. 发送订单状态消息: paid (创建即视为已支付)
if (orderProducer != null) orderProducer.sendPaidMessage(orderNo);

            log.info("订单创建成功, orderNo={}, userId={}", orderNo, userId);
            return buildOrderVO(order, order.getOrderNo());
        } finally {
            // 释放分布式锁
            stringRedisTemplate.delete(lockKey);
        }
    }

    @Override
    public IPage<OrderVO> page(Long userId, OrderQueryDTO query) {
        Page<Order> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(query.getStatus() != null, Order::getStatus, query.getStatus())
                .orderByDesc(Order::getCreateTime);

        IPage<Order> orderPage = orderMapper.selectPage(page, wrapper);

        return orderPage.convert(order -> buildOrderVO(order, order.getOrderNo()));
    }

    @Override
    public OrderVO detail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return buildOrderVO(order, order.getOrderNo());
    }

    @Override
    public OrderVO getByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return buildOrderVO(order, orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        // 只有待支付和已支付的订单可以取消
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException(ResultCode.ORDER_STATUS_INVALID);
        }

        order.setStatus(4); // CANCELLED
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 发送库存释放消息
if (orderProducer != null) orderProducer.sendCancelMessage(order.getOrderNo());

        log.info("订单已取消, orderNo={}", order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String orderNo, Integer status) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            log.warn("订单不存在, orderNo={}", orderNo);
            return;
        }
        order.setStatus(status);
        if (status == 2) {
            order.setShipTime(LocalDateTime.now());
        } else if (status == 3) {
            order.setCompleteTime(LocalDateTime.now());
        } else if (status == 4) {
            order.setCancelTime(LocalDateTime.now());
        }
        orderMapper.updateById(order);
        log.info("订单状态已更新, orderNo={}, status={}", orderNo, status);
    }

    private OrderVO buildOrderVO(Order order, String orderNo) {
        OrderVO vo = new OrderVO();
        BeanUtil.copyProperties(order, vo);

        // 查询订单项
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, orderNo));
        List<OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtil.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);

        return vo;
    }
}
