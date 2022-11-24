package com.example.sync_everything.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author ForeverDdB
 * @ClassName SecurityConfig
 * @Description
 * @createTime 2022年 09月18日 17:40
 **/
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeRequests(
            au -> au.antMatchers("/", "/**").permitAll()

        ).formLogin().disable().cors().and().csrf().disable()
        .build();
    }
}
