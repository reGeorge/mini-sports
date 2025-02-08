package com.example.sports.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.example.sports.entity.Permission;

@Data
public class User {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private String credential;
    private Integer points;
    private String level;
    private Integer status;
    private String idType;        // 证件类型
    private String idNumber;      // 证件号码
    private String address;       // 居住地
    private String gripStyle;     // 握拍方式
    private String racketConfig;  // 球拍配置
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Role> roles;     // 用户角色列表
    private List<Permission> permissions; // 用户权限列表
}