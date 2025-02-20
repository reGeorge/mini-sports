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
        // if ("FINISHED".equals(match.getStatus())) {
        //     throw new IllegalStateException("比赛已结束，无法更新比分");
        // }

        // 3. 更新比分
        match.setPlayer1Score(player1Score);
        match.setPlayer2Score(player2Score);
        match.setStatus("FINISHED");
        match.setUpdatedAt(LocalDateTime.now());

        // 4. 保存更新
        matchRecordMapper.update(match);

        // 5. 检查并更新小组赛阶段状态
        TournamentStage stage = tournamentStageMapper.selectById(match.getStageId());
        if (stage != null && "GROUP".equals(stage.getType())) {
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
    }

    /**
     * 生成淘汰赛对阵表
     * @param tournamentId 赛事ID
     * @param groupStageId 小组赛阶段ID
     */
    private void generateKnockoutMatches(Long tournamentId, Long groupStageId) {
        log.info("开始生成淘汰赛对阵表，赛事ID: {}, 小组赛阶段ID: {}", tournamentId, groupStageId);

        // 1. 获取所有小组的比赛记录
        List<TournamentGroup> groups = tournamentGroupMapper.selectByStageId(groupStageId);
        List<PlayerStats> allQualifiedPlayers = new ArrayList<>();
        log.info("获取到 {} 个小组", groups.size());

        // 2. 计算每个小组的选手成绩并选出前4名
        for (TournamentGroup group : groups) {
            log.info("开始处理小组 {} 的成绩统计", group.getName());
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

            // 输出小组排名
            log.info("小组 {} 排名情况：", group.getName());
            for (int i = 0; i < sortedPlayers.size(); i++) {
                PlayerStats player = sortedPlayers.get(i);
                log.info("第{}名 - 选手ID: {}, 积分: {}, 胜场: {}, 总得分: {}, 得失分差: {}",
                    i + 1, player.getPlayerId(), player.getPoints(), player.getWins(),
                    player.getTotalScore(), player.getScoreDiff());
            }

            // 2.3 选取前4名选手
            int qualifiedCount = Math.min(4, sortedPlayers.size());
            allQualifiedPlayers.addAll(sortedPlayers.subList(0, qualifiedCount));
            log.info("小组 {} 晋级 {} 名选手", group.getName(), qualifiedCount);
        }

        // 3. 获取淘汰赛阶段
        TournamentStage knockoutStage = tournamentStageMapper.selectNextStage(tournamentId, 1);
        if (knockoutStage == null || !"KNOCKOUT".equals(knockoutStage.getType())) {
            log.error("未找到淘汰赛阶段或阶段类型错误");
            return;
        }

        // 4. 生成淘汰赛对阵表
        List<MatchRecord> knockoutMatches = generateKnockoutPairings(allQualifiedPlayers, tournamentId, knockoutStage.getId());
        log.info("生成淘汰赛对阵表完成，共 {} 场比赛", knockoutMatches.size());
        
        // 5. 保存淘汰赛对阵记录
        for (MatchRecord match : knockoutMatches) {
            matchRecordMapper.insert(match);
            log.info("创建淘汰赛比赛记录：选手1 ID: {} vs 选手2 ID: {}, 轮次: {}",
                match.getPlayer1Id(), match.getPlayer2Id(), match.getRound());
        }
        
        log.info("淘汰赛对阵表生成完成");
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

        List<Long> firstRoundPlayers = new ArrayList<>();
        
        // 添加轮空的选手
        for (int i = 0; i < byeCount; i++) {
            firstRoundPlayers.add(players.get(i).getPlayerId());
        }

        // 添加需要比赛的选手
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
                match.setCreatedAt(LocalDateTime.now());
                match.setUpdatedAt(LocalDateTime.now());
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