package com.saas.user.service;

import com.saas.common.Result;
import com.saas.user.dto.LoginDTO;
import com.saas.user.dto.RegisterDTO;
import com.saas.user.dto.UserVO;

public interface UserService {

    Result<UserVO> register(RegisterDTO registerDTO);

    Result<String> login(LoginDTO loginDTO);

    void logout(String token);

    UserVO getById(Long id);
}
