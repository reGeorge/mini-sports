package com.example.sports.service.impl;

import com.example.sports.entity.Tournament;
import com.example.sports.entity.TournamentRegistration;
import com.example.sports.entity.User;
import com.example.sports.exception.BusinessException;
import com.example.sports.mapper.TournamentMapper;
import com.example.sports.mapper.TournamentRegistrationMapper;
import com.example.sports.service.TournamentRegistrationService;
import com.example.sports.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TournamentRegistrationServiceImpl implements TournamentRegistrationService {

    @Autowired
    private TournamentRegistrationMapper registrationMapper;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Override
    public List<TournamentRegistration> getRegistrations(Long tournamentId) {
        return registrationMapper.selectByTournamentId(tournamentId);
    }

    @Override
    @Transactional
    public void register(Long tournamentId) {
        // 获取当前用户
        Long userId = SecurityUtils.getCurrentUserId();
        
        // 检查赛事是否存在且在报名阶段
        Tournament tournament = tournamentMapper.selectById(tournamentId);
        if (tournament == null) {
            throw new BusinessException("赛事不存在");
        }
        if (!"REGISTERING".equals(tournament.getStatus())) {
            throw new BusinessException("赛事不在报名阶段");
        }
        
        // 检查是否已达到最大参与人数
        if (tournament.getCurrentPlayers() >= tournament.getMaxPlayers()) {
            throw new BusinessException("报名人数已满");
        }
        
        // 检查是否已经报名
        TournamentRegistration existingRegistration = registrationMapper.selectByTournamentIdAndUserId(tournamentId, userId);
        if (existingRegistration != null) {
            throw new BusinessException("您已报名该赛事");
        }
        
        // 创建报名记录
        TournamentRegistration registration = new TournamentRegistration();
        registration.setTournamentId(tournamentId);
        registration.setUserId(userId);
        registration.setStatus("PENDING");
        registration.setPaymentStatus("UNPAID");
        registration.setCreatedAt(new Date());
        registration.setUpdatedAt(new Date());
        
        registrationMapper.insert(registration);
        
        // 更新赛事当前参与人数
        tournamentMapper.incrementCurrentPlayers(tournamentId);
    }

    @Override
    @Transactional
    public void cancelRegistration(Long tournamentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        
        // 检查报名记录是否存在
        TournamentRegistration registration = registrationMapper.selectByTournamentIdAndUserId(tournamentId, userId);
        if (registration == null) {
            throw new BusinessException("未找到报名记录");
        }
        
        // 检查是否可以取消
        if (!"PENDING".equals(registration.getStatus())) {
            throw new BusinessException("当前状态不可取消报名");
        }
        
        // 删除报名记录
        registrationMapper.deleteById(registration.getId());
        
        // 更新赛事当前参与人数
        tournamentMapper.decrementCurrentPlayers(tournamentId);
    }

    @Override
    @Transactional
    public void auditRegistration(Long tournamentId, Long userId, String status) {
        // 检查报名记录是否存在
        TournamentRegistration registration = registrationMapper.selectByTournamentIdAndUserId(tournamentId, userId);
        if (registration == null) {
            throw new BusinessException("未找到报名记录");
        }
        
        // 检查状态是否有效
        if (!List.of("APPROVED", "REJECTED").contains(status)) {
            throw new BusinessException("无效的审核状态");
        }
        
        // 更新状态
        registration.setStatus(status);
        registration.setUpdatedAt(new Date());
        registrationMapper.updateById(registration);
        
        // 如果拒绝报名，需要减少当前参与人数
        if ("REJECTED".equals(status)) {
            tournamentMapper.decrementCurrentPlayers(tournamentId);
        }
    }
} 