package com.example.sports.dto;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String nickname;
    private String phone;
    private String password;
    private String adminCode;
} 