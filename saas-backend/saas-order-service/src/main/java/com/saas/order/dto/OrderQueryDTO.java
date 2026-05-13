package com.saas.order.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {

    private Integer status;
    private Long page = 1L;
    private Long size = 10L;
}
