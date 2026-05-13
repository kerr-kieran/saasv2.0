package com.saas.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.common.Result;
import com.saas.order.dto.CreateOrderDTO;
import com.saas.order.dto.OrderQueryDTO;
import com.saas.order.service.OrderService;
import com.saas.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<OrderVO> create(@RequestHeader("X-User-Id") Long userId,
                                  @Valid @RequestBody CreateOrderDTO dto) {
        log.info("创建订单, userId={}, dto={}", userId, dto);
        OrderVO orderVO = orderService.create(userId, dto);
        return Result.ok(orderVO);
    }

    /**
     * 分页查询订单
     */
    @GetMapping("/page")
    public Result<IPage<OrderVO>> page(@RequestHeader("X-User-Id") Long userId,
                                        OrderQueryDTO query) {
        IPage<OrderVO> page = orderService.page(userId, query);
        return Result.ok(page);
    }

    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@RequestHeader("X-User-Id") Long userId,
                                  @PathVariable("id") Long orderId) {
        OrderVO orderVO = orderService.detail(orderId);
        return Result.ok(orderVO);
    }

    /**
     * 根据订单号查询
     */
    @GetMapping("/no/{orderNo}")
    public Result<OrderVO> getByOrderNo(@PathVariable("orderNo") String orderNo) {
        OrderVO orderVO = orderService.getByOrderNo(orderNo);
        return Result.ok(orderVO);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable("id") Long orderId) {
        orderService.cancel(userId, orderId);
        return Result.ok();
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/status/{orderNo}")
    public Result<OrderVO> status(@PathVariable("orderNo") String orderNo) {
        OrderVO orderVO = orderService.getByOrderNo(orderNo);
        return Result.ok(orderVO);
    }
}
