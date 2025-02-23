package com.example.sports.vo;

import lombok.Data;
import java.util.Map;

@Data
public class UserMatchStatsVO {
    private Integer totalMatches;    // 总场次
    private Integer wins;            // 胜场
    private Integer losses;          // 负场
    private Double winRate;          // 胜率
    private Integer totalScores;     // 总得分
    private Integer totalLost;       // 总失分
    private Double avgScore;         // 场均得分
    private Double avgLost;          // 场均失分

    public static UserMatchStatsVO fromMap(Map<String, Object> map) {
        UserMatchStatsVO vo = new UserMatchStatsVO();
        vo.setTotalMatches(((Number) map.get("total_matches")).intValue());
        vo.setWins(((Number) map.get("wins")).intValue());
        vo.setLosses(((Number) map.get("losses")).intValue());
        vo.setTotalScores(((Number) map.get("total_scores")).intValue());
        vo.setTotalLost(((Number) map.get("total_lost")).intValue());
        
        // 计算胜率
        if (vo.getTotalMatches() > 0) {
            vo.setWinRate((double) vo.getWins() / vo.getTotalMatches() * 100);
        }
        
        // 计算场均得分和失分
        if (vo.getTotalMatches() > 0) {
            vo.setAvgScore((double) vo.getTotalScores() / vo.getTotalMatches());
            vo.setAvgLost((double) vo.getTotalLost() / vo.getTotalMatches());
        }
        
        return vo;
    }
} 