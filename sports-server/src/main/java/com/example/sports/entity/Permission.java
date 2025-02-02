package com.example.sports.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Permission {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createdAt;
} 