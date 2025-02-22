package com.example.sports.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TournamentStage {
    private Long id;
    private Long tournamentId;
    private String name;
    private String type;        // GROUP-小组赛, KNOCKOUT-淘汰赛
    private String status;      // PENDING-未开始, ONGOING-进行中, FINISHED-已结束
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private Integer orderNum;  // 排序顺序（数据库字段名为order_num）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}