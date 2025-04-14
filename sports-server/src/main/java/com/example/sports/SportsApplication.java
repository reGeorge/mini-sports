package com.example.sports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SportsApplication {
    public static void main(String[] args) {
        // 设置系统属性，解决可能的MVC问题
        System.setProperty("spring.mvc.pathmatch.matching-strategy", "ANT_PATH_MATCHER");
        SpringApplication.run(SportsApplication.class, args);
    }
} 