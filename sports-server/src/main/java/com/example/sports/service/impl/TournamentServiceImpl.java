package com.example.sports.service.impl;

import com.example.sports.entity.Tournament;
import com.example.sports.exception.BusinessException;
import com.example.sports.mapper.TournamentMapper;
import com.example.sports.service.TournamentService;
import com.example.sports.utils.SecurityUtils;
import com.example.sports.vo.PageVO;
import com.example.sports.vo.TournamentQueryVO;
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
        log.info("========== 查询赛事列表 ==========");
        log.info("查询参数: {}", queryVO);
        
        // 计算偏移量
        int offset = (queryVO.getPage() - 1) * queryVO.getPageSize();
        log.info("计算的偏移量: {}", offset);
        
        // 查询数据
        List<Tournament> list = tournamentMapper.selectList(queryVO, offset);
        log.info("查询到的赛事列表: {}", list);
        
        long total = tournamentMapper.selectCount(queryVO);
        log.info("总记录数: {}", total);
        
        PageVO<Tournament> result = new PageVO<>(list, total, queryVO.getPage(), queryVO.getPageSize());
        log.info("返回的分页数据: {}", result);
        
        return result;
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
        // 添加调试日志
        log.info("开始创建赛事，接收到的数据：{}", tournament);
        
        try {
            // 设置初始状态
            tournament.setStatus("DRAFT");
            tournament.setCurrentPlayers(0);
            tournament.setCreatedBy(SecurityUtils.getCurrentUserId());
            tournament.setCreatedAt(new Date());
            tournament.setUpdatedAt(new Date());
            
            log.info("处理后的赛事数据：{}", tournament);
            
            // 插入数据
            tournamentMapper.insert(tournament);
            log.info("赛事创建成功，ID：{}", tournament.getId());
            
            return tournament;
        } catch (Exception e) {
            log.error("创建赛事失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Tournament update(Tournament tournament) {
        // 检查赛事是否存在
        Tournament existing = getById(tournament.getId());
        
        // 只允许修改草稿状态或报名中状态的赛事
        if (!"DRAFT".equals(existing.getStatus()) && !"REGISTERING".equals(existing.getStatus())) {
            throw new BusinessException("只能修改草稿状态或报名中状态的赛事");
        }
        
        // 更新时间
        tournament.setUpdatedAt(new Date());
        
        // 更新数据
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
            case "DRAFT":
                isValid = "REGISTERING".equals(newStatus);
                break;
            case "REGISTERING":
                isValid = "ONGOING".equals(newStatus);
                break;
            case "ONGOING":
                isValid = "FINISHED".equals(newStatus);
                break;
            default:
                isValid = false;
        }

        if (!isValid) {
            throw new BusinessException("非法的状态转换");
        }
    }
}