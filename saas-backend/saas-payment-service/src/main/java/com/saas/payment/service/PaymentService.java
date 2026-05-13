package com.saas.payment.service;

import com.saas.payment.vo.PaymentVO;

import java.util.Map;

public interface PaymentService {

    /**
     * 发起支付
     */
    PaymentVO pay(Long userId, String orderNo, String channel);

    /**
     * 查询支付记录
     */
    PaymentVO query(String payNo);

    /**
     * 处理支付回调
     */
    String handleCallback(String channel, Map<String, String> params);
}
