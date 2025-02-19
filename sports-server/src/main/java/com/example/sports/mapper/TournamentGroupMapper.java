package com.example.sports.mapper;

import com.example.sports.entity.TournamentGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TournamentGroupMapper {
    /**
     * 插入分组记录
     */
    void insert(TournamentGroup group);

    /**
     * 更新分组记录
     */
    void update(TournamentGroup group);

    /**
     * 根据ID查询分组
     */
    TournamentGroup selectById(@Param("id") Long id);

    /**
     * 根据赛事ID和阶段ID查询分组列表
     */
    List<TournamentGroup> selectByTournamentAndStage(@Param("tournamentId") Long tournamentId, @Param("stageId") Long stageId);

    /**
     * 删除分组
     */
    void deleteById(@Param("id") Long id);
}