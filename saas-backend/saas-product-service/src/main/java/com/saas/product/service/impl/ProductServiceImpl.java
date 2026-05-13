package com.saas.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import com.saas.product.dto.ProductQueryDTO;
import com.saas.product.entity.Category;
import com.saas.product.entity.Sku;
import com.saas.product.entity.Spu;
import com.saas.product.mapper.CategoryMapper;
import com.saas.product.mapper.SkuMapper;
import com.saas.product.mapper.SpuMapper;
import com.saas.product.service.ProductService;
import com.saas.product.vo.CategoryVO;
import com.saas.product.vo.SkuVO;
import com.saas.product.vo.SpuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final SpuMapper spuMapper;
    private final SkuMapper skuMapper;
    private final CategoryMapper categoryMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String CATEGORY_TREE_KEY = "product:category:tree";
    private static final Duration CATEGORY_TREE_TTL = Duration.ofHours(24);

    @Override
    public IPage<SpuVO> page(ProductQueryDTO query) {
        Page<Spu> page = new Page<>(query.getPage(), query.getSize());

        LambdaQueryWrapper<Spu> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().trim().isEmpty()) {
            wrapper.like(Spu::getName, query.getKeyword());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(Spu::getCategoryId, query.getCategoryId());
        }
        if (query.getMinPrice() != null) {
            wrapper.ge(Spu::getMinPrice, query.getMinPrice());
        }
        if (query.getMaxPrice() != null) {
            wrapper.le(Spu::getMaxPrice, query.getMaxPrice());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Spu::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Spu::getSales);

        IPage<Spu> spuPage = spuMapper.selectPage(page, wrapper);

        // Collect category IDs and batch query category names
        List<Spu> spuList = spuPage.getRecords();
        Map<Long, String> categoryNameMap = buildCategoryNameMap(spuList);

        List<SpuVO> voList = spuList.stream().map(spu -> {
            SpuVO vo = new SpuVO();
            BeanUtil.copyProperties(spu, vo);
            vo.setCategoryName(categoryNameMap.getOrDefault(spu.getCategoryId(), null));
            return vo;
        }).collect(Collectors.toList());

        IPage<SpuVO> resultPage = new Page<>(spuPage.getCurrent(), spuPage.getSize(), spuPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    @Override
    public SpuVO detail(Long spuId) {
        Spu spu = spuMapper.selectById(spuId);
        if (spu == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        SpuVO vo = new SpuVO();
        BeanUtil.copyProperties(spu, vo);

        // Query SKU list
        List<Sku> skuList = skuMapper.selectList(
                new LambdaQueryWrapper<Sku>().eq(Sku::getSpuId, spuId));
        List<SkuVO> skuVOList = skuList.stream().map(sku -> {
            SkuVO skuVO = new SkuVO();
            BeanUtil.copyProperties(sku, skuVO);
            return skuVO;
        }).collect(Collectors.toList());
        vo.setSkuList(skuVOList);

        // Query category name
        Category category = categoryMapper.selectById(spu.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        return vo;
    }

    @Override
    public List<CategoryVO> categoryTree() {
        // Try cache first
        String cached = redisTemplate.opsForValue().get(CATEGORY_TREE_KEY);
        if (cached != null) {
            return cn.hutool.json.JSONUtil.toList(cached, CategoryVO.class);
        }

        // Query all categories from DB
        List<Category> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));

        if (allCategories.isEmpty()) {
            return Collections.emptyList();
        }

        // Convert to VO list
        List<CategoryVO> voList = allCategories.stream().map(cat -> {
            CategoryVO vo = new CategoryVO();
            BeanUtil.copyProperties(cat, vo);
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());

        // Build tree by parentId
        Map<Long, CategoryVO> voMap = voList.stream()
                .collect(Collectors.toMap(CategoryVO::getId, Function.identity()));

        List<CategoryVO> tree = new ArrayList<>();
        for (CategoryVO vo : voList) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                tree.add(vo);
            } else {
                CategoryVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }

        // Cache result
        redisTemplate.opsForValue().set(CATEGORY_TREE_KEY,
                cn.hutool.json.JSONUtil.toJsonStr(tree), CATEGORY_TREE_TTL);

        return tree;
    }

    /**
     * Build a map of categoryId -> categoryName from a list of SPUs.
     */
    private Map<Long, String> buildCategoryNameMap(List<Spu> spuList) {
        if (spuList.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> categoryIds = spuList.stream()
                .map(Spu::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
        return categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
    }
}
