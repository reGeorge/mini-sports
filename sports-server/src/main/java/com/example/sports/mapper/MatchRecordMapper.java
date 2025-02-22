package com.example.sports.mapper;

import com.example.sports.entity.MatchRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchRecordMapper {
    /**
     * 插入比赛记录
     */
    void insert(MatchRecord record);

    /**
     * 更新比赛记录
     */
    void update(MatchRecord record);

    /**
     * 根据ID查询比赛记录
     */
    MatchRecord selectById(@Param("id") Long id);

    /**
     * 根据赛事ID和阶段ID查询比赛记录列表
     */
    List<MatchRecord> selectByTournamentAndStage(@Param("tournamentId") Long tournamentId, @Param("stageId") Long stageId);

    /**
     * 根据分组ID查询比赛记录列表
     */
    List<MatchRecord> selectByGroupId(@Param("groupId") Long groupId);

    /**
     * 删除比赛记录
     */
    void deleteById(@Param("id") Long id);

    /**
     * 根据赛事阶段ID查询比赛记录列表
     */
    List<MatchRecord> selectByStageId(@Param("stageId") Long stageId);

    /**
     * 根据阶段ID和轮次查询比赛记录
     */
    List<MatchRecord> selectByRound(@Param("stageId") Long stageId, @Param("round") Integer round);
}