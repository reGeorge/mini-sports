package com.example.sports.controller;

import com.example.sports.common.response.Result;
import com.example.sports.dto.LoginDTO;
import com.example.sports.dto.RegisterDTO;
import com.example.sports.entity.User;
import com.example.sports.mapper.UserMapper;
import com.example.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

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
    public Result<User> login(@Validated @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        return Result.success(user);
    }
} 