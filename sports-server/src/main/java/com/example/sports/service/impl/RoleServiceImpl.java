package com.example.sports.service.impl;

import com.example.sports.entity.Role;
import com.example.sports.entity.Permission;
import com.example.sports.mapper.RoleMapper;
import com.example.sports.mapper.UserMapper;
import com.example.sports.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Role findById(Integer id) {
        Role role = roleMapper.findById(id);
        if (role != null) {
            role.setPermissions(roleMapper.findPermissionsByRoleId(id));
        }
        return role;
    }
    
    @Override
    public List<Role> findAll() {
        List<Role> roles = roleMapper.findAll();
        roles.forEach(role -> role.setPermissions(roleMapper.findPermissionsByRoleId(role.getId())));
        return roles;
    }
    
    @Override
    @Transactional
    public Role create(Role role) {
        roleMapper.insert(role);
        return role;
    }
    
    @Override
    @Transactional
    public Role update(Role role) {
        roleMapper.update(role);
        return role;
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        roleMapper.delete(id);
    }
    
    @Override
    public Set<Permission> findPermissions(Integer roleId) {
        return roleMapper.findPermissionsByRoleId(roleId);
    }
    
    @Override
    @Transactional
    public void addPermission(Integer roleId, Integer permissionId) {
        roleMapper.addPermission(roleId, permissionId);
    }
    
    @Override
    @Transactional
    public void removePermission(Integer roleId, Integer permissionId) {
        roleMapper.removePermission(roleId, permissionId);
    }
    
    @Override
    @Transactional
    public void assignUserRole(Long userId, Integer roleId) {
        userMapper.addUserRole(userId, roleId);
    }
    
    @Override
    @Transactional
    public void removeUserRole(Long userId, Integer roleId) {
        userMapper.removeUserRole(userId, roleId);
    }
} 