package com.saas.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.admin.dto.AdminLoginDTO;
import com.saas.admin.entity.AdminUser;
import com.saas.admin.mapper.AdminUserMapper;
import com.saas.admin.service.AdminUserService;
import com.saas.admin.util.JwtUtil;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(AdminLoginDTO dto) {
        // 查询用户
        AdminUser adminUser = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>()
                        .eq(AdminUser::getUsername, dto.getUsername()));
        if (adminUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码 (BCrypt 简化处理: 使用MD5)
        String encodedPassword = DigestUtils.md5DigestAsHex(
                dto.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!adminUser.getPassword().equals(encodedPassword)) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查状态
        if (adminUser.getStatus() != null && adminUser.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "账号已被禁用");
        }

        // 更新登录时间
        adminUser.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(adminUser);

        // 生成Token
        String token = jwtUtil.generateToken(adminUser.getId(), adminUser.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", adminUser.getId());
        result.put("username", adminUser.getUsername());
        result.put("realName", adminUser.getRealName());
        result.put("avatar", adminUser.getAvatar());

        log.info("管理员登录成功, username={}", dto.getUsername());
        return result;
    }

    @Override
    public IPage<AdminUser> listUsers(Long page, Long size, String keyword) {
        Page<AdminUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<AdminUser>()
                .like(keyword != null && !keyword.isEmpty(), AdminUser::getUsername, keyword)
                .or()
                .like(keyword != null && !keyword.isEmpty(), AdminUser::getRealName, keyword)
                .orderByDesc(AdminUser::getCreateTime);
        return adminUserMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUser createUser(AdminUser adminUser) {
        // 检查用户名是否重复
        AdminUser exist = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>()
                        .eq(AdminUser::getUsername, adminUser.getUsername()));
        if (exist != null) {
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "用户名已存在");
        }

        // 密码MD5加密
        adminUser.setPassword(
                DigestUtils.md5DigestAsHex(
                        adminUser.getPassword().getBytes(StandardCharsets.UTF_8)));
        adminUser.setStatus(1);
        adminUserMapper.insert(adminUser);

        log.info("管理员创建成功, username={}", adminUser.getUsername());
        return adminUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUser updateUser(Long id, AdminUser adminUser) {
        AdminUser exist = adminUserMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (adminUser.getPassword() != null && !adminUser.getPassword().isEmpty()) {
            adminUser.setPassword(
                    DigestUtils.md5DigestAsHex(
                            adminUser.getPassword().getBytes(StandardCharsets.UTF_8)));
        }
        adminUser.setId(id);
        adminUserMapper.updateById(adminUser);

        log.info("管理员更新成功, id={}", id);
        return adminUserMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        AdminUser exist = adminUserMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        adminUserMapper.deleteById(id);
        log.info("管理员删除成功, id={}", id);
    }
}
