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
    
    @Insert("INSERT INTO role(name, description) VALUES(#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);
    
    @Update("UPDATE role SET name = #{name}, description = #{description} WHERE id = #{id}")
    int update(Role role);
    
    @Delete("DELETE FROM role WHERE id = #{id}")
    int delete(Integer id);
    
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    Set<Permission> findPermissionsByRoleId(Integer roleId);
    
    @Insert("INSERT INTO role_permission(role_id, permission_id) VALUES(#{roleId}, #{permissionId})")
    int addPermission(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);
    
    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int removePermission(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);

    @Select("SELECT * FROM role WHERE code = #{code}")
    Role findByCode(String code);
} 