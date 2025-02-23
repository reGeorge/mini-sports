
-- 回退比赛阶段的脚本
-- 开始事务
BEGIN;

-- 存储要清理的赛事ID（请替换为实际的赛事ID）
SET @tournament_id = 17;

-- 删除积分变更记录
DELETE FROM points_record 
WHERE tournament_id = @tournament_id;

-- 删除比赛记录
DELETE FROM match_record 
WHERE tournament_id = @tournament_id;

-- 删除小组信息
DELETE FROM tournament_group 
WHERE tournament_id = @tournament_id;

-- 删除赛事阶段
DELETE FROM tournament_stage 
WHERE tournament_id = @tournament_id;

-- 重置赛事状态为报名中
UPDATE tournament 
SET status = 'REGISTERING',
    updated_at = NOW()
WHERE id = @tournament_id;

-- 提交事务
COMMIT;