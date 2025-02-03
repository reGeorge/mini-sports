package com.example.sports.service;

import com.example.sports.entity.Permission;
import java.util.List;

public interface PermissionService {
    List<Permission> findAll();
    Permission findById(Integer id);
    List<Permission> findByUserId(Long userId);
} 