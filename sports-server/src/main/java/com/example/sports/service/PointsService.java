package com.example.sports.service;

import com.example.sports.entity.User;
import com.example.sports.vo.PageVO;
import com.example.sports.entity.PointsRecord;
import java.util.List;

public interface PointsService {
    List<User> searchByNickname(String nickname);
    
    List<User> getPointsRanking(int limit);
    
    Integer getUserPoints(Long userId);
    
    PageVO<PointsRecord> getPointsRecords(Long userId, int page, int size);

    /**
     * 管理员设置用户积分
     * @param userId 用户ID
     * @param points 要设置的积分值
     * @param reason 积分变更原因
     */
    void setUserPoints(Long userId, Integer points, String reason);
}