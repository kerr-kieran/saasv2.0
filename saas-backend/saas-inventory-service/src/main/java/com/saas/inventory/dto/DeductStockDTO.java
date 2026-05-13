package com.saas.inventory.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class DeductStockDTO {

    @NotBlank(message = "Order number cannot be blank")
    private String orderNo;

    @NotEmpty(message = "Stock items cannot be empty")
    private List<StockItem> items;

    @Data
    public static class StockItem {

        private Long skuId;

        private Integer quantity;
    }
}
