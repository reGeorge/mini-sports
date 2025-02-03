package com.example.sports.controller;

import com.example.sports.common.Result;
import com.example.sports.entity.Role;
import com.example.sports.entity.Permission;
import com.example.sports.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping("/{id}")
    public Result<Role> getRole(@PathVariable Integer id) {
        return Result.success(roleService.findById(id));
    }
    
    @GetMapping
    public Result<List<Role>> getAllRoles() {
        return Result.success(roleService.findAll());
    }
    
    @PostMapping
    public Result<Role> createRole(@RequestBody Role role) {
        return Result.success(roleService.create(role));
    }
    
    @PutMapping("/{id}")
    public Result<Role> updateRole(@PathVariable Integer id, @RequestBody Role role) {
        role.setId(id);
        return Result.success(roleService.update(role));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Integer id) {
        roleService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/{roleId}/permissions")
    public Result<Set<Permission>> getRolePermissions(@PathVariable Integer roleId) {
        return Result.success(roleService.findPermissions(roleId));
    }
    
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public Result<Void> addRolePermission(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        roleService.addPermission(roleId, permissionId);
        return Result.success();
    }
    
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public Result<Void> removeRolePermission(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        roleService.removePermission(roleId, permissionId);
        return Result.success();
    }
    
    @PostMapping("/assign")
    public Result<Void> assignUserRole(@RequestParam Long userId, @RequestParam Integer roleId) {
        roleService.assignUserRole(userId, roleId);
        return Result.success();
    }
    
    @DeleteMapping("/assign")
    public Result<Void> removeUserRole(@RequestParam Long userId, @RequestParam Integer roleId) {
        roleService.removeUserRole(userId, roleId);
        return Result.success();
    }
    
    @PutMapping("/{roleId}/permissions")
    public Result<Void> updateRolePermissions(
            @PathVariable Integer roleId,
            @RequestBody Map<String, List<Integer>> requestBody) {
        List<Integer> permissionIds = requestBody.get("permissionIds");
        roleService.updateRolePermissions(roleId, permissionIds);
        return Result.success();
    }
} 