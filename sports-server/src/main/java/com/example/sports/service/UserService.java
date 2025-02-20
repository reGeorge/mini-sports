package com.example.sports.service;

import com.example.sports.dto.LoginDTO;
import com.example.sports.dto.RegisterDTO;
import com.example.sports.dto.UpdateUserDTO;
import com.example.sports.dto.UpdatePointsDTO;
import com.example.sports.entity.User;

public interface UserService {
    User register(RegisterDTO registerDTO);
    User login(LoginDTO loginDTO);
    User update(UpdateUserDTO updateUserDTO);
    User findByNickname(String nickname);
    User save(User user);
    void updatePoints(UpdatePointsDTO updatePointsDTO);
    User getUserInfo(Long userId);
    User getUserById(Long player1Id);
}