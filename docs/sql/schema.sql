-- 用户表
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
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 权限表
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
CREATE TABLE `role_permission` (
  `role_id` int NOT NULL COMMENT '角色ID',
  `permission_id` int NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 赛事表
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