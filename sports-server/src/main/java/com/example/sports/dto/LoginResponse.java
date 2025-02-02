package com.example.sports.dto;

import com.example.sports.entity.User;
import lombok.Data;

@Data
public class LoginResponse {
    private User user;
    private String token;
} 