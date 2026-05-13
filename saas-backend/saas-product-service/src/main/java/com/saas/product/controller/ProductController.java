package com.saas.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.common.Result;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import com.saas.product.dto.ProductQueryDTO;
import com.saas.product.service.ProductService;
import com.saas.product.vo.CategoryVO;
import com.saas.product.vo.SpuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Paginated product query.
     */
    @GetMapping("/page")
    public Result<IPage<SpuVO>> page(ProductQueryDTO query) {
        return Result.ok(productService.page(query));
    }

    /**
     * Product detail by SPU ID.
     */
    @GetMapping("/{id}")
    public Result<SpuVO> detail(@PathVariable Long id) {
        return Result.ok(productService.detail(id));
    }

    /**
     * Keyword search.
     */
    @GetMapping("/search")
    public Result<IPage<SpuVO>> search(@RequestParam String keyword,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size) {
        ProductQueryDTO query = new ProductQueryDTO();
        query.setKeyword(keyword);
        query.setPage(page);
        query.setSize(size);
        return Result.ok(productService.page(query));
    }

    /**
     * Full category tree.
     */
    @GetMapping("/category/tree")
    public Result<List<CategoryVO>> categoryTree() {
        return Result.ok(productService.categoryTree());
    }

    /**
     * Single category by ID.
     */
    @GetMapping("/category/{id}")
    public Result<CategoryVO> categoryById(@PathVariable Long id) {
        // Leverage cached tree to find single category
        List<CategoryVO> tree = productService.categoryTree();
        CategoryVO found = findCategoryInTree(tree, id);
        if (found == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return Result.ok(found);
    }

    private CategoryVO findCategoryInTree(List<CategoryVO> tree, Long targetId) {
        for (CategoryVO node : tree) {
            if (node.getId().equals(targetId)) {
                return node;
            }
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                CategoryVO found = findCategoryInTree(node.getChildren(), targetId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
