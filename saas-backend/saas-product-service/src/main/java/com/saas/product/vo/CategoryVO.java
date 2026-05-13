package com.saas.product.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryVO {

    private Long id;

    private Long parentId;

    private String name;

    private Integer level;

    private Integer sort;

    private String icon;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<CategoryVO> children;
}
