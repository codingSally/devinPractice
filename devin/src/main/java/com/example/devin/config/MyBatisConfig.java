package com.example.devin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.devin.mapper")
public class MyBatisConfig {
}
