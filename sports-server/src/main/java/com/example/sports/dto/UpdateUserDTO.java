package com.example.sports.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private String gripStyle;     // 握拍方式
    private String racketConfig;  // 球拍配置
} 