package com.saas.inventory.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryVO {

    private Long skuId;

    private Long warehouseId;

    private Integer totalStock;

    private Integer availableStock;

    private Integer lockedStock;

    private Integer safetyStock;

    private LocalDateTime updateTime;
}
