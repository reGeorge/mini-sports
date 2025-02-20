package com.example.sports.vo;

import com.example.sports.entity.MatchRecord;
import com.example.sports.entity.User;
import com.example.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class TournamentMatchVO {
    private Long id;
    private Long player1Id;
    private String player1Name;
    private Long player2Id;
    private String player2Name;
    private Integer player1Score;
    private Integer player2Score;
    private String status;  // PENDING, ONGOING, FINISHED
    private String winner;  // PLAYER1, PLAYER2

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public Integer getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(Integer player1Score) {
        this.player1Score = player1Score;
    }

    public Integer getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(Integer player2Score) {
        this.player2Score = player2Score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    
    public TournamentMatchVO() {
    }

    private UserService userService;

    @Autowired
    public TournamentMatchVO(UserService userService) {
        this.userService = userService;
    }

    public static TournamentMatchVO fromEntity(MatchRecord record, UserService userService) {
        if (record == null) {
            return null;
        }

        TournamentMatchVO vo = new TournamentMatchVO();
        vo.setId(record.getId());
        vo.setPlayer1Id(record.getPlayer1Id());
        vo.setPlayer2Id(record.getPlayer2Id());
        vo.setPlayer1Score(record.getPlayer1Score());
        vo.setPlayer2Score(record.getPlayer2Score());
        vo.setStatus(record.getStatus());

        // 获取并设置选手姓名
        if (record.getPlayer1Id() != null) {
            User player1 = userService.getUserById(record.getPlayer1Id());
            if (player1 != null) {
                vo.setPlayer1Name(player1.getNickname());
            }
        }
        if (record.getPlayer2Id() != null) {
            User player2 = userService.getUserById(record.getPlayer2Id());
            if (player2 != null) {
                vo.setPlayer2Name(player2.getNickname());
            }
        }

        // 设置获胜者
        if (record.getWinnerId() != null) {
            if (record.getWinnerId().equals(record.getPlayer1Id())) {
                vo.setWinner("PLAYER1");
            } else if (record.getWinnerId().equals(record.getPlayer2Id())) {
                vo.setWinner("PLAYER2");
            }
        }

        return vo;
    }
}