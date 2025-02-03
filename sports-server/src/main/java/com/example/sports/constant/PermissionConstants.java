package com.example.sports.constant;

public class PermissionConstants {
    // 用户管理权限
    public static final String USER_VIEW = "user:view";
    public static final String USER_EDIT = "user:edit";
    public static final String USER_DELETE = "user:delete";

    // 角色权限管理
    public static final String ROLE_VIEW = "role:view";
    public static final String ROLE_EDIT = "role:edit";
    public static final String ROLE_DELETE = "role:delete";
    public static final String ROLE_ASSIGN = "role:assign";

    // 赛事管理权限
    public static final String GAME_VIEW = "game:view";
    public static final String GAME_CREATE = "game:create";
    public static final String GAME_EDIT = "game:edit";
    public static final String GAME_DELETE = "game:delete";
    public static final String GAME_AUDIT = "game:audit";
    public static final String GAME_REGISTER = "game:register";

    // 积分管理权限
    public static final String POINTS_VIEW = "points:view";
    public static final String POINTS_EDIT = "points:edit";
    public static final String POINTS_RULE = "points:rule";

    // 系统管理权限
    public static final String SYSTEM_CONFIG = "system:config";
    public static final String SYSTEM_LOG = "system:log";
} 