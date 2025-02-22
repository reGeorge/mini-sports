package com.example.sports.service.impl;

import com.example.sports.entity.PointsRecord;
import com.example.sports.entity.User;
import com.example.sports.mapper.UserMapper;
import com.example.sports.mapper.PointsRecordMapper;
import com.example.sports.service.PointsService;
import com.example.sports.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Override
    public List<User> searchByNickname(String nickname) {
        return userMapper.searchByNickname(nickname);
    }

    @Override
    public Integer getUserPoints(Long userId) {
        // 获取用户积分
        return userMapper.getUserPoints(userId);
    }

    @Override
    public List<User> getPointsRanking(int limit) {
        // 获取积分排行榜
        return userMapper.getPointsRanking(limit);
    }

    @Override
    public PageVO<PointsRecord> getPointsRecords(Long userId, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 查询数据
        List<PointsRecord> records = pointsRecordMapper.getPointsRecords(userId, offset, size);
        long total = pointsRecordMapper.getPointsRecordsCount(userId);
        
        return new PageVO<>(records, total, page, size);
    }

    @Override
    @Transactional
    public void setUserPoints(Long userId, Integer points, String reason) {
        // 获取用户当前积分
        Integer currentPoints = userMapper.getUserPoints(userId);
        
        // 更新用户积分
        userMapper.updateUserPoints(userId, points);
        
        // 创建积分变更记录
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPointsBefore(currentPoints);
        record.setPointsAfter(points);
        record.setPointsChange(points - currentPoints); // 计算积分变化值
        record.setDescription(reason);
        record.setType(points > currentPoints ? "增加" : "减少");
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        // 保存积分变更记录
        pointsRecordMapper.insert(record);
    }
}
