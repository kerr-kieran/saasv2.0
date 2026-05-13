package com.saas.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.saas.common.Result;
import com.saas.common.ResultCode;
import com.saas.common.exception.BusinessException;
import com.saas.common.utils.JwtUtils;
import com.saas.user.dto.LoginDTO;
import com.saas.user.dto.RegisterDTO;
import com.saas.user.dto.UserVO;
import com.saas.user.entity.User;
import com.saas.user.mapper.UserMapper;
import com.saas.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.ttl}")
    private long jwtTtl;

    private static final String TOKEN_PREFIX = "user:token:";
    private static final long TOKEN_TTL_SECONDS = 7 * 24 * 60 * 60;

    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String, String> redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Result<UserVO> register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String phone = registerDTO.getPhone();
        String email = registerDTO.getEmail();

        // 检查用户名唯一性
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0) {
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "用户名已存在");
        }
        if (phone != null && userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) > 0) {
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "手机号已被注册");
        }
        if (email != null && userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, email)) > 0) {
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(phone);
        user.setEmail(email);
        user.setNickname(username);
        user.setStatus(1);

        userMapper.insert(user);
        log.info("用户注册成功: username={}, id={}", username, user.getId());

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return Result.ok(userVO);
    }

    @Override
    public Result<String> login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "账户已被禁用");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 生成JWT
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), jwtSecret, jwtTtl);

        // 存储token到Redis
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, user.getId().toString(),
                Duration.ofSeconds(TOKEN_TTL_SECONDS));

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户登录成功: username={}, id={}", username, user.getId());
        return Result.ok(token);
    }

    @Override
    public void logout(String token) {
        if (token != null) {
            redisTemplate.delete(TOKEN_PREFIX + token);
            log.info("用户登出成功");
        }
    }

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }
}
