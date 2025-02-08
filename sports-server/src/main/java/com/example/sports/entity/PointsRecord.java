package com.example.sports.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsRecord {
    private Long id;
    private Long userId;
    private Integer points;
    private String description;
    private String type;  // 积分变动类型：增加/减少
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}