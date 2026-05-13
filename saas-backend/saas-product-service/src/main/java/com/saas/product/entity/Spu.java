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
@TableName("saas_spu")
public class Spu extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String spuNo;

    private String name;

    private Long categoryId;

    private String brand;

    private String description;

    private String mainImage;

    private String images;

    private Integer status;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer sales;
}
