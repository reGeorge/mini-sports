package com.example.sports.vo;

import java.util.List;
import java.util.stream.Collectors;
import com.example.sports.entity.TournamentStage;
import com.example.sports.service.UserService;
import com.example.sports.entity.MatchRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TournamentStageVO {
    private Long id;
    private Long tournamentId;
    private String name;
    private String type;
    private String status;
    private String typeText;    // 类型文本说明
    private String statusText;  // 状态文本说明
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private Integer orderNum;  // 排序顺序（数据库字段名为order_num）
    private Integer totalMatches;    // 总场次
    private Integer finishedMatches; // 已完成场次
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private List<TournamentMatchVO> matches;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
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

    public String getTypeText() {
        if (type == null) return "";
        switch (type) {
            case "GROUP": return "小组赛";
            case "KNOCKOUT": return "淘汰赛";
            default: return type;
        }
    }

    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case "PENDING": return "未开始";
            case "ONGOING": return "进行中";
            case "FINISHED": return "已结束";
            default: return status;
        }
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
        vo.setTournamentId(stage.getTournamentId());
        vo.setName(stage.getName());
        vo.setType(stage.getType());
        vo.setStatus(stage.getStatus());
        vo.setStartTime(stage.getStartTime());
        vo.setEndTime(stage.getEndTime());
        vo.setOrderNum(stage.getOrderNum());
        vo.setCreatedAt(stage.getCreatedAt());
        vo.setUpdatedAt(stage.getUpdatedAt());
        return vo;
    }

    public static TournamentStageVO fromEntity(TournamentStage stage, List<MatchRecord> matchRecords, UserService userService) {
        TournamentStageVO vo = fromEntity(stage);
        if (vo != null && matchRecords != null && !matchRecords.isEmpty()) {
            List<TournamentMatchVO> matchVOs = matchRecords.stream()
                .map(record -> TournamentMatchVO.fromEntity(record, userService))
                .collect(Collectors.toList());
            vo.setMatches(matchVOs);
            
            // 设置比赛统计信息
            vo.setTotalMatches(matchRecords.size());
            vo.setFinishedMatches((int) matchRecords.stream()
                .filter(match -> "FINISHED".equals(match.getStatus()))
                .count());
        }
        return vo;
    }
}