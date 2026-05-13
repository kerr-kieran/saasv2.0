package com.saas.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private Long id;
    private String orderNo;
    private Long userId;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<OrderItemVO> items;
}
