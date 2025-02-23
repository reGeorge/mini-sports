package com.example.sports.controller;

import com.example.sports.common.Result;
import com.example.sports.entity.MatchRecord;
import com.example.sports.service.MatchRecordService;
import com.example.sports.utils.SecurityUtils;
import com.example.sports.vo.PageVO;
import com.example.sports.vo.UserMatchStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
public class MatchController {

    @Autowired
    private MatchRecordService matchRecordService;

    /**
     * 获取用户比赛历史
     */
    @GetMapping("/user/history")
    public Result<PageVO<MatchRecord>> getUserMatchHistory(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageVO<MatchRecord> pageVO = matchRecordService.getUserMatchHistory(userId, pageNum, pageSize);
        return Result.success(pageVO);
    }

    /**
     * 获取用户比赛统计数据
     */
    @GetMapping("/user/stats")
    public Result<UserMatchStatsVO> getUserMatchStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserMatchStatsVO stats = matchRecordService.getUserMatchStats(userId);
        return Result.success(stats);
    }
} 