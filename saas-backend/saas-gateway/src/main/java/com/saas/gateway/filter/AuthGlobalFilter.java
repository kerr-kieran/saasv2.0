package com.saas.gateway.filter;

import com.saas.common.constant.Constants;
import com.saas.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 网关全局认证过滤器。
 * 校验 Authorization 头中的 JWT Token，解析后将 userId 和 username 写入请求头传递给下游服务。
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /** 免认证路径白名单 */
    private static final List<String> WHITELIST = Arrays.asList(
            "/api/user/login",
            "/api/user/register",
            "/api/product/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 白名单路径直接放行
        if (isWhitelisted(path)) {
            log.debug("白名单路径，跳过认证: {}", path);
            return chain.filter(exchange);
        }

        // 从请求头获取 Token
        String authHeader = exchange.getRequest().getHeaders().getFirst(Constants.TOKEN_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(Constants.TOKEN_PREFIX)) {
            log.warn("缺少 Authorization 头或格式不正确: path={}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(Constants.TOKEN_PREFIX.length());

        // 校验 Token
        if (!JwtUtils.validateToken(token, jwtSecret)) {
            log.warn("Token 校验失败: path={}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 解析 Token，提取用户信息
        try {
            Claims claims = JwtUtils.parseToken(token, jwtSecret);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);

            log.debug("Token 校验通过: userId={}, username={}, path={}", userId, username, path);

            // 将用户信息写入请求头，传递给下游服务
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(Constants.USER_ID_HEADER, String.valueOf(userId))
                    .header(Constants.USERNAME_HEADER, username)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            log.error("Token 解析异常: path={}", path, e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * 判断当前路径是否在白名单中。
     */
    private boolean isWhitelisted(String path) {
        return WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    public int getOrder() {
        // 较高的优先级，确保认证过滤器在其他过滤器之前执行
        return -100;
    }
}
