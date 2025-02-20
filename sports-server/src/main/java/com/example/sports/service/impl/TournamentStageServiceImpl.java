package com.example.sports.service.impl;

import com.example.sports.entity.Tournament;
import com.example.sports.entity.TournamentStage;
import com.example.sports.entity.TournamentGroup;
import com.example.sports.entity.MatchRecord;
import com.example.sports.entity.TournamentRegistration;
import com.example.sports.service.TournamentStageService;
import com.example.sports.service.UserService;
import com.example.sports.vo.TournamentStageVO;
import com.example.sports.service.TournamentService;
import com.example.sports.mapper.TournamentStageMapper;
import com.example.sports.mapper.TournamentGroupMapper;
import com.example.sports.mapper.MatchRecordMapper;
import com.example.sports.mapper.TournamentRegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TournamentStageServiceImpl implements TournamentStageService {

    private static final Logger log = LoggerFactory.getLogger(TournamentStageServiceImpl.class);

    @Autowired
    private TournamentStageMapper tournamentStageMapper;

    @Autowired
    private TournamentGroupMapper tournamentGroupMapper;

    @Autowired
    private MatchRecordMapper matchRecordMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentRegistrationMapper registrationMapper;

    @Override
    @Transactional
    public List<TournamentStage> startTournament(Long tournamentId) {
        log.info("开始处理赛事启动流程，赛事ID: {}", tournamentId);

        // 1. 检查赛事状态和报名情况
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        log.info("获取到赛事信息: {}", tournament);

        if (!"REGISTERING".equals(tournament.getStatus())) {
            throw new IllegalStateException("赛事状态不正确，无法开始赛事");
        }

        // 2. 获取已报名的选手列表
        List<TournamentRegistration> registrations = registrationMapper.selectByTournamentId(tournamentId);
        if (registrations.isEmpty()) {
            throw new IllegalStateException("没有选手报名，无法开始赛事");
        }

        // 3. 生成赛事阶段
        List<TournamentStage> stages = new ArrayList<>();
        
        // 3.1 创建小组赛阶段
        TournamentStage groupStage = new TournamentStage();
        groupStage.setTournamentId(tournamentId);
        groupStage.setType("GROUP");
        groupStage.setStatus("PENDING");
        groupStage.setOrderNum(1);
        groupStage.setName("小组赛");
        groupStage.setCreatedAt(LocalDateTime.now());
        groupStage.setUpdatedAt(LocalDateTime.now());
        tournamentStageMapper.insert(groupStage);
        stages.add(groupStage);

        // 3.2 生成分组
        int totalPlayers = registrations.size();
        int groupSize = 6; // 每组6人
        int numGroups = (totalPlayers + groupSize - 1) / groupSize; // 向上取整

        for (int i = 0; i < numGroups; i++) {
            TournamentGroup group = new TournamentGroup();
            group.setTournamentId(tournamentId);
            group.setStageId(groupStage.getId());
            group.setName(String.format("Group %c", (char)('A' + i)));
            group.setCreatedAt(LocalDateTime.now());
            group.setUpdatedAt(LocalDateTime.now());
            tournamentGroupMapper.insert(group);

            // 为该组生成对阵表
            List<TournamentRegistration> groupPlayers = registrations.subList(
                i * groupSize,
                Math.min((i + 1) * groupSize, totalPlayers)
            );

            // 生成组内循环赛对阵表
            List<MatchRecord> matches = new ArrayList<>();
            for (int j = 0; j < groupPlayers.size(); j++) {
                for (int k = j + 1; k < groupPlayers.size(); k++) {
                    MatchRecord match = new MatchRecord();
                    match.setTournamentId(tournamentId);
                    match.setStageId(groupStage.getId());
                    match.setGroupId(group.getId());
                    match.setPlayer1Id(groupPlayers.get(j).getUserId());
                    match.setPlayer2Id(groupPlayers.get(k).getUserId());
                    match.setStatus("PENDING");
                    match.setPlayer1Score(0);
                    match.setPlayer2Score(0);
                    match.setCreatedAt(LocalDateTime.now());
                    match.setUpdatedAt(LocalDateTime.now());
                    matches.add(match);
                }
            }

            // 优化对阵顺序，确保同一选手不会连续比赛
            List<MatchRecord> optimizedMatches = optimizeMatchOrder(matches);
            for (MatchRecord match : optimizedMatches) {
                matchRecordMapper.insert(match);
            }
        }

        // 3.3 创建淘汰赛阶段
        TournamentStage knockoutStage = new TournamentStage();
        knockoutStage.setTournamentId(tournamentId);
        knockoutStage.setType("KNOCKOUT");
        knockoutStage.setStatus("PENDING");
        knockoutStage.setOrderNum(2);
        knockoutStage.setName("淘汰赛");
        knockoutStage.setCreatedAt(LocalDateTime.now());
        knockoutStage.setUpdatedAt(LocalDateTime.now());
        tournamentStageMapper.insert(knockoutStage);
        stages.add(knockoutStage);

        // 4. 更新赛事状态为进行中
        tournament.setStatus("ONGOING");
        tournamentService.update(tournament);

        return stages;
    }

    @Override
    public TournamentStage updateStageStatus(Long stageId, String status) {
        TournamentStage stage = tournamentStageMapper.selectById(stageId);
        if (stage == null) {
            throw new IllegalArgumentException("赛事阶段不存在");
        }

        stage.setStatus(status);
        stage.setUpdatedAt(LocalDateTime.now());
        tournamentStageMapper.update(stage);

        return stage;
    }

    @Override
    public List<TournamentStageVO> getStagesByTournamentId(Long tournamentId) {
        List<TournamentStage> stages = tournamentStageMapper.selectByTournamentId(tournamentId);
        return stages.stream()
                .<TournamentStageVO>map(stage -> {
                    List<MatchRecord> matches = matchRecordMapper.selectByTournamentAndStage(tournamentId, stage.getId());
                    return TournamentStageVO.fromEntity(stage, matches != null && !matches.isEmpty() ? matches : null, userService);
                })
                .collect(Collectors.toList());
    }

    @Override
    public TournamentStage getCurrentStage(Long tournamentId) {
        return tournamentStageMapper.selectCurrentStage(tournamentId);
    }

    /**
     * 优化对阵顺序，确保同一选手不会连续比赛
     * @param matches 原始对阵列表
     * @return 优化后的对阵列表
     */
    private List<MatchRecord> optimizeMatchOrder(List<MatchRecord> matches) {
        List<MatchRecord> optimized = new ArrayList<>();
        List<MatchRecord> remaining = new ArrayList<>(matches);
        Map<Long, Integer> playerLastMatchIndex = new HashMap<>();

        while (!remaining.isEmpty()) {
            MatchRecord bestMatch = null;
            int bestGap = -1;

            for (MatchRecord match : remaining) {
                // 计算两个选手的最近比赛间隔
                int player1LastMatch = playerLastMatchIndex.getOrDefault(match.getPlayer1Id(), -1);
                int player2LastMatch = playerLastMatchIndex.getOrDefault(match.getPlayer2Id(), -1);
                int currentGap = optimized.size() - Math.max(player1LastMatch, player2LastMatch);

                // 选择间隔最大的比赛
                if (bestMatch == null || currentGap > bestGap) {
                    bestMatch = match;
                    bestGap = currentGap;
                }
            }

            // 更新选手的最近比赛索引
            playerLastMatchIndex.put(bestMatch.getPlayer1Id(), optimized.size());
            playerLastMatchIndex.put(bestMatch.getPlayer2Id(), optimized.size());

            optimized.add(bestMatch);
            remaining.remove(bestMatch);
        }

        return optimized;
    }

    @Override
    @Transactional
    public TournamentStage moveToNextStage(Long tournamentId) {
        // 1. 获取当前阶段
        TournamentStage currentStage = getCurrentStage(tournamentId);
        if (currentStage == null) {
            throw new IllegalStateException("当前没有进行中的赛事阶段");
        }

        // 2. 完成当前阶段
        currentStage.setStatus("FINISHED");
        currentStage.setUpdatedAt(LocalDateTime.now());
        tournamentStageMapper.update(currentStage);

        // 3. 开始下一个阶段
        TournamentStage nextStage = tournamentStageMapper.selectNextStage(tournamentId, currentStage.getOrderNum());
        if (nextStage != null) {
            nextStage.setStatus("ONGOING");
            nextStage.setUpdatedAt(LocalDateTime.now());
            tournamentStageMapper.update(nextStage);
            return nextStage;
        }

        // 4. 如果没有下一个阶段，说明赛事已结束
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        tournament.setStatus("FINISHED");
        tournamentService.update(tournament);

        return null;
    }
}