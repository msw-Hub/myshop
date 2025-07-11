package com.example.myShop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // URL에 /images/** 로 시작하는 요청은 uploadPath 경로에서 리소스를 찾도록 설정
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
    }
}