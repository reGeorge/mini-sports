package com.example.sports.dto;

import lombok.Data;

@Data
public class UpdatePointsDTO {
    private Long userId;
    private Integer points;
    private String reason;
    private String type;
}