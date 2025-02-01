package com.example.sports.service;

import com.example.sports.entity.User;
import java.util.List;

public interface PointsService {
    List<User> searchByNickname(String nickname);
} 