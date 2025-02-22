package com.example.sports.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsRecord {
    private Long id;
    private Long userId;
    private Long tournamentId;
    private Long matchId;
    private Integer pointsChange;  // 积分变化值，正数为增加，负数为减少
    private Integer pointsBefore;  // 变化前积分
    private Integer pointsAfter;   // 变化后积分
    private String type;           // 类型：WIN-胜利, LOSE-失败
    private String description;    // 描述
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}