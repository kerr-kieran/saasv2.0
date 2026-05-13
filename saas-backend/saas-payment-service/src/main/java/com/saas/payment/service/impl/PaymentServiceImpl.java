package com.saas.payment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import com.saas.payment.entity.Payment;
import com.saas.payment.mapper.PaymentMapper;
import com.saas.payment.service.PaymentService;
import com.saas.payment.vo.PaymentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO pay(Long userId, String orderNo, String channel) {
        // 检查是否已有成功的支付记录
        Payment existPayment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getOrderNo, orderNo)
                        .eq(Payment::getStatus, 1));
        if (existPayment != null) {
            log.warn("订单已支付, orderNo={}", orderNo);
            throw new BusinessException(ResultCode.CONFLICT);
        }

        // 生成支付单号
        String payNo = UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        // 模拟支付金额 (实际应从订单服务查询)
        BigDecimal amount = new BigDecimal("0.01");

        // 创建支付记录
        Payment payment = new Payment();
        payment.setPayNo(payNo);
        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setChannel(channel);
        payment.setStatus(1); // 模拟支付成功
        payment.setPayTime(LocalDateTime.now());
        payment.setThirdPayNo("SIMULATED_" + System.currentTimeMillis());
        paymentMapper.insert(payment);

        // 发送支付成功消息
        rocketMQTemplate.convertAndSend("ORDER_STATUS:paid", orderNo);
        log.info("支付成功, payNo={}, orderNo={}, channel={}", payNo, orderNo, channel);

        PaymentVO vo = new PaymentVO();
        BeanUtil.copyProperties(payment, vo);
        return vo;
    }

    @Override
    public PaymentVO query(String payNo) {
        Payment payment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>().eq(Payment::getPayNo, payNo));
        if (payment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        PaymentVO vo = new PaymentVO();
        BeanUtil.copyProperties(payment, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleCallback(String channel, Map<String, String> params) {
        // 从回调参数中提取支付单号和订单号
        String payNo = params.get("out_trade_no");
        String orderNo = params.get("order_no");
        String thirdPayNo = params.get("transaction_id");
        String status = params.get("status");

        log.info("收到支付回调, channel={}, payNo={}, orderNo={}, status={}", channel, payNo, orderNo, status);

        Payment payment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>().eq(Payment::getPayNo, payNo));
        if (payment == null) {
            log.error("支付记录不存在, payNo={}", payNo);
            return "fail";
        }

        // 验证签名 (placeholder)
        // if (!verifySignature(channel, params)) {
        //     log.error("签名验证失败, payNo={}", payNo);
        //     return "fail";
        // }

        // 更新支付状态
        if ("SUCCESS".equals(status)) {
            if (payment.getStatus() == 1) {
                log.info("支付记录已处理, payNo={}", payNo);
                return "success";
            }
            payment.setStatus(1);
            payment.setPayTime(LocalDateTime.now());
            payment.setCallbackTime(LocalDateTime.now());
            payment.setThirdPayNo(thirdPayNo);
            paymentMapper.updateById(payment);

            // 发送支付成功消息
            rocketMQTemplate.convertAndSend("ORDER_STATUS:paid", payment.getOrderNo());
        } else {
            payment.setStatus(2); // failed
            payment.setCallbackTime(LocalDateTime.now());
            paymentMapper.updateById(payment);
        }

        return "success";
    }
}
