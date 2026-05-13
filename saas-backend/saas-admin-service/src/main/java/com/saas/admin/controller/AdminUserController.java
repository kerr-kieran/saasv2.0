package com.saas.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.admin.entity.AdminUser;
import com.saas.admin.service.AdminUserService;
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

@Slf4j
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 管理员列表
     */
    @GetMapping("/list")
    public Result<IPage<AdminUser>> list(@RequestParam(defaultValue = "1") Long page,
                                          @RequestParam(defaultValue = "10") Long size,
                                          @RequestParam(required = false) String keyword) {
        IPage<AdminUser> result = adminUserService.listUsers(page, size, keyword);
        return Result.ok(result);
    }

    /**
     * 创建管理员
     */
    @PostMapping
    public Result<AdminUser> create(@RequestBody AdminUser adminUser) {
        AdminUser result = adminUserService.createUser(adminUser);
        return Result.ok(result);
    }

    /**
     * 更新管理员
     */
    @PutMapping("/{id}")
    public Result<AdminUser> update(@PathVariable("id") Long id,
                                     @RequestBody AdminUser adminUser) {
        AdminUser result = adminUserService.updateUser(id, adminUser);
        return Result.ok(result);
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        adminUserService.deleteUser(id);
        return Result.ok();
    }

    /**
     * 分配角色 (placeholder)
     */
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable("id") Long userId,
                                     @RequestBody java.util.Map<String, Object> body) {
        // TODO: 实现角色分配逻辑
        log.info("分配角色, userId={}, roles={}", userId, body);
        return Result.ok();
    }
}
