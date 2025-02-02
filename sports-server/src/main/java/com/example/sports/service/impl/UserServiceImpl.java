package com.example.sports.service.impl;

import com.example.sports.dto.LoginDTO;
import com.example.sports.dto.RegisterDTO;
import com.example.sports.dto.UpdateUserDTO;
import com.example.sports.entity.User;
import com.example.sports.mapper.UserMapper;
import com.example.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterDTO registerDTO) {
        // 参数校验
        if (!StringUtils.hasText(registerDTO.getNickname())) {
            throw new RuntimeException("昵称不能为空");
        }
        if (!StringUtils.hasText(registerDTO.getPassword())) {
            throw new RuntimeException("密码不能为空");
        }
        if (!StringUtils.hasText(registerDTO.getPhone())) {
            throw new RuntimeException("手机号不能为空");
        }

        // 检查用户名是否已存在
        if (userMapper.findByNickname(registerDTO.getNickname()) != null) {
            throw new RuntimeException("昵称已存在");
        }

        // 创建新用户
        User user = new User();
        user.setNickname(registerDTO.getNickname());
        user.setPhone(registerDTO.getPhone());
        
        // 密码加密存储
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        log.info("普通用户注册密码明文：{}，密文：{}", registerDTO.getPassword(), encodedPassword);
        user.setCredential(encodedPassword);
        
        // 设置其他默认值
        user.setStatus(1);
        user.setPoints(0);
        user.setLevel("BEGINNER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(LoginDTO loginDTO) {
        // 查找用户
        User user = userMapper.findByNickname(loginDTO.getNickname());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        log.info("用户登录，昵称：{}，密码明文：{}，数据库密文：{}", 
            loginDTO.getNickname(), loginDTO.getPassword(), user.getCredential());

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getCredential())) {
            log.warn("密码验证失败");
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        return user;
    }

    @Override
    public User update(UpdateUserDTO updateUserDTO) {
        if (updateUserDTO.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        User existingUser = userMapper.findById(updateUserDTO.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        existingUser.setGripStyle(updateUserDTO.getGripStyle());
        existingUser.setRacketConfig(updateUserDTO.getRacketConfig());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        userMapper.update(existingUser);
        
        return existingUser;
    }

    @Override
    public User findByNickname(String nickname) {
        return userMapper.findByNickname(nickname);
    }
    
    @Override
    public User save(User user) {
        userMapper.insert(user);
        return user;
    }
} 