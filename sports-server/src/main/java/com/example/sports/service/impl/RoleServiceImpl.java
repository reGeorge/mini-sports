package com.example.sports.service.impl;

import com.example.sports.entity.Role;
import com.example.sports.entity.Permission;
import com.example.sports.mapper.RoleMapper;
import com.example.sports.mapper.UserMapper;
import com.example.sports.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    
    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    
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
    
    @Override
    @Transactional
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        log.debug("更新用户角色，用户ID：{}，角色IDs：{}", userId, roleIds);
        // 先删除用户所有角色
        userMapper.deleteByUserId(userId);
        // 重新分配角色
        for (Long roleId : roleIds) {
            assignUserRole(userId, roleId.intValue());  // 转换为Integer
        }
    }
    
    @Override
    @Transactional
    public void updateRolePermissions(Integer roleId, List<Integer> permissionIds) {
        // 先删除该角色的所有权限
        roleMapper.deleteRolePermissions(roleId);
        
        // 重新添加权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Integer permissionId : permissionIds) {
                roleMapper.addPermission(roleId, permissionId);
            }
        }
    }
} 