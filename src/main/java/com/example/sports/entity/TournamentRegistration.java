package com.example.sports.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TournamentRegistration {
    private Long id;
    
    private Long tournamentId;
    
    private Long userId;
    
    // PENDING, APPROVED, REJECTED
    private String status;
    
    // UNPAID, PAID
    private String paymentStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    
    // 关联的用户信息
    private User user;
} 