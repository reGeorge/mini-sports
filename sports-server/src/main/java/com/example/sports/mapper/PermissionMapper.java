package com.example.sports.mapper;

import com.example.sports.entity.Permission;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PermissionMapper {
    
    @Select("SELECT * FROM permission")
    List<Permission> findAll();
    
    @Select("SELECT * FROM permission WHERE id = #{id}")
    Permission findById(Integer id);
    
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Permission> findByUserId(Long userId);
} 