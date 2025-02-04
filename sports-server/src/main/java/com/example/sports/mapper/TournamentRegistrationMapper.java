package com.example.sports.mapper;

import com.example.sports.entity.TournamentRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TournamentRegistrationMapper {
    // 插入报名记录
    int insert(TournamentRegistration registration);
    
    // 根据ID更新报名记录
    int updateById(TournamentRegistration registration);
    
    // 根据ID删除报名记录
    int deleteById(Long id);
    
    // 根据赛事ID查询报名列表
    List<TournamentRegistration> selectByTournamentId(Long tournamentId);
    
    // 根据赛事ID和用户ID查询报名记录
    TournamentRegistration selectByTournamentIdAndUserId(@Param("tournamentId") Long tournamentId, @Param("userId") Long userId);

    /**
     * 获取赛事中最早的候补报名记录
     */
    TournamentRegistration selectFirstWaitlist(Long tournamentId);
} 