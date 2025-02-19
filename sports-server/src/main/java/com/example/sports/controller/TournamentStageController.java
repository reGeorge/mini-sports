package com.example.sports.controller;

import com.example.sports.common.Result;
import com.example.sports.service.TournamentStageService;
import com.example.sports.vo.TournamentStageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentStageController {

    @Autowired
    private TournamentStageService tournamentStageService;

    /**
     * 获取赛事阶段列表
     *
     * @param tournamentId 赛事ID
     * @return 赛事阶段列表
     */
    @GetMapping("/{tournamentId}/stages")
    public Result<List<TournamentStageVO>> getStages(@PathVariable Long tournamentId) {
        List<TournamentStageVO> stages = tournamentStageService.getStagesByTournamentId(tournamentId);
        return Result.success(stages);
    }
}