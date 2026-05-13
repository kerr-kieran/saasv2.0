package com.saas.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.product.dto.ProductQueryDTO;
import com.saas.product.vo.CategoryVO;
import com.saas.product.vo.SpuVO;

import java.util.List;

public interface ProductService {

    /**
     * Paginated product query with keyword, category filter, price range.
     */
    IPage<SpuVO> page(ProductQueryDTO query);

    /**
     * Product detail with SPU, SKU list, and category name.
     */
    SpuVO detail(Long spuId);

    /**
     * Full category tree.
     */
    List<CategoryVO> categoryTree();
}
