package com.example.sports.service.impl;

import com.example.sports.entity.Permission;
import com.example.sports.mapper.PermissionMapper;
import com.example.sports.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Override
    public List<Permission> findAll() {
        return permissionMapper.findAll();
    }
    
    @Override
    public Permission findById(Integer id) {
        return permissionMapper.findById(id);
    }
    
    @Override
    public List<Permission> findByUserId(Long userId) {
        return permissionMapper.findByUserId(userId);
    }
} 