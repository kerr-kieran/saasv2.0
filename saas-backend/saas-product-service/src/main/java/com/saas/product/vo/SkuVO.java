package com.saas.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SkuVO {

    private Long id;

    private String skuNo;

    private Long spuId;

    private String specName;

    private String specData;

    private BigDecimal price;

    private BigDecimal costPrice;

    private String image;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
