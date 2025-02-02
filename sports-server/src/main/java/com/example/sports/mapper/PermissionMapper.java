package com.example.sports.mapper;

import com.example.sports.entity.Permission;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

@Mapper
public interface PermissionMapper {
    
    @Select("SELECT * FROM permission WHERE id = #{id}")
    Permission findById(Integer id);
    
    @Select("SELECT * FROM permission")
    List<Permission> findAll();
    
    @Insert("INSERT INTO permission(name, code, description) VALUES(#{name}, #{code}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Permission permission);
    
    @Update("UPDATE permission SET name = #{name}, code = #{code}, description = #{description} WHERE id = #{id}")
    int update(Permission permission);
    
    @Delete("DELETE FROM permission WHERE id = #{id}")
    int delete(Integer id);
    
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    Set<Permission> findByUserId(Long userId);
} 