package com.example.sports.controller;

import com.example.sports.common.response.Result;
import com.example.sports.dto.LoginDTO;
import com.example.sports.dto.RegisterDTO;
import com.example.sports.dto.UpdateUserDTO;
import com.example.sports.entity.User;
import com.example.sports.mapper.UserMapper;
import com.example.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;

import java.util.List;

import com.example.sports.constant.AdminConstant;
import com.example.sports.dto.AdminRegisterRequest;
import com.example.sports.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import com.example.sports.dto.LoginResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String JWT_SECRET = "your-secret-key";
    private static final long JWT_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("API is working!");
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userMapper.findAll());
    }

    @PostMapping("/register")
    public Result<User> register(@Validated @RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginDTO loginDTO) {
        log.info("用户登录，昵称：{}", loginDTO.getNickname());
        log.info("登录密码明文：{}", loginDTO.getPassword());
        
        // 验证用户
        User user = userService.login(loginDTO);
        
        // 生成 JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("nickname", user.getNickname());
        
        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
            .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
            .compact();
            
        log.info("用户 {} 登录成功，生成token：{}", user.getNickname(), token);
        
        // 返回用户信息和token
        LoginResponse response = new LoginResponse();
        response.setUser(user);
        response.setToken(token);
        
        return Result.success(response);
    }

    @PostMapping("/update")
    public Result<User> update(@RequestBody UpdateUserDTO updateUserDTO) {
        log.debug("接收到用户更新请求：{}", updateUserDTO);
        try {
            User user = userService.update(updateUserDTO);
            log.debug("用户更新成功：{}", user);
            return Result.success(user);
        } catch (Exception e) {
            log.error("用户更新失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register/admin")
    public Result<User> registerAdmin(@RequestBody @Validated AdminRegisterRequest request) {
        try {
            log.info("开始管理员注册，昵称：{}", request.getNickname());
            log.info("管理员注册密码明文：{}", request.getPassword());
            
            // 验证管理员注册码
            if (!AdminConstant.ADMIN_CODE.equals(request.getAdminCode())) {
                log.warn("管理员注册码错误：{}", request.getAdminCode());
                return Result.error("管理员注册码错误");
            }
            
            // 检查用户名是否已存在
            User existingUser = userService.findByNickname(request.getNickname());
            if (existingUser != null) {
                log.warn("用户名已存在：{}, 已存储的密码密文：{}", request.getNickname(), existingUser.getCredential());
                return Result.error("用户名已存在");
            }
            
            // 创建用户
            User user = new User();
            user.setNickname(request.getNickname());
            user.setPhone(request.getPhone());
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            log.info("管理员注册密码加密后：{}", encodedPassword);
            user.setCredential(encodedPassword);
            user.setStatus(1);
            user.setPoints(0);
            user.setLevel("BEGINNER");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            // 保存用户
            userService.save(user);
            log.info("用户保存成功，ID：{}", user.getId());
            
            // 分配管理员角色
            roleService.assignUserRole(user.getId(), AdminConstant.ADMIN_ROLE_ID);
            log.info("管理员角色分配成功");
            
            return Result.success(user);
        } catch (Exception e) {
            log.error("管理员注册失败", e);
            return Result.error("注册失败：" + e.getMessage());
        }
    }
} 