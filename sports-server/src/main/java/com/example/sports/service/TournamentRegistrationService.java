package com.example.sports.service;

import com.example.sports.entity.TournamentRegistration;
import java.util.List;

public interface TournamentRegistrationService {
    // 获取报名列表
    List<TournamentRegistration> getRegistrations(Long tournamentId);
    
    // 报名
    void register(Long tournamentId);
    
    // 取消报名
    void cancelRegistration(Long tournamentId);
    
    // 审核报名
    void auditRegistration(Long tournamentId, Long userId, String status);
} 