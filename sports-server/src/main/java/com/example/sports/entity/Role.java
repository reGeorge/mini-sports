package com.example.sports.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class Role {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    
    @JsonIgnore
    private Set<Permission> permissions;
} 