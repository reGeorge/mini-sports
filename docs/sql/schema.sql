-- -------------------------------------------
-- 文件名：schema.sql
-- 描述：数据库表结构定义
-- 日期：2025-02-04
-- 标签：数据库, 表结构
-- -------------------------------------------
-- 启动 mysql 并连接数据库



-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `nickname` varchar(50) NOT NULL COMMENT '昵称',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `credential` varchar(100) NOT NULL COMMENT '密码凭证',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `points` int NOT NULL DEFAULT '0' COMMENT '积分',
  `level` varchar(20) NOT NULL DEFAULT 'BEGINNER' COMMENT '级别',
  `grip_style` varchar(50) DEFAULT NULL COMMENT '握拍方式',
  `racket_config` varchar(100) DEFAULT NULL COMMENT '球拍配置',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_nickname` (`nickname`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 权限表
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(50) NOT NULL COMMENT '权限名称',
  `code` varchar(50) NOT NULL COMMENT '权限编码',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `role_id` int NOT NULL COMMENT '角色ID',
  `permission_id` int NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 赛事表
DROP TABLE IF EXISTS `tournament`;
CREATE TABLE `tournament` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '赛事ID',
  `title` varchar(100) NOT NULL COMMENT '赛事标题',
  `description` text COMMENT '赛事说明',
  `type` varchar(20) NOT NULL COMMENT '类型：SINGLES-单打 DOUBLES-双打 TEAM-团体',
  `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿 REGISTERING-报名中 ONGOING-进行中 FINISHED-已结束',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(200) NOT NULL COMMENT '比赛地点',
  `max_players` int NOT NULL COMMENT '最大参与人数',
  `current_players` int NOT NULL DEFAULT '0' COMMENT '当前参与人数',
  `level` varchar(20) NOT NULL DEFAULT '0' COMMENT '积分上限：0-无限制',
  `entry_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '报名费',
  `created_by` bigint NOT NULL COMMENT '创建人ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事表';

-- 赛事报名表
DROP TABLE IF EXISTS `tournament_registration`;
CREATE TABLE `tournament_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `tournament_id` bigint NOT NULL COMMENT '赛事ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` varchar(20) NOT NULL COMMENT '状态：PENDING-待审核 APPROVED-已通过 REJECTED-已拒绝 WAITLIST-候补中',
  `payment_status` varchar(20) NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付 PAID-已支付',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tournament_user` (`tournament_id`,`user_id`),
  KEY `idx_tournament_status` (`tournament_id`,`status`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事报名表';

-- 积分规则表
DROP TABLE IF EXISTS `points_rule`;
CREATE TABLE `points_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `type` varchar(50) NOT NULL COMMENT '规则类型(GAIN-获取,COST-消耗)',
  `action` varchar(50) NOT NULL COMMENT '行为编码',
  `name` varchar(100) NOT NULL COMMENT '规则名称',
  `points_min_diff` int NOT NULL DEFAULT '0' COMMENT '最小积分差值',
  `points_max_diff` int NOT NULL DEFAULT '0' COMMENT '最大积分差值',
  `win_points` int NOT NULL DEFAULT '0' COMMENT '胜者获得积分',
  `lose_points` int NOT NULL DEFAULT '0' COMMENT '败者失去积分',
  `description` varchar(200) DEFAULT NULL COMMENT '规则描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_action` (`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分规则表';

-- 积分记录表
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rule_id` bigint NOT NULL COMMENT '规则ID',
  `type` varchar(50) NOT NULL COMMENT '类型(GAIN-获取,COST-消耗)',
  `points` int NOT NULL COMMENT '积分值',
  `balance` int NOT NULL COMMENT '变更后余额',
  `description` varchar(200) DEFAULT NULL COMMENT '变更说明',
  `ref_id` bigint DEFAULT NULL COMMENT '关联业务ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_rule_id` (`rule_id`),
  KEY `idx_ref_id` (`ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 积分等级表
DROP TABLE IF EXISTS `points_level`;
CREATE TABLE `points_level` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '等级ID',
  `code` varchar(50) NOT NULL COMMENT '等级编码',
  `name` varchar(100) NOT NULL COMMENT '等级名称',
  `min_points` int NOT NULL COMMENT '最小积分',
  `max_points` int NOT NULL COMMENT '最大积分',
  `description` varchar(200) DEFAULT NULL COMMENT '等级说明',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分等级表';

-- 比赛阶段表
DROP TABLE IF EXISTS `tournament_stage`;
CREATE TABLE `tournament_stage` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '阶段ID',
  `tournament_id` bigint NOT NULL COMMENT '赛事ID',
  `name` varchar(50) NOT NULL COMMENT '阶段名称',
  `type` varchar(20) NOT NULL COMMENT '阶段类型：ROUND_ROBIN-循环赛 KNOCKOUT-淘汰赛',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待开始 ONGOING-进行中 FINISHED-已结束',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '阶段顺序',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tournament` (`tournament_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛阶段表';

-- 比赛分组表
DROP TABLE IF EXISTS `tournament_group`;
CREATE TABLE `tournament_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分组ID',
  `tournament_id` bigint NOT NULL COMMENT '赛事ID',
  `stage_id` bigint NOT NULL COMMENT '阶段ID',
  `name` varchar(50) NOT NULL COMMENT '分组名称',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tournament_stage` (`tournament_id`, `stage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛分组表';

-- 比赛记录表
DROP TABLE IF EXISTS `match_record`;
CREATE TABLE `match_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `tournament_id` bigint NOT NULL COMMENT '赛事ID',
  `stage_id` bigint NOT NULL COMMENT '阶段ID',
  `group_id` bigint DEFAULT NULL COMMENT '分组ID',
  `player1_id` bigint NOT NULL COMMENT '选手1ID',
  `player2_id` bigint NOT NULL COMMENT '选手2ID',
  `player1_score` int NOT NULL DEFAULT '0' COMMENT '选手1得分',
  `player2_score` int NOT NULL DEFAULT '0' COMMENT '选手2得分',
  `winner_id` bigint DEFAULT NULL COMMENT '获胜者ID',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待开始 ONGOING-进行中 FINISHED-已结束',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `round` int DEFAULT NULL COMMENT '比赛轮次',
  `court_number` varchar(20) DEFAULT NULL COMMENT '场地编号',
  `referee_id` bigint DEFAULT NULL COMMENT '裁判ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tournament` (`tournament_id`),
  KEY `idx_stage_group` (`stage_id`, `group_id`),
  KEY `idx_player1` (`player1_id`),
  KEY `idx_player2` (`player2_id`),
  KEY `idx_winner` (`winner_id`),
  KEY `idx_referee` (`referee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛记录表';

-- 比赛详情表
DROP TABLE IF EXISTS `match_detail`;
CREATE TABLE `match_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `match_id` bigint NOT NULL COMMENT '比赛记录ID',
  `game_number` int NOT NULL COMMENT '局数',
  `player1_score` int NOT NULL DEFAULT '0' COMMENT '选手1得分',
  `player2_score` int NOT NULL DEFAULT '0' COMMENT '选手2得分',
  `notes` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_match` (`match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛详情表';