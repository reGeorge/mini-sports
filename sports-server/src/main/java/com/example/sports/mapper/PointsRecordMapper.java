package com.example.sports.mapper;

import com.example.sports.entity.PointsRecord;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PointsRecordMapper {
    /**
     * 插入积分变更记录
     * @param record 积分记录
     */
    void insert(PointsRecord record);
    
    /**
     * 获取用户的积分变更记录
     * @param userId 用户ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 积分变更记录列表
     */
    List<PointsRecord> getPointsRecords(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);
    
    /**
     * 获取用户的积分变更记录总数
     * @param userId 用户ID
     * @return 记录总数
     */
    long getPointsRecordsCount(@Param("userId") Long userId);

    List<PointsRecord> selectByTournamentId(@Param("tournamentId") Long tournamentId);
    
    List<PointsRecord> selectByUserId(@Param("userId") Long userId);
    
    List<PointsRecord> selectByTournamentAndUser(
        @Param("tournamentId") Long tournamentId,
        @Param("userId") Long userId
    );
}