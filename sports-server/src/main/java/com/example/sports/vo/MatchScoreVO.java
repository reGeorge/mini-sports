package com.example.sports.vo;

import lombok.Data;

@Data
public class MatchScoreVO {
    /**
     * 选手1的比分
     */
    private Integer player1Score;

    /**
     * 选手2的比分
     */
    private Integer player2Score;
}