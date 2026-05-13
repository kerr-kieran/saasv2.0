package com.saas.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpuVO {

    private Long id;

    private String spuNo;

    private String name;

    private Long categoryId;

    private String categoryName;

    private String brand;

    private String description;

    private String mainImage;

    private String images;

    private Integer status;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer sales;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<SkuVO> skuList;
}
