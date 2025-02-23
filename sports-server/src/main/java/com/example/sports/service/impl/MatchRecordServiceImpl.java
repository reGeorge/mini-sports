package com.example.sports.service.impl;

import com.example.sports.entity.MatchRecord;
import com.example.sports.mapper.MatchRecordMapper;
import com.example.sports.service.MatchRecordService;
import com.example.sports.vo.PageVO;
import com.example.sports.vo.UserMatchStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MatchRecordServiceImpl implements MatchRecordService {

    @Autowired
    private MatchRecordMapper matchRecordMapper;

    @Override
    public PageVO<MatchRecord> getUserMatchHistory(Long userId, Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询数据
        List<MatchRecord> list = matchRecordMapper.selectUserMatchHistory(userId, offset, pageSize);
        int total = matchRecordMapper.countUserMatches(userId);
        
        return new PageVO<>(list, total, pageNum, pageSize);
    }

    @Override
    public UserMatchStatsVO getUserMatchStats(Long userId) {
        Map<String, Object> stats = matchRecordMapper.selectUserMatchStats(userId);
        return UserMatchStatsVO.fromMap(stats);
    }
} 