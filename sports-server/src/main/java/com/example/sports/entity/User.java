package com.example.sports.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String openId;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private Integer points;
    private String level;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 