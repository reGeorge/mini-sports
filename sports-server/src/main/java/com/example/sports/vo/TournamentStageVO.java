package com.example.sports.vo;

import java.util.List;

public class TournamentStageVO {
    private Long id;
    private String name;
    private String type;  // GROUP, KNOCKOUT
    private String status;  // PENDING, ONGOING, FINISHED
    private List<TournamentMatchVO> matches;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TournamentMatchVO> getMatches() {
        return matches;
    }

    public void setMatches(List<TournamentMatchVO> matches) {
        this.matches = matches;
    }
}