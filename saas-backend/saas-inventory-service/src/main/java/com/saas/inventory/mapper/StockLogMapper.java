package com.saas.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.inventory.entity.StockLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockLogMapper extends BaseMapper<StockLog> {
}
