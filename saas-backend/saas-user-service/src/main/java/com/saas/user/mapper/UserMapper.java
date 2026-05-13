package com.saas.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
