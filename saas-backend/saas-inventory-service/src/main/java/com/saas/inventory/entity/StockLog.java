package com.saas.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Stock change log. No BaseEntity — this is append-only.
 */
@Data
@TableName("saas_stock_log")
public class StockLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long skuId;

    private Long warehouseId;

    /** 1=inbound 2=outbound 3=lock 4=release 5=adjust */
    private Integer changeType;

    private Integer changeQuantity;

    private Integer beforeStock;

    private Integer afterStock;

    private String orderNo;

    private String remark;

    private LocalDateTime createTime;
}
