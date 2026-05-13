package com.saas.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.saas.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_sku")
public class Sku extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String skuNo;

    private Long spuId;

    private String specName;

    private String specData;

    private BigDecimal price;

    private BigDecimal costPrice;

    private String image;

    private Integer status;
}
