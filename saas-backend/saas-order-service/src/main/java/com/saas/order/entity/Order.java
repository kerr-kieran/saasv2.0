package com.saas.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.saas.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_order")
public class Order extends BaseEntity {

    private Long id;
    private String orderNo;
    private Long userId;
    /**
     * 0=PENDING 1=PAID 2=SHIPPED 3=COMPLETED 4=CANCELLED
     */
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private LocalDateTime payTime;
    private String payChannel;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String trackingNo;
    private String logisticsCompany;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private String remark;
}
