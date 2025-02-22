package com.example.sports.service;

import com.example.sports.entity.Tournament;
import com.example.sports.vo.PageVO;
import com.example.sports.vo.TournamentQueryVO;

public interface TournamentService {
    /**
     * 获取赛事列表
     */
    PageVO<Tournament> getList(TournamentQueryVO queryVO);

    /**
     * 根据ID获取赛事信息
     * @param id 赛事ID
     * @return 赛事信息
     */
    Tournament getTournamentById(Long id);

    /**
     * 获取赛事详情
     */
    Tournament getById(Long id);

    /**
     * 创建赛事
     */
    Tournament create(Tournament tournament);

    /**
     * 更新赛事
     */
    Tournament update(Tournament tournament);

    /**
     * 删除赛事
     */
    void delete(Long id);

    /**
     * 更新赛事状态
     */
    Tournament updateStatus(Long id, String status);

    /**
     * 获取已报名人数
     */
    int getRegisteredCount(Long tournamentId);

    /**
     * 获取用户参加的赛事列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的赛事列表
     */
    PageVO<Tournament> getUserTournaments(Long userId, Integer pageNum, Integer pageSize);
}