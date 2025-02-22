package com.example.sports.service.impl;

import com.example.sports.entity.Tournament;
import com.example.sports.entity.TournamentStage;
import com.example.sports.entity.TournamentGroup;
import com.example.sports.entity.MatchRecord;
import com.example.sports.entity.TournamentRegistration;
import com.example.sports.entity.User;
import com.example.sports.entity.PointsRecord;
import com.example.sports.service.TournamentStageService;
import com.example.sports.service.UserService;
import com.example.sports.vo.TournamentStageVO;
import com.example.sports.service.TournamentService;
import com.example.sports.mapper.TournamentStageMapper;
import com.example.sports.mapper.TournamentGroupMapper;
import com.example.sports.mapper.MatchRecordMapper;
import com.example.sports.mapper.TournamentRegistrationMapper;
import com.example.sports.mapper.PointsRecordMapper;
import com.example.sports.constant.PointsRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

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

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    /**
     * 将 LocalDateTime 转换为 Date
     */
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

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
        LocalDateTime now = LocalDateTime.now();
        groupStage.setCreatedAt(now);
        groupStage.setUpdatedAt(now);
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
            group.setCreatedAt(now);
            group.setUpdatedAt(now);
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
                    match.setCreatedAt(now);
                    match.setUpdatedAt(now);
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
        now = LocalDateTime.now();
        knockoutStage.setCreatedAt(now);
        knockoutStage.setUpdatedAt(now);
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

    @Override
    @Transactional
    public void updateMatchScore(Long tournamentId, Long matchId, Integer player1Score, Integer player2Score) {
        // 1. 验证比分是否合法
        if (player1Score < 0 || player2Score < 0) {
            throw new IllegalArgumentException("比分不能为负数");
        }

        // 2. 获取并验证比赛记录
        MatchRecord match = matchRecordMapper.selectById(matchId);
        if (match == null) {
            throw new IllegalArgumentException("比赛记录不存在");
        }
        if (!match.getTournamentId().equals(tournamentId)) {
            throw new IllegalArgumentException("比赛记录与赛事不匹配");
        }

        // 3. 更新比分和获胜者
        match.setPlayer1Score(player1Score);
        match.setPlayer2Score(player2Score);
        match.setStatus("FINISHED");
        match.setEndTime(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());
        
        // 设置获胜者ID
        if (player1Score > player2Score) {
            match.setWinnerId(match.getPlayer1Id());
        } else if (player2Score > player1Score) {
            match.setWinnerId(match.getPlayer2Id());
        } else {
            throw new IllegalArgumentException("比分不能相等，必须决出胜负");
        }

        // 4. 保存更新
        matchRecordMapper.update(match);

        // 5. 获取赛事阶段信息
        TournamentStage stage = tournamentStageMapper.selectById(match.getStageId());
        if (stage == null) {
            throw new IllegalStateException("赛事阶段不存在");
        }

        // 6. 根据不同阶段类型处理后续逻辑
        if ("GROUP".equals(stage.getType())) {
            handleGroupStageMatch(stage, tournamentId);
        } else if ("KNOCKOUT".equals(stage.getType())) {
            handleKnockoutStageMatch(stage, match);
        }
    }

    /**
     * 处理小组赛阶段比赛完成后的逻辑
     */
    private void handleGroupStageMatch(TournamentStage stage, Long tournamentId) {
        // 获取该阶段所有比赛
        List<MatchRecord> stageMatches = matchRecordMapper.selectByTournamentAndStage(tournamentId, stage.getId());
        
        // 检查是否所有比赛都已完成
        boolean allFinished = stageMatches.stream()
                .allMatch(m -> "FINISHED".equals(m.getStatus()));
        
        // 如果所有比赛都完成，更新阶段状态为已结束，并生成出线名单
        if (allFinished) {
            stage.setStatus("FINISHED");
            stage.setUpdatedAt(LocalDateTime.now());
            tournamentStageMapper.update(stage);

            // 生成小组赛成绩和出线名单
            generateKnockoutMatches(tournamentId, stage.getId());
        }
    }

    /**
     * 处理淘汰赛阶段比赛完成后的逻辑
     */
    private void handleKnockoutStageMatch(TournamentStage stage, MatchRecord finishedMatch) {
        // 1. 获取当前轮次所有比赛
        List<MatchRecord> currentRoundMatches = matchRecordMapper.selectByRound(stage.getId(), finishedMatch.getRound());
        
        // 2. 检查当前轮次是否全部完成
        boolean allFinished = currentRoundMatches.stream()
                .allMatch(match -> "FINISHED".equals(match.getStatus()));
        
        if (allFinished) {
            // 3. 获取获胜者列表
            List<Long> winners = currentRoundMatches.stream()
                    .map(MatchRecord::getWinnerId)
                    .collect(Collectors.toList());
            
            // 4. 获取下一轮比赛
            List<MatchRecord> nextRoundMatches = matchRecordMapper.selectByRound(
                stage.getId(), finishedMatch.getRound() + 1);
            
            if (!nextRoundMatches.isEmpty()) {
                // 5. 更新下一轮比赛的选手
                for (int i = 0; i < winners.size(); i += 2) {
                    if (i/2 < nextRoundMatches.size()) {
                        MatchRecord nextMatch = nextRoundMatches.get(i/2);
                        if (i + 1 < winners.size()) {
                            // 设置两个选手
                            nextMatch.setPlayer1Id(winners.get(i));
                            nextMatch.setPlayer2Id(winners.get(i + 1));
                        } else {
                            // 最后一个选手，直接晋级
                            nextMatch.setPlayer1Id(winners.get(i));
                            nextMatch.setStatus("FINISHED");
                            nextMatch.setWinnerId(winners.get(i));
                        }
                        matchRecordMapper.update(nextMatch);
                    }
                }
            } else {
                // 没有下一轮比赛，说明是决赛结束了
                finishTournament(stage.getTournamentId(), finishedMatch.getWinnerId());
            }
        }
    }

    /**
     * 生成下一轮淘汰赛对阵
     */
    private void generateNextRoundMatches(Long stageId, Integer currentRound, List<Long> winners) {
        if (winners == null || winners.isEmpty()) {
            throw new IllegalStateException("没有获胜者，无法生成下一轮对阵");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 获取赛事ID
        TournamentStage stage = tournamentStageMapper.selectById(stageId);
        if (stage == null) {
            throw new IllegalStateException("赛事阶段不存在");
        }
        
        // 生成下一轮对阵
        for (int i = 0; i < winners.size(); i += 2) {
            if (i + 1 >= winners.size()) {
                continue;
            }
            
            MatchRecord nextMatch = new MatchRecord();
            nextMatch.setTournamentId(stage.getTournamentId());
            nextMatch.setStageId(stageId);
            nextMatch.setPlayer1Id(winners.get(i));
            nextMatch.setPlayer2Id(winners.get(i + 1));
            nextMatch.setRound(currentRound + 1);
            nextMatch.setStatus("PENDING");
            nextMatch.setPlayer1Score(0);
            nextMatch.setPlayer2Score(0);
            nextMatch.setCreatedAt(now);
            nextMatch.setUpdatedAt(now);
            
            matchRecordMapper.insert(nextMatch);
        }
    }

    /**
     * 处理赛事结束逻辑
     */
    private void finishTournament(Long tournamentId, Long winnerId) {
        // 更新赛事状态
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        tournament.setStatus("FINISHED");
        tournament.setEndTime(toDate(LocalDateTime.now()));
        tournament.setUpdatedAt(toDate(LocalDateTime.now()));
        tournamentService.update(tournament);

        // 获取所有比赛记录
        List<MatchRecord> allMatches = matchRecordMapper.selectByTournamentId(tournamentId);
        
        // 用于记录每个用户的积分变化
        Map<Long, Integer> userPointsChanges = new HashMap<>();
        
        // 遍历所有比赛记录，计算积分变化
        for (MatchRecord match : allMatches) {
            if (!"FINISHED".equals(match.getStatus())) {
                continue;
            }
            
            // 获取选手当前积分
            User player1 = userService.getUserById(match.getPlayer1Id());
            User player2 = userService.getUserById(match.getPlayer2Id());
            
            // 计算积分变化
            if (match.getWinnerId().equals(match.getPlayer1Id())) {
                // 选手1胜利
                int winPoints = PointsRule.getWinPoints(player1.getPoints());
                int losePoints = PointsRule.getLosePoints(player2.getPoints());
                
                // 记录积分变化
                recordPointsChange(match, player1, winPoints, "WIN");
                recordPointsChange(match, player2, -losePoints, "LOSE");
                
                // 累计总积分变化
                userPointsChanges.merge(player1.getId(), winPoints, Integer::sum);
                userPointsChanges.merge(player2.getId(), -losePoints, Integer::sum);
            } else {
                // 选手2胜利
                int winPoints = PointsRule.getWinPoints(player2.getPoints());
                int losePoints = PointsRule.getLosePoints(player1.getPoints());
                
                // 记录积分变化
                recordPointsChange(match, player2, winPoints, "WIN");
                recordPointsChange(match, player1, -losePoints, "LOSE");
                
                // 累计总积分变化
                userPointsChanges.merge(player2.getId(), winPoints, Integer::sum);
                userPointsChanges.merge(player1.getId(), -losePoints, Integer::sum);
            }
        }
        
        // 更新所有用户的积分
        for (Map.Entry<Long, Integer> entry : userPointsChanges.entrySet()) {
            Long userId = entry.getKey();
            Integer pointsChange = entry.getValue();
            
            User user = userService.getUserById(userId);
            user.setPoints(Math.max(0, user.getPoints() + pointsChange)); // 确保积分不会小于0
            userService.updateUser(user);
        }
    }
    
    /**
     * 记录积分变化
     */
    private void recordPointsChange(MatchRecord match, User user, int pointsChange, String type) {
        PointsRecord record = new PointsRecord();
        record.setUserId(user.getId());
        record.setTournamentId(match.getTournamentId());
        record.setMatchId(match.getId());
        record.setPointsChange(pointsChange);
        record.setPointsBefore(user.getPoints());
        record.setPointsAfter(Math.max(0, user.getPoints() + pointsChange));
        record.setType(type);
        record.setDescription(String.format(
            "%s在比赛中%s，%s%d积分",
            user.getNickname(),
            "WIN".equals(type) ? "获胜" : "失败",
            pointsChange >= 0 ? "获得" : "失去",
            Math.abs(pointsChange)
        ));
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        pointsRecordMapper.insert(record);
    }

    /**
     * 生成淘汰赛对阵表
     */
    private void generateKnockoutMatches(Long tournamentId, Long groupStageId) {
        // 1. 获取所有小组的比赛记录
        List<TournamentGroup> groups = tournamentGroupMapper.selectByStageId(groupStageId);
        List<PlayerStats> allQualifiedPlayers = new ArrayList<>();

        // 2. 计算每个小组的选手成绩并选出前4名
        for (TournamentGroup group : groups) {
            List<MatchRecord> groupMatches = matchRecordMapper.selectByGroupId(group.getId());
            Map<Long, PlayerStats> playerStatsMap = new HashMap<>();

            // 2.1 统计每个选手的比赛成绩
            for (MatchRecord match : groupMatches) {
                updatePlayerStats(playerStatsMap, match);
            }

            // 2.2 将统计结果转换为列表并排序
            List<PlayerStats> sortedPlayers = new ArrayList<>(playerStatsMap.values());
            sortedPlayers.sort((a, b) -> {
                if (a.getPoints() != b.getPoints()) {
                    return b.getPoints() - a.getPoints(); // 按积分降序
                }
                if (a.getScoreDiff() != b.getScoreDiff()) {
                    return b.getScoreDiff() - a.getScoreDiff(); // 按得失分差降序
                }
                return b.getTotalScore() - a.getTotalScore(); // 按总得分降序
            });

            // 2.3 选取前4名选手
            int qualifiedCount = Math.min(4, sortedPlayers.size());
            allQualifiedPlayers.addAll(sortedPlayers.subList(0, qualifiedCount));
        }

        // 3. 获取淘汰赛阶段
        TournamentStage knockoutStage = tournamentStageMapper.selectNextStage(tournamentId, 1);
        if (knockoutStage == null || !"KNOCKOUT".equals(knockoutStage.getType())) {
            throw new IllegalStateException("未找到淘汰赛阶段或阶段类型错误");
        }

        // 4. 生成淘汰赛对阵表
        List<MatchRecord> knockoutMatches = generateKnockoutPairings(allQualifiedPlayers, tournamentId, knockoutStage.getId());
        
        // 5. 保存淘汰赛对阵记录
        for (MatchRecord match : knockoutMatches) {
            matchRecordMapper.insert(match);
        }
    }

    /**
     * 更新选手统计数据
     */
    private void updatePlayerStats(Map<Long, PlayerStats> statsMap, MatchRecord match) {
        // 处理选手1的统计
        PlayerStats player1Stats = statsMap.computeIfAbsent(match.getPlayer1Id(), k -> new PlayerStats(k));
        player1Stats.addMatch(match.getPlayer1Score(), match.getPlayer2Score());

        // 处理选手2的统计
        PlayerStats player2Stats = statsMap.computeIfAbsent(match.getPlayer2Id(), k -> new PlayerStats(k));
        player2Stats.addMatch(match.getPlayer2Score(), match.getPlayer1Score());
    }

    /**
     * 生成淘汰赛对阵
     */
    private List<MatchRecord> generateKnockoutPairings(List<PlayerStats> players, Long tournamentId, Long stageId) {
        List<MatchRecord> matches = new ArrayList<>();
        int totalPlayers = players.size();
        int nextPowerOfTwo = Integer.highestOneBit(totalPlayers - 1) * 2;
        int byeCount = nextPowerOfTwo - totalPlayers; // 轮空数量

        // 按照积分排序，让成绩好的选手优先轮空
        players.sort((a, b) -> b.getPoints() - a.getPoints());

        List<Long> currentRoundPlayers = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // 添加轮空的选手到下一轮
        for (int i = 0; i < byeCount; i++) {
            currentRoundPlayers.add(players.get(i).getPlayerId());
        }

        // 生成第一轮比赛
        for (int i = byeCount; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                MatchRecord match = new MatchRecord();
                match.setTournamentId(tournamentId);
                match.setStageId(stageId);
                match.setPlayer1Id(players.get(i).getPlayerId());
                match.setPlayer2Id(players.get(i + 1).getPlayerId());
                match.setPlayer1Score(0);
                match.setPlayer2Score(0);
                match.setStatus("PENDING");
                match.setRound(1); // 第一轮
                match.setCreatedAt(now);
                match.setUpdatedAt(now);
                matches.add(match);
            }
        }

        // 生成后续轮次的对阵（预先生成）
        int currentRound = 1;
        int matchesInRound = (players.size() - byeCount) / 2;

        while (matchesInRound > 0) {
            currentRound++;
            matchesInRound = matchesInRound / 2;

            // 为每个预期的比赛创建占位记录
            for (int i = 0; i < matchesInRound; i++) {
                MatchRecord match = new MatchRecord();
                match.setTournamentId(tournamentId);
                match.setStageId(stageId);
                match.setPlayer1Score(0);
                match.setPlayer2Score(0);
                match.setStatus("PENDING");
                match.setRound(currentRound);
                match.setCreatedAt(now);
                match.setUpdatedAt(now);
                matches.add(match);
            }
        }

        return matches;
    }

    /**
     * 选手统计数据类
     */
    private static class PlayerStats {
        private final Long playerId;
        private int points = 0; // 积分
        private int wins = 0; // 胜场
        private int totalScore = 0; // 总得分
        private int totalAgainst = 0; // 总失分

        public PlayerStats(Long playerId) {
            this.playerId = playerId;
        }

        public void addMatch(int scoreFor, int scoreAgainst) {
            if (scoreFor > scoreAgainst) {
                points += 3; // 胜场得3分
                wins++;
            } else if (scoreFor == scoreAgainst) {
                points += 1; // 平局得1分
            }
            totalScore += scoreFor;
            totalAgainst += scoreAgainst;
        }

        public Long getPlayerId() {
            return playerId;
        }

        public int getPoints() {
            return points;
        }

        public int getWins() {
            return wins;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public int getScoreDiff() {
            return totalScore - totalAgainst;
        }
    }
}