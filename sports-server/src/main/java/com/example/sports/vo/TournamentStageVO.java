package com.example.sports.vo;

import java.util.List;
import java.util.stream.Collectors;
import com.example.sports.entity.TournamentStage;
import com.example.sports.service.UserService;
import com.example.sports.entity.MatchRecord;

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

    public TournamentStageVO() {
    }

    public static TournamentStageVO fromEntity(TournamentStage stage) {
        if (stage == null) {
            return null;
        }
        
        TournamentStageVO vo = new TournamentStageVO();
        vo.setId(stage.getId());
        vo.setName(stage.getName());
        vo.setType(stage.getType());
        vo.setStatus(stage.getStatus());
        return vo;
    }

    public static TournamentStageVO fromEntity(TournamentStage stage, List<MatchRecord> matchRecords, UserService userService) {
        if (stage == null) {
            return null;
        }
        TournamentStageVO vo = new TournamentStageVO();
        vo.setId(stage.getId());
        vo.setName(stage.getName());
        vo.setType(stage.getType());
        vo.setStatus(stage.getStatus());

        // 设置比赛记录
        if (matchRecords != null && !matchRecords.isEmpty()) {
            List<TournamentMatchVO> matchVOs = matchRecords.stream()
                .map(record -> TournamentMatchVO.fromEntity(record, userService))
                .collect(Collectors.toList());
            vo.setMatches(matchVOs);
        }
        return vo;
    }
}