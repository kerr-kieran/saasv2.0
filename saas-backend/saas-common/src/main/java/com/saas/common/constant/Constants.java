package com.saas.common.constant;

/**
 * 系统常量定义。
 */
public interface Constants {

    /** Token 请求头名称 */
    String TOKEN_HEADER = "Authorization";

    /** Token 前缀 */
    String TOKEN_PREFIX = "Bearer ";

    /** 用户 ID 请求头，网关解析后传递给下游服务 */
    String USER_ID_HEADER = "X-User-Id";

    /** 用户名请求头，网关解析后传递给下游服务 */
    String USERNAME_HEADER = "X-Username";
}
