package com.saas.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.common.Result;
import com.saas.inventory.dto.DeductStockDTO;
import com.saas.inventory.dto.ReleaseStockDTO;
import com.saas.inventory.entity.StockLog;
import com.saas.inventory.service.InventoryService;
import com.saas.inventory.vo.InventoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Query inventory by SKU ID.
     */
    @GetMapping("/{skuId}")
    public Result<InventoryVO> getBySkuId(@PathVariable Long skuId) {
        InventoryVO vo = inventoryService.getBySkuId(skuId);
        return Result.ok(vo);
    }

    /**
     * Batch query inventory by SKU IDs.
     */
    @PostMapping("/batch")
    public Result<List<InventoryVO>> batchGet(@RequestBody List<Long> skuIds) {
        return Result.ok(inventoryService.batchGet(skuIds));
    }

    /**
     * Deduct stock.
     */
    @PostMapping("/deduct")
    public Result<Void> deduct(@Valid @RequestBody DeductStockDTO dto) {
        inventoryService.deduct(dto);
        return Result.ok();
    }

    /**
     * Release (add back) stock.
     */
    @PostMapping("/release")
    public Result<Void> release(@Valid @RequestBody ReleaseStockDTO dto) {
        inventoryService.release(dto);
        return Result.ok();
    }

    /**
     * Paginated stock change log.
     */
    @GetMapping("/log/page")
    public Result<IPage<StockLog>> logPage(@RequestParam(required = false) Long skuId,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(inventoryService.logPage(skuId, page, size));
    }
}
