package com.saas.order.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(RocketMQTemplate.class)
@RequiredArgsConstructor
public class OrderProducer {

    private final RocketMQTemplate rocketMQTemplate;

    private static final String TOPIC = "ORDER_STATUS";

    /**
     * 发送订单已支付消息
     */
    public void sendPaidMessage(String orderNo) {
        rocketMQTemplate.convertAndSend(TOPIC + ":paid", orderNo);
        log.info("发送支付消息, orderNo={}", orderNo);
    }

    /**
     * 发送订单取消消息
     */
    public void sendCancelMessage(String orderNo) {
        rocketMQTemplate.convertAndSend(TOPIC + ":cancel", orderNo);
        log.info("发送取消消息, orderNo={}", orderNo);
    }

    /**
     * 发送订单发货消息
     */
    public void sendShippedMessage(String orderNo) {
        rocketMQTemplate.convertAndSend(TOPIC + ":shipped", orderNo);
        log.info("发送发货消息, orderNo={}", orderNo);
    }
}
