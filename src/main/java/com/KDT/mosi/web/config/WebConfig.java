package com.KDT.mosi.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {   // ← implements 추가

  @Value("${upload.path}")       // C:/KDT/projects/uploads/bbs
  private String uploadPath;

  @Value("${upload.url-prefix}") // /static/uploads
  private String urlPrefix;

  // ✅ BCryptPasswordEncoder를 Bean으로 등록
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(urlPrefix + "/**")          // /static/uploads/**
        .addResourceLocations("file:" + uploadPath + "/");
  }
}
