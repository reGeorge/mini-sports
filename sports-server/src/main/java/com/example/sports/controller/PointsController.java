package com.example.sports.controller;

import com.example.sports.common.response.Result;
import com.example.sports.entity.PointsRecord;
import com.example.sports.entity.User;
import com.example.sports.service.PointsService;
import com.example.sports.vo.PageVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/ranking")
    public Result<List<User>> getPointsRanking(@RequestParam(defaultValue = "10") int limit) {
        List<User> ranking = pointsService.getPointsRanking(limit);
        return Result.success(ranking);
    }

    @GetMapping("/records")
    public Result<PageVO<PointsRecord>> getPointsRecords(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageVO<PointsRecord> records = pointsService.getPointsRecords(userId, page, size);
        return Result.success(records);
    }

    @PutMapping("/admin/user/{userId}/points")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> setUserPoints(
            @PathVariable Long userId,
            @RequestParam Integer points,
            @RequestParam String reason) {
        pointsService.setUserPoints(userId, points, reason);
        return Result.success(null);
    }
}