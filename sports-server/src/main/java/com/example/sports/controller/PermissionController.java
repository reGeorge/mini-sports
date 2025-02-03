package com.example.sports.controller;

import com.example.sports.common.Result;
import com.example.sports.entity.Permission;
import com.example.sports.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    @GetMapping
    public Result<List<Permission>> getAllPermissions() {
        return Result.success(permissionService.findAll());
    }
    
    @GetMapping("/{id}")
    public Result<Permission> getPermission(@PathVariable Integer id) {
        return Result.success(permissionService.findById(id));
    }
    
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getUserPermissions(@PathVariable Long userId) {
        return Result.success(permissionService.findByUserId(userId));
    }
} 