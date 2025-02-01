package com.example.sports.service.impl;

import com.example.sports.entity.User;
import com.example.sports.mapper.UserMapper;
import com.example.sports.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> searchByNickname(String nickname) {
        return userMapper.searchByNickname(nickname);
    }
} 