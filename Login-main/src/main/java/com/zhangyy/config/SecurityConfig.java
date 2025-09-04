package com.zhangyy.config;

import com.zhangyy.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    // 设置过滤器链，由于禁用表单登录，因此UsernamePasswordAuthenticationFilter过滤器不会执行
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)                // 关闭 CSRF
                .formLogin(AbstractHttpConfigurer::disable)          // 禁用表单登录
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/login").permitAll() // 放行 /login
                        .anyRequest().authenticated()          // 其他接口需要认证
                )
                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 注入 AuthenticationManager，用于自定义登录接口
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 手动注册jwt过滤器
    @Bean
    public JwtFilter jwtFilter(StringRedisTemplate redisTemplate) {
        return new JwtFilter(redisTemplate);
    }

}