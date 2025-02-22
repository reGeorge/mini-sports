package com.example.sports.mapper;

import com.example.sports.entity.TournamentStage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TournamentStageMapper {
    
    /**
     * 插入赛事阶段
     */
    int insert(TournamentStage stage);
    
    /**
     * 更新赛事阶段
     */
    int update(TournamentStage stage);
    
    /**
     * 删除赛事阶段
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID查询
     */
    TournamentStage selectById(@Param("id") Long id);
    
    /**
     * 根据赛事ID查询阶段列表
     */
    List<TournamentStage> selectByTournamentId(@Param("tournamentId") Long tournamentId);

    TournamentStage selectCurrentStage(Long tournamentId);

    TournamentStage selectNextStage(Long tournamentId, Integer currentOrderNum);
}