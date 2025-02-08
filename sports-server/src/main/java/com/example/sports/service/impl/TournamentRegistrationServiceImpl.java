package com.example.sports.service.impl;

import com.example.sports.entity.Tournament;
import com.example.sports.entity.TournamentRegistration;
import com.example.sports.entity.User;
import com.example.sports.exception.BusinessException;
import com.example.sports.mapper.TournamentMapper;
import com.example.sports.mapper.TournamentRegistrationMapper;
import com.example.sports.mapper.UserMapper;
import com.example.sports.service.TournamentRegistrationService;
import com.example.sports.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class TournamentRegistrationServiceImpl implements TournamentRegistrationService {

    @Autowired
    private TournamentRegistrationMapper registrationMapper;

    @Autowired
    private TournamentMapper tournamentMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<TournamentRegistration> getRegistrations(Long tournamentId) {
        List<TournamentRegistration> registrations = registrationMapper.selectByTournamentId(tournamentId);
        // 实时查询每个用户的最新信息
        for (TournamentRegistration registration : registrations) {
            if (registration.getUser() != null) {
                User latestUser = userMapper.selectById(registration.getUser().getId());
                registration.setUser(latestUser);
            }
        }
        return registrations;
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
        
        // 检查是否已经报名
        TournamentRegistration existingRegistration = registrationMapper.selectByTournamentIdAndUserId(tournamentId, userId);
        if (existingRegistration != null) {
            throw new BusinessException("您已报名该赛事");
        }
        
        // 创建报名记录
        TournamentRegistration registration = new TournamentRegistration();
        registration.setTournamentId(tournamentId);
        registration.setUserId(userId);
        registration.setCreatedAt(new Date());
        registration.setUpdatedAt(new Date());
        registration.setPaymentStatus("UNPAID");
        
        // 检查是否需要进入候补
        if (tournament.getCurrentPlayers() >= tournament.getMaxPlayers()) {
            registration.setStatus("WAITLIST");
        } else {
            registration.setStatus("PENDING");
            // 只有正式报名才增加当前参与人数
            tournamentMapper.incrementCurrentPlayers(tournamentId);
        }
        
        registrationMapper.insert(registration);
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
        if (!Arrays.asList("PENDING", "WAITLIST").contains(registration.getStatus())) {
            throw new BusinessException("当前状态不可取消报名");
        }
        
        // 删除报名记录
        registrationMapper.deleteById(registration.getId());
        
        // 如果是正式报名状态，则减少参与人数并处理候补
        if ("PENDING".equals(registration.getStatus())) {
            tournamentMapper.decrementCurrentPlayers(tournamentId);
            // 处理候补队列
            processWaitlist(tournamentId);
        }
    }

    /**
     * 处理候补队列
     */
    private void processWaitlist(Long tournamentId) {
        // 获取赛事信息
        Tournament tournament = tournamentMapper.selectById(tournamentId);
        if (tournament == null || tournament.getCurrentPlayers() >= tournament.getMaxPlayers()) {
            return;
        }

        // 获取最早候补的报名记录
        TournamentRegistration waitlistRegistration = registrationMapper.selectFirstWaitlist(tournamentId);
        if (waitlistRegistration != null) {
            // 更新状态为正式报名
            waitlistRegistration.setStatus("PENDING");
            waitlistRegistration.setUpdatedAt(new Date());
            registrationMapper.updateById(waitlistRegistration);
            
            // 增加当前参与人数
            tournamentMapper.incrementCurrentPlayers(tournamentId);
        }
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
        if (!Arrays.asList("APPROVED", "REJECTED").contains(status)) {
            throw new BusinessException("无效的审核状态");
        }
        
        // 更新状态
        registration.setStatus(status);
        registration.setUpdatedAt(new Date());
        registrationMapper.updateById(registration);
    }
}