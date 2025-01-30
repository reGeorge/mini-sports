package com.example.sports.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.sports.mapper")
public class MyBatisConfig {
    // MyBatis 配置类，扫描 mapper 接口
} 