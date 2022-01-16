package com.example.backend.security.config;

import com.example.backend.repository.UserInfoRepository;
import com.example.backend.security.fitler.JsonAuthenticationFilter;
import com.example.backend.security.fitler.JwtAuthenticationFilter;
import com.example.backend.security.handler.JsonAuthenticationSuccessHandler;
import com.example.backend.security.provider.JsonAuthenticationProvider;
import com.example.backend.security.service.UserInfoService;
import com.example.backend.security.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Order(2)
public class JsonSecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${security.url.login}")
    private String loginUrl;
    private final ObjectMapper objectMapper;

    private final UserInfoRepository userInfoRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(jsonAuthenticationProvider())
                .userDetailsService(userInfoService())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jsonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserInfoService userInfoService(){
        return new UserInfoService(passwordEncoder(), userInfoRepository);
    }

    @Bean
    public JsonAuthenticationProvider jsonAuthenticationProvider(){
        return new JsonAuthenticationProvider(passwordEncoder(), userDetailsService());
    }

    @Bean
    public JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler(){
        return new JsonAuthenticationSuccessHandler();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JsonAuthenticationFilter jsonAuthenticationFilter() throws Exception {
        JsonAuthenticationFilter jsonAuthenticationFilter = new JsonAuthenticationFilter(loginUrl, objectMapper);
        jsonAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        jsonAuthenticationFilter.setAuthenticationSuccessHandler(jsonAuthenticationSuccessHandler());

        return jsonAuthenticationFilter;
    }
}