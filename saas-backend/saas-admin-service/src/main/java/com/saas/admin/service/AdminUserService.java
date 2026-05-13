package com.saas.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.admin.dto.AdminLoginDTO;
import com.saas.admin.entity.AdminUser;

import java.util.Map;

public interface AdminUserService {

    /**
     * 管理员登录
     */
    Map<String, Object> login(AdminLoginDTO dto);

    /**
     * 分页查询管理员列表
     */
    IPage<AdminUser> listUsers(Long page, Long size, String keyword);

    /**
     * 创建管理员
     */
    AdminUser createUser(AdminUser adminUser);

    /**
     * 更新管理员
     */
    AdminUser updateUser(Long id, AdminUser adminUser);

    /**
     * 删除管理员
     */
    void deleteUser(Long id);
}
