package com.saas.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("saas_payment")
public class Payment {

    private Long id;
    private String payNo;
    private String orderNo;
    private Long userId;
    private BigDecimal amount;
    private String channel;
    /**
     * 0=pending 1=success 2=failed 3=refunded
     */
    private Integer status;
    private String thirdPayNo;
    private LocalDateTime payTime;
    private LocalDateTime callbackTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
