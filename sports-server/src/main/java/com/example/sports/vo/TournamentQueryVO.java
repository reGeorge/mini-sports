package com.example.sports.vo;

import lombok.Data;

@Data
public class TournamentQueryVO {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String status;
    private String type;
} 