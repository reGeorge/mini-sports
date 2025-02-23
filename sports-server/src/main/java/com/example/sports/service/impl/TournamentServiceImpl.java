package com.example.sports.service.impl;

import com.example.sports.entity.Tournament;
import com.example.sports.exception.BusinessException;
import com.example.sports.mapper.TournamentMapper;
import com.example.sports.service.TournamentService;
import com.example.sports.utils.SecurityUtils;
import com.example.sports.vo.PageVO;
import com.example.sports.vo.TournamentQueryVO;
import com.example.sports.constant.TournamentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService {

    private static final Logger log = LoggerFactory.getLogger(TournamentServiceImpl.class);

    @Autowired
    private TournamentMapper tournamentMapper;

    @Override
    public PageVO<Tournament> getList(TournamentQueryVO queryVO) {
        // 计算偏移量
        int offset = (queryVO.getPage() - 1) * queryVO.getPageSize();
        
        // 查询数据
        List<Tournament> list = tournamentMapper.selectList(queryVO, offset);
        long total = tournamentMapper.selectCount(queryVO);
        
        return new PageVO<>(list, total, queryVO.getPage(), queryVO.getPageSize());
    }

    @Override
    public Tournament getById(Long id) {
        Tournament tournament = tournamentMapper.selectById(id);
        if (tournament == null) {
            throw new BusinessException("赛事不存在");
        }
        return tournament;
    }

    @Override
    public Tournament getTournamentById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public Tournament create(Tournament tournament) {
        try {
            // 设置初始状态
            tournament.setStatus("DRAFT");
            tournament.setCurrentPlayers(0);
            tournament.setCreatedBy(SecurityUtils.getCurrentUserId());
            tournament.setCreatedAt(new Date());
            tournament.setUpdatedAt(new Date());
            
            // 插入数据
            tournamentMapper.insert(tournament);
            return tournament;
        } catch (Exception e) {
            log.error("创建赛事失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Tournament update(Tournament tournament) {
        // 更新时间
        tournament.setUpdatedAt(new Date());
        tournamentMapper.update(tournament);
        return tournament;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 检查赛事是否存在
        Tournament tournament = getById(id);
        
        // 只能删除草稿状态的赛事
        if (!"DRAFT".equals(tournament.getStatus())) {
            throw new BusinessException("只能删除草稿状态的赛事");
        }
        
        // 删除数据
        tournamentMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Tournament updateStatus(Long id, String status) {
        // 检查赛事是否存在
        Tournament tournament = getById(id);
        
        // 检查状态转换是否合法
        validateStatusTransition(tournament.getStatus(), status);
        
        // 更新状态
        tournament.setStatus(status);
        tournament.setUpdatedAt(new Date());
        tournamentMapper.update(tournament);
        
        return tournament;
    }

    /**
     * 验证状态转换是否合法
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        boolean isValid = false;
        
        switch (currentStatus) {
            case TournamentStatus.DRAFT:
                isValid = TournamentStatus.REGISTERING.equals(newStatus);
                break;
            case TournamentStatus.REGISTERING:
                isValid = TournamentStatus.ONGOING.equals(newStatus);
                break;
            case TournamentStatus.ONGOING:
                isValid = TournamentStatus.FINISHED.equals(newStatus);
                break;
            default:
                isValid = false;
        }

        if (!isValid) {
            throw new BusinessException("非法的状态转换");
        }
    }

    @Override
    public int getRegisteredCount(Long tournamentId) {
        Tournament tournament = getById(tournamentId);
        if (tournament == null) {
            throw new BusinessException("赛事不存在");
        }
        return tournament.getCurrentPlayers();
    }

    @Override
    public PageVO<Tournament> getUserTournaments(Long userId, Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 获取用户参加的赛事列表
        List<Tournament> tournaments = tournamentMapper.selectUserTournaments(userId, offset, pageSize);
        
        // 获取总数
        int total = tournamentMapper.countUserTournaments(userId);
        
        // 返回分页数据
        return new PageVO<>(tournaments, total, pageNum, pageSize);
    }
}