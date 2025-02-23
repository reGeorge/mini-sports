package com.example.sports.vo;

import com.example.sports.entity.MatchRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MatchRecordVO extends MatchRecord {
    private String player1Name;    // 选手1名称
    private String player2Name;    // 选手2名称
    private Integer pointsChange;  // 积分变更
    private Integer pointsBalance; // 变更后积分
    private String pointsType;     // 积分类型(WIN/LOSE)
} 