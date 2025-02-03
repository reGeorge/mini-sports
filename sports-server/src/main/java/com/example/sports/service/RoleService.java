package com.example.sports.service;

import com.example.sports.entity.Role;
import com.example.sports.entity.Permission;

import java.util.List;
import java.util.Set;

public interface RoleService {
    Role findById(Integer id);
    List<Role> findAll();
    Role create(Role role);
    Role update(Role role);
    void delete(Integer id);
    
    Set<Permission> findPermissions(Integer roleId);
    void addPermission(Integer roleId, Integer permissionId);
    void removePermission(Integer roleId, Integer permissionId);
    
    void assignUserRole(Long userId, Integer roleId);
    void removeUserRole(Long userId, Integer roleId);

    void updateUserRoles(Long userId, List<Long> roleIds);
} 