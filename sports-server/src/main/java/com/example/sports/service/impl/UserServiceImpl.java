package com.example.sports.service.impl;

import com.example.sports.dto.LoginDTO;
import com.example.sports.dto.RegisterDTO;
import com.example.sports.entity.User;
import com.example.sports.mapper.UserMapper;
import com.example.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

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
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            registerDTO.getPassword().getBytes()
        );
        user.setCredential(encryptedPassword);
        
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

        // 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        if (!user.getCredential().equals(encryptedPassword)) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        return user;
    }
} 