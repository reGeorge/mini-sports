package com.example.sports.controller;

import com.example.sports.common.Result;
import com.example.sports.entity.Tournament;
import com.example.sports.entity.TournamentStage;
import com.example.sports.service.TournamentService;
import com.example.sports.service.TournamentStageService;
import com.example.sports.utils.TournamentGroupingStrategy;
import com.example.sports.utils.SecurityUtils;
import com.example.sports.vo.MatchScoreVO;
import com.example.sports.vo.PageVO;
import com.example.sports.vo.TournamentQueryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private static final Logger log = LoggerFactory.getLogger(TournamentController.class);

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentStageService tournamentStageService;

    /**
     * 获取赛事列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('tournament:view')")
    public Result<PageVO<Tournament>> list(TournamentQueryVO queryVO) {
        try {
            PageVO<Tournament> pageVO = tournamentService.getList(queryVO);
            return Result.success(pageVO);
        } catch (Exception e) {
            log.error("查询赛事列表失败", e);
            throw e;
        }
    }

    /**
     * 获取赛事详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('tournament:view')")
    public Result<Tournament> getById(@PathVariable Long id) {
        return Result.success(tournamentService.getById(id));
    }

    /**
     * 创建赛事
     */
    @PostMapping
    @PreAuthorize("hasAuthority('tournament:create')")
    public Result<Tournament> create(@RequestBody Tournament tournament) {
        try {
            Tournament created = tournamentService.create(tournament);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建赛事失败", e);
            throw e;
        }
    }

    /**
     * 更新赛事
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('tournament:edit')")
    public Result<Tournament> update(@PathVariable Long id, @RequestBody Tournament tournament) {
        tournament.setId(id);
        return Result.success(tournamentService.update(tournament));
    }

    /**
     * 删除赛事
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('tournament:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        tournamentService.delete(id);
        return Result.success();
    }

    /**
     * 更新赛事状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('tournament:edit')")
    public Result<Tournament> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return Result.success(tournamentService.updateStatus(id, status));
    }

    /**
     * 开始赛事
     */
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('tournament:edit')")
    public Result<List<TournamentStage>> startTournament(@PathVariable Long id) {
        try {
            List<TournamentStage> stages = tournamentStageService.startTournament(id);
            return Result.success(stages);
        } catch (Exception e) {
            log.error("开始赛事失败", e);
            throw e;
        }
    }

    /**
     * 更新比赛比分
     */
    @PutMapping("/{tournamentId}/matches/{matchId}/score")
    @PreAuthorize("hasAuthority('match:score')")
    public Result<Void> updateMatchScore(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestBody MatchScoreVO scoreVO) {
        try {
            tournamentStageService.updateMatchScore(tournamentId, matchId, scoreVO.getPlayer1Score(), scoreVO.getPlayer2Score());
            return Result.success();
        } catch (Exception e) {
            log.error("更新比赛比分失败", e);
            throw e;
        }
    }

    /**
     * 获取用户参加的赛事列表
     */
    @GetMapping("/user/registered")
    public Result<PageVO<Tournament>> getUserTournaments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(tournamentService.getUserTournaments(userId, pageNum, pageSize));
    }

    /**
     * 获取分组策略预览
     */
    @GetMapping("/{id}/grouping-strategy")
    @PreAuthorize("hasAuthority('tournament:view')")
    public Result<TournamentGroupingStrategy.GroupingResult> getGroupingStrategy(@PathVariable Long id) {
        Tournament tournament = tournamentService.getById(id);
        if (tournament == null) {
            return Result.error("赛事不存在");
        }
        
        // 获取已报名人数
        int registeredCount = tournamentService.getRegisteredCount(id);
        return Result.success(TournamentGroupingStrategy.calculateStrategy(registeredCount));
    }
}