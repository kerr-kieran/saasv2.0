package com.saas.user.controller;

import com.saas.common.Result;
import com.saas.common.utils.JwtUtils;
import com.saas.user.dto.LoginDTO;
import com.saas.user.dto.RegisterDTO;
import com.saas.user.dto.UserVO;
import com.saas.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        userService.logout(token);
        return Result.ok();
    }

    @GetMapping("/info")
    public Result<UserVO> info(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        Long userId = JwtUtils.getUserId(token, jwtSecret);
        UserVO userVO = userService.getById(userId);
        return Result.ok(userVO);
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
