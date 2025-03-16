package com.example.devin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.devin.interceptor.PerformanceInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * Web MVC configuration for high performance
 * Configures resource caching and performance monitoring
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configure resource handlers for static resources
     * Implements browser caching for improved performance
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cache static resources for 1 day
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic());
        
        // Cache CSS and JS for 1 hour
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
        
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
    }
    
    /**
     * Add performance monitoring interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceInterceptor());
    }
    
    /**
     * Performance monitoring interceptor bean
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
}
