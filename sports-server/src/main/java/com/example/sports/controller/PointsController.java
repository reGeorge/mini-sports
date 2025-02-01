package com.example.sports.controller;

import com.example.sports.common.response.Result;
import com.example.sports.entity.User;
import com.example.sports.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping("/search")
    public Result<List<User>> searchByNickname(@RequestParam String nickname) {
        List<User> users = pointsService.searchByNickname(nickname);
        return Result.success(users);
    }
} 