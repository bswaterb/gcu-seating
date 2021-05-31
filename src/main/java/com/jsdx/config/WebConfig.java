package com.jsdx.config;


import com.jsdx.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaohui on 2020/12/13
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath  =new ArrayList<>();
        //除了以下的接口都需要带上token进行访问
        excludePath.add("/");
        excludePath.add("/login");
        excludePath.add("/csrf/**");
        excludePath.add("/error/**");
        excludePath.add("/register/**");
//        excludePath.add("/normal/getAccountInfo/**");
        excludePath.add("/swagger-ui.html");
        excludePath.add("/**swagger**/**");
        excludePath.add("/webjars/**");
        excludePath.add("/resources/**");
        excludePath.add("/swagger-resources/**");
        excludePath.add("/v2/**");

        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS", "HEAD")
                .maxAge(3600 * 24);
    }
}
