package com.zhangyy.controller;

import com.zhangyy.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;

    public LoginController(AuthenticationManager authenticationManager, StringRedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, 你已经认证成功！";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            // 生成 JWT
            String token = JwtUtil.generateToken(request.getUsername());
            // 存入 Redis（白名单，过期时间1小时）
            redisTemplate.opsForValue().set("token:" + token, request.getUsername(), 1, TimeUnit.HOURS);
            return "{\"token\":\"" + token + "\"}";
        } catch (AuthenticationException e) {
            return "{\"error\":\"账号或密码错误\"}";
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        // 放入黑名单
        redisTemplate.opsForValue().set("blacklist:" + token, "1", 1, TimeUnit.HOURS);
        // 删除白名单
        redisTemplate.delete("token:" + token);
        return "{\"msg\":\"已登出\"}";
    }
}

class LoginRequest {
    private String username;
    private String password;
    // getter / setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}