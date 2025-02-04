package com.example.sports.mapper;

import com.example.sports.entity.Tournament;
import com.example.sports.vo.TournamentQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TournamentMapper {
    /**
     * 查询列表
     */
    List<Tournament> selectList(@Param("query") TournamentQueryVO queryVO, @Param("offset") int offset);

    /**
     * 查询总数
     */
    long selectCount(@Param("query") TournamentQueryVO queryVO);

    /**
     * 根据ID查询
     */
    Tournament selectById(@Param("id") Long id);

    /**
     * 插入数据
     */
    int insert(Tournament tournament);

    /**
     * 更新数据
     */
    int update(Tournament tournament);

    /**
     * 删除数据
     */
    int deleteById(@Param("id") Long id);

    /**
     * 增加当前参与人数
     */
    int incrementCurrentPlayers(@Param("id") Long id);

    /**
     * 减少当前参与人数
     */
    int decrementCurrentPlayers(@Param("id") Long id);
} 