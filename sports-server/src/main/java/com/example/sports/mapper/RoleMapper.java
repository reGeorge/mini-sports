package com.example.sports.mapper;

import com.example.sports.entity.Role;
import com.example.sports.entity.Permission;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

@Mapper
public interface RoleMapper {
    
    @Select("SELECT * FROM role WHERE id = #{id}")
    Role findById(Integer id);
    
    @Select("SELECT * FROM role")
    List<Role> findAll();
    
    @Insert("INSERT INTO role(name, code, description) VALUES(#{name}, #{code}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);
    
    @Update("UPDATE role SET name = #{name}, code = #{code}, description = #{description} WHERE id = #{id}")
    int update(Role role);
    
    @Delete("DELETE FROM role WHERE id = #{id}")
    int delete(Integer id);
    
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    Set<Permission> findPermissionsByRoleId(Integer roleId);
    
    @Insert("INSERT INTO role_permission(role_id, permission_id) VALUES(#{roleId}, #{permissionId})")
    void addPermission(Integer roleId, Integer permissionId);
    
    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    void removePermission(Integer roleId, Integer permissionId);
    
    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId}")
    void deleteRolePermissions(Integer roleId);

    @Select("SELECT * FROM role WHERE code = #{code}")
    Role findByCode(String code);
} 