package com.example.sports.utils;

/**
 * 赛事分组策略工具类
 */
public class TournamentGroupingStrategy {
    
    /**
     * 分组策略结果
     */
    public static class GroupingResult {
        private final int groupCount;        // 分组数量
        private final int playersPerGroup;   // 每组人数
        private final int qualifiersPerGroup; // 每组出线人数
        private final int totalQualifiers;    // 总出线人数
        private final String description;     // 策略描述

        public GroupingResult(int groupCount, int playersPerGroup, int qualifiersPerGroup, int totalQualifiers, String description) {
            this.groupCount = groupCount;
            this.playersPerGroup = playersPerGroup;
            this.qualifiersPerGroup = qualifiersPerGroup;
            this.totalQualifiers = totalQualifiers;
            this.description = description;
        }

        public int getGroupCount() { return groupCount; }
        public int getPlayersPerGroup() { return playersPerGroup; }
        public int getQualifiersPerGroup() { return qualifiersPerGroup; }
        public int getTotalQualifiers() { return totalQualifiers; }
        public String getDescription() { return description; }
    }

    /**
     * 计算分组策略
     * @param totalPlayers 总参赛人数
     * @return 分组策略结果
     */
    public static GroupingResult calculateStrategy(int totalPlayers) {
        int groupCount;
        int playersPerGroup;
        int qualifiersPerGroup;
        int totalQualifiers;
        String description;

        if (totalPlayers <= 6) {
            // 6人及以下分1组
            groupCount = 1;
            playersPerGroup = totalPlayers;
            qualifiersPerGroup = Math.min(4, totalPlayers);
            totalQualifiers = qualifiersPerGroup;
            description = String.format("单组循环赛：共%d人，前%d名进入淘汰赛", totalPlayers, qualifiersPerGroup);
        } else if (totalPlayers <= 12) {
            // 7-12人分2组
            groupCount = 2;
            playersPerGroup = (totalPlayers + 1) / 2;
            qualifiersPerGroup = 4;
            totalQualifiers = Math.min(8, totalPlayers);
            description = String.format("分2组：每组%d-%d人，每组前%d名进入淘汰赛", 
                totalPlayers/2, playersPerGroup, qualifiersPerGroup);
        } else if (totalPlayers <= 24) {
            // 13-24人分4组
            groupCount = 4;
            playersPerGroup = (totalPlayers + 3) / 4;
            qualifiersPerGroup = 2;
            totalQualifiers = 8;
            description = String.format("分4组：每组%d-%d人，每组前%d名进入淘汰赛", 
                totalPlayers/4, playersPerGroup, qualifiersPerGroup);
        } else {
            // 25人以上分8组
            groupCount = 8;
            playersPerGroup = (totalPlayers + 7) / 8;
            qualifiersPerGroup = 2;
            totalQualifiers = 16;
            description = String.format("分8组：每组%d-%d人，每组前%d名进入淘汰赛", 
                totalPlayers/8, playersPerGroup, qualifiersPerGroup);
        }

        return new GroupingResult(groupCount, playersPerGroup, qualifiersPerGroup, totalQualifiers, description);
    }
} 