package com.example.sports.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String JWT_SECRET = System.getenv().getOrDefault("JWT_SECRET", "your-secret-key");  // 优先用环境变量

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        try {
            String token = extractToken(request);
            log.debug("提取到的token: {}", token);

            if (token != null) {
                Claims claims = validateToken(token);
                if (claims != null) {
                    setAuthentication(claims);
                }
            }
        } catch (Exception e) {
            log.error("处理token时发生错误", e);
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        log.debug("Authorization header: {}", header);
        
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Token验证失败", e);
            return null;
        }
    }

    private void setAuthentication(Claims claims) {
        Long userId = claims.get("userId", Long.class);
        String nickname = claims.get("nickname", String.class);
        log.debug("设置认证信息 - userId: {}, nickname: {}", userId, nickname);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId.toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
} 