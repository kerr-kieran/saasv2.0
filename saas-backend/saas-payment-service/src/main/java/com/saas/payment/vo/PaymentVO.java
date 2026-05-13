package com.saas.payment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentVO {

    private Long id;
    private String payNo;
    private String orderNo;
    private Long userId;
    private BigDecimal amount;
    private String channel;
    private Integer status;
    private String thirdPayNo;
    private LocalDateTime payTime;
    private LocalDateTime callbackTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
