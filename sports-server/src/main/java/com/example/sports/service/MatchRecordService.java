package com.example.sports.service;

import com.example.sports.entity.MatchRecord;
import com.example.sports.vo.PageVO;
import com.example.sports.vo.UserMatchStatsVO;

public interface MatchRecordService {
    /**
     * 获取用户比赛历史
     */
    PageVO<MatchRecord> getUserMatchHistory(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户比赛统计数据
     */
    UserMatchStatsVO getUserMatchStats(Long userId);
} 