package com.example.sports.service;

import com.example.sports.entity.TournamentStage;
import com.example.sports.vo.TournamentStageVO;

import java.util.List;

public interface TournamentStageService {
    /**
     * 开始赛事，生成赛事阶段
     * @param tournamentId 赛事ID
     * @return 生成的赛事阶段列表
     */
    List<TournamentStage> startTournament(Long tournamentId);

    /**
     * 更新赛事阶段状态
     * @param stageId 阶段ID
     * @param status 新状态
     * @return 更新后的赛事阶段
     */
    TournamentStage updateStageStatus(Long stageId, String status);

    /**
     * 获取赛事的所有阶段
     * @param tournamentId 赛事ID
     * @return 赛事阶段列表
     */
    List<TournamentStageVO> getStagesByTournamentId(Long tournamentId);

    /**
     * 获取赛事当前进行的阶段
     * @param tournamentId 赛事ID
     * @return 当前阶段
     */
    TournamentStage getCurrentStage(Long tournamentId);

    /**
     * 进入下一个赛事阶段
     * @param tournamentId 赛事ID
     * @return 下一个阶段
     */
    TournamentStage moveToNextStage(Long tournamentId);
}