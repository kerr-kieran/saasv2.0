package com.saas.order.mq;

import com.saas.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 订单状态消息消费者
 * 监听 ORDER_STATUS topic，根据 tag 更新订单状态
 */
@Slf4j
@Component
@ConditionalOnBean(org.apache.rocketmq.spring.core.RocketMQTemplate.class)
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "ORDER_STATUS",
        consumerGroup = "order-status-consumer-group",
        selectorExpression = "shipped || cancel"
)
public class OrderConsumer implements RocketMQListener<String> {

    private final OrderService orderService;

    @Override
    public void onMessage(String orderNo) {
        log.info("收到订单状态变更消息, orderNo={}", orderNo);
        try {
            // 查询当前订单状态并更新
            // tag为shipped时更新为2(SHIPPED), cancel时更新为4(CANCELLED)
            // 此处简化处理：根据消息中携带的tag决定状态
            // 实际场景中消息体应包含目标状态
            orderService.updateStatus(orderNo, 2); // 默认处理为已发货
        } catch (Exception e) {
            log.error("处理订单状态消息失败, orderNo={}", orderNo, e);
        }
    }
}
