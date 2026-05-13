package com.saas.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.inventory.dto.DeductStockDTO;
import com.saas.inventory.dto.ReleaseStockDTO;
import com.saas.inventory.entity.StockLog;
import com.saas.inventory.vo.InventoryVO;

import java.util.List;

public interface InventoryService {

    /**
     * Query inventory by SKU ID (cache-first).
     */
    InventoryVO getBySkuId(Long skuId);

    /**
     * Batch query inventory by SKU IDs.
     */
    List<InventoryVO> batchGet(List<Long> skuIds);

    /**
     * Deduct available stock with distributed lock.
     */
    void deduct(DeductStockDTO dto);

    /**
     * Release (add back) available stock.
     */
    void release(ReleaseStockDTO dto);

    /**
     * Paginated stock change log.
     */
    IPage<StockLog> logPage(Long skuId, Integer page, Integer size);
}
