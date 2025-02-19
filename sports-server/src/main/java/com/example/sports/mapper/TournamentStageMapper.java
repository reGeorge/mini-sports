package com.example.sports.mapper;

import com.example.sports.entity.TournamentStage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TournamentStageMapper {
    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TournamentStage stage);

    TournamentStage selectById(Long id);

    List<TournamentStage> selectByTournamentId(Long tournamentId);

    TournamentStage selectCurrentStage(Long tournamentId);

    TournamentStage selectNextStage(Long tournamentId, Integer currentOrderNum);

    int update(TournamentStage stage);
}