package com.example.sports.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsRecord {
    private Long id;
    private Long userId;
    private Long ruleId;
    private String type;           // 类型：WIN-胜利, LOSE-失败
    private Integer points;        // 积分值
    private Integer balance;       // 积分余额
    private String description;    // 描述
    private Long refId;           // 关联ID（比如比赛ID）
    private Integer pointsBefore;  // 变更前积分
    private Integer pointsAfter;   // 变更后积分
    private Integer pointsChange;  // 积分变更值
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}