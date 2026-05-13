package com.saas.inventory.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ReleaseStockDTO {

    @NotBlank(message = "Order number cannot be blank")
    private String orderNo;

    private String reason;

    @NotEmpty(message = "Stock items cannot be empty")
    private List<DeductStockDTO.StockItem> items;
}
