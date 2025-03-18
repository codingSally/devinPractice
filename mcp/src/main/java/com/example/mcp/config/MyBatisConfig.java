package com.example.mcp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.mcp.mapper")
public class MyBatisConfig {
}
