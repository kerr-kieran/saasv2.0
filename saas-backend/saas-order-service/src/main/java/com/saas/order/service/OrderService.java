package com.saas.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.order.dto.CreateOrderDTO;
import com.saas.order.dto.OrderQueryDTO;
import com.saas.order.vo.OrderVO;

public interface OrderService {

    /**
     * 创建订单
     */
    OrderVO create(Long userId, CreateOrderDTO dto);

    /**
     * 分页查询用户订单
     */
    IPage<OrderVO> page(Long userId, OrderQueryDTO query);

    /**
     * 订单详情
     */
    OrderVO detail(Long orderId);

    /**
     * 根据订单号查询
     */
    OrderVO getByOrderNo(String orderNo);

    /**
     * 取消订单
     */
    void cancel(Long userId, Long orderId);

    /**
     * 更新订单状态 (MQ消费者调用)
     */
    void updateStatus(String orderNo, Integer status);
}
