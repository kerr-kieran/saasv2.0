package com.saas.inventory.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import com.saas.inventory.dto.DeductStockDTO;
import com.saas.inventory.dto.ReleaseStockDTO;
import com.saas.inventory.entity.Inventory;
import com.saas.inventory.entity.StockLog;
import com.saas.inventory.mapper.InventoryMapper;
import com.saas.inventory.mapper.StockLogMapper;
import com.saas.inventory.service.InventoryService;
import com.saas.inventory.vo.InventoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final StockLogMapper stockLogMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String STOCK_CACHE_KEY = "inventory:stock:";
    private static final String LOCK_KEY = "lock:inventory:";
    private static final Duration STOCK_CACHE_TTL = Duration.ofMinutes(5);
    private static final long LOCK_EXPIRE_SECONDS = 10;

    @Override
    public InventoryVO getBySkuId(Long skuId) {
        String cacheKey = STOCK_CACHE_KEY + skuId;

        // Try cache first
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return JSONUtil.toBean(cached, InventoryVO.class);
        }

        // Cache miss — query DB
        Inventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, skuId));
        if (inventory == null) {
            return null;
        }

        InventoryVO vo = new InventoryVO();
        BeanUtil.copyProperties(inventory, vo);

        // Write back to cache
        redisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(vo), STOCK_CACHE_TTL);

        return vo;
    }

    @Override
    public List<InventoryVO> batchGet(List<Long> skuIds) {
        List<InventoryVO> result = new ArrayList<>();
        for (Long skuId : skuIds) {
            InventoryVO vo = getBySkuId(skuId);
            if (vo != null) {
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deduct(DeductStockDTO dto) {
        for (DeductStockDTO.StockItem item : dto.getItems()) {
            deductSingleItem(item.getSkuId(), item.getQuantity(), dto.getOrderNo());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void release(ReleaseStockDTO dto) {
        for (DeductStockDTO.StockItem item : dto.getItems()) {
            releaseSingleItem(item.getSkuId(), item.getQuantity(), dto.getOrderNo(), dto.getReason());
        }
    }

    @Override
    public IPage<StockLog> logPage(Long skuId, Integer page, Integer size) {
        Page<StockLog> p = new Page<>(page, size);
        LambdaQueryWrapper<StockLog> wrapper = new LambdaQueryWrapper<>();
        if (skuId != null) {
            wrapper.eq(StockLog::getSkuId, skuId);
        }
        wrapper.orderByDesc(StockLog::getCreateTime);
        return stockLogMapper.selectPage(p, wrapper);
    }

    // ---- private helpers ----

    private void deductSingleItem(Long skuId, Integer quantity, String orderNo) {
        String lockKey = LOCK_KEY + skuId;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(locked)) {
            try {
                Inventory inventory = inventoryMapper.selectOne(
                        new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, skuId));
                if (inventory == null) {
                    throw new BusinessException(ResultCode.NOT_FOUND);
                }
                if (inventory.getAvailableStock() < quantity) {
                    throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
                }

                int beforeStock = inventory.getAvailableStock();
                int afterStock = beforeStock - quantity;

                // Deduct stock
                inventory.setAvailableStock(afterStock);
                inventory.setTotalStock(inventory.getTotalStock() - quantity);
                inventoryMapper.updateById(inventory);

                // Record log
                StockLog stockLog = new StockLog();
                stockLog.setSkuId(skuId);
                stockLog.setWarehouseId(inventory.getWarehouseId());
                stockLog.setChangeType(2); // 2=outbound (deduct)
                stockLog.setChangeQuantity(-quantity);
                stockLog.setBeforeStock(beforeStock);
                stockLog.setAfterStock(afterStock);
                stockLog.setOrderNo(orderNo);
                stockLog.setRemark("Order deduction");
                stockLog.setCreateTime(LocalDateTime.now());
                stockLogMapper.insert(stockLog);

                // Delete cache
                redisTemplate.delete(STOCK_CACHE_KEY + skuId);

                log.info("Deducted stock: skuId={}, quantity={}, before={}, after={}, orderNo={}",
                        skuId, quantity, beforeStock, afterStock, orderNo);

            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            throw new BusinessException(ResultCode.CONFLICT.getCode(),
                    "Failed to acquire lock for skuId: " + skuId + ", please retry");
        }
    }

    private void releaseSingleItem(Long skuId, Integer quantity, String orderNo, String reason) {
        String lockKey = LOCK_KEY + skuId;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(locked)) {
            try {
                Inventory inventory = inventoryMapper.selectOne(
                        new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, skuId));
                if (inventory == null) {
                    throw new BusinessException(ResultCode.NOT_FOUND);
                }

                int beforeStock = inventory.getAvailableStock();
                int afterStock = beforeStock + quantity;

                // Release stock
                inventory.setAvailableStock(afterStock);
                inventory.setTotalStock(inventory.getTotalStock() + quantity);
                inventoryMapper.updateById(inventory);

                // Record log
                StockLog stockLog = new StockLog();
                stockLog.setSkuId(skuId);
                stockLog.setWarehouseId(inventory.getWarehouseId());
                stockLog.setChangeType(4); // 4=release
                stockLog.setChangeQuantity(quantity);
                stockLog.setBeforeStock(beforeStock);
                stockLog.setAfterStock(afterStock);
                stockLog.setOrderNo(orderNo);
                stockLog.setRemark(reason != null ? reason : "Order release");
                stockLog.setCreateTime(LocalDateTime.now());
                stockLogMapper.insert(stockLog);

                // Delete cache
                redisTemplate.delete(STOCK_CACHE_KEY + skuId);

                log.info("Released stock: skuId={}, quantity={}, before={}, after={}, orderNo={}, reason={}",
                        skuId, quantity, beforeStock, afterStock, orderNo, reason);

            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            throw new BusinessException(ResultCode.CONFLICT.getCode(),
                    "Failed to acquire lock for skuId: " + skuId + ", please retry");
        }
    }
}
