package com.zhangyy.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;



// 在内存中注册一个用户，包含用户名和加密密码
@Configuration
public class UserConfig {

    // 这个方法可以自定，只要 UserDetailsService 被注册为 Bean，
    // 并且 AuthenticationManager 使用了 DaoAuthenticationProvider，它就会自动被调用
    // 负责到数据库查询
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user")
                .password(passwordEncoder.encode("123456"))
                .roles("USER")
                .build());
        // 这里是直接创建了一个并返回给AuthenticationProvider，之后默认的AuthenticationProvider比对密码是否正确
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}