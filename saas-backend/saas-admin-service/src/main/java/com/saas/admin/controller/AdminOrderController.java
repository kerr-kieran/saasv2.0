package com.saas.admin.controller;

import com.saas.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final RestTemplate restTemplate;

    private static final String ORDER_SERVICE_URL = "http://saas-order-service/api/order";

    /**
     * 订单分页查询
     */
    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Long page,
                          @RequestParam(defaultValue = "10") Long size,
                          @RequestParam(required = false) Integer status) {
        try {
            String url = ORDER_SERVICE_URL + "/page?page=" + page + "&size=" + size;
            if (status != null) {
                url += "&status=" + status;
            }
            return restTemplate.getForObject(url, Result.class);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return Result.fail(500, "获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable("id") Long id) {
        try {
            return restTemplate.getForObject(ORDER_SERVICE_URL + "/" + id, Result.class);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return Result.fail(500, "获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 修改订单状态 (发货/完成等)
     */
    @PostMapping("/{id}/status")
    public Result<?> changeStatus(@PathVariable("id") Long id,
                                  @RequestBody Map<String, Object> body) {
        try {
            // 通过订单号更新状态
            String orderNo = (String) body.get("orderNo");
            Integer status = (Integer) body.get("status");
            String url = ORDER_SERVICE_URL + "/status/" + orderNo + "?status=" + status;
            restTemplate.put(url, null);
            return Result.ok();
        } catch (Exception e) {
            log.error("修改订单状态失败", e);
            return Result.fail(500, "修改订单状态失败: " + e.getMessage());
        }
    }
}
