package com.example.sports.controller;

import com.example.sports.common.response.Result;
import com.example.sports.entity.User;
import com.example.sports.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("API is working!");
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userMapper.findAll());
    }
    
} 