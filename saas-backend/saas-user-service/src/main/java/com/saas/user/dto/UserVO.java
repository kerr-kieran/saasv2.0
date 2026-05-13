package com.saas.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String phone;

    private String email;

    private String nickname;

    private String avatar;

    private Integer status;

    private LocalDateTime lastLoginTime;
}
