package com.saas.admin.controller;

import com.saas.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final RestTemplate restTemplate;

    private static final String PRODUCT_SERVICE_URL = "http://saas-product-service/api/product";

    /**
     * 商品分页查询
     */
    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Long page,
                          @RequestParam(defaultValue = "10") Long size,
                          @RequestParam(required = false) String keyword) {
        try {
            String url = PRODUCT_SERVICE_URL + "/page?page=" + page + "&size=" + size;
            if (keyword != null && !keyword.isEmpty()) {
                url += "&keyword=" + keyword;
            }
            return restTemplate.getForObject(url, Result.class);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return Result.fail(500, "获取商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> body) {
        try {
            return restTemplate.postForObject(PRODUCT_SERVICE_URL, body, Result.class);
        } catch (Exception e) {
            log.error("创建商品失败", e);
            return Result.fail(500, "创建商品失败: " + e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable("id") Long id,
                            @RequestBody Map<String, Object> body) {
        try {
            restTemplate.put(PRODUCT_SERVICE_URL + "/" + id, body);
            return Result.ok();
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return Result.fail(500, "更新商品失败: " + e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") Long id) {
        try {
            restTemplate.delete(PRODUCT_SERVICE_URL + "/" + id);
            return Result.ok();
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return Result.fail(500, "删除商品失败: " + e.getMessage());
        }
    }
}
