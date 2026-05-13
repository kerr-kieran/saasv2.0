package com.saas.admin.controller;

import com.saas.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

    private final RestTemplate restTemplate;

    private static final String MEMBER_SERVICE_URL = "http://saas-member-service/api/member";

    /**
     * 会员分页查询
     */
    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Long page,
                          @RequestParam(defaultValue = "10") Long size,
                          @RequestParam(required = false) String keyword) {
        try {
            String url = MEMBER_SERVICE_URL + "/page?page=" + page + "&size=" + size;
            if (keyword != null && !keyword.isEmpty()) {
                url += "&keyword=" + keyword;
            }
            return restTemplate.getForObject(url, Result.class);
        } catch (Exception e) {
            log.error("获取会员列表失败", e);
            return Result.fail(500, "获取会员列表失败: " + e.getMessage());
        }
    }

    /**
     * 会员详情
     */
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable("id") Long id) {
        try {
            return restTemplate.getForObject(MEMBER_SERVICE_URL + "/" + id, Result.class);
        } catch (Exception e) {
            log.error("获取会员详情失败", e);
            return Result.fail(500, "获取会员详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新会员状态
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable("id") Long id,
                                   @RequestBody Map<String, Object> body) {
        try {
            restTemplate.put(MEMBER_SERVICE_URL + "/" + id + "/status", body);
            return Result.ok();
        } catch (Exception e) {
            log.error("更新会员状态失败", e);
            return Result.fail(500, "更新会员状态失败: " + e.getMessage());
        }
    }
}
