package com.saas.payment.controller;

import com.saas.common.Result;
import com.saas.payment.service.PaymentService;
import com.saas.payment.vo.PaymentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 发起支付
     */
    @PostMapping("/pay")
    public Result<PaymentVO> pay(@RequestHeader("X-User-Id") Long userId,
                                  @RequestBody Map<String, String> body) {
        String orderNo = body.get("orderNo");
        String channel = body.get("channel");
        PaymentVO paymentVO = paymentService.pay(userId, orderNo, channel);
        return Result.ok(paymentVO);
    }

    /**
     * 查询支付记录
     */
    @GetMapping("/query/{payNo}")
    public Result<PaymentVO> query(@PathVariable("payNo") String payNo) {
        PaymentVO paymentVO = paymentService.query(payNo);
        return Result.ok(paymentVO);
    }

    /**
     * 支付回调
     */
    @PostMapping("/callback/{channel}")
    public Result<String> callback(@PathVariable("channel") String channel,
                                    @RequestParam Map<String, String> params) {
        String result = paymentService.handleCallback(channel, params);
        return Result.ok(result);
    }
}
