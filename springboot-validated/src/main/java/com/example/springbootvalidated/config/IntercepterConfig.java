package com.example.springbootvalidated.config;

import com.example.springbootvalidated.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: Kevin
 * @Date: 2022/6/28 10:19
 * @Description:
 */
@Configuration
public class IntercepterConfig implements WebMvcConfigurer {
    @Autowired
    AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**","/login.html","/user/login");

    }
}