package com.example.sports.constant;

/**
 * 赛事状态常量
 */
public class TournamentStatus {
    /**
     * 草稿状态
     */
    public static final String DRAFT = "DRAFT";

    /**
     * 报名中状态
     */
    public static final String REGISTERING = "REGISTERING";

    /**
     * 进行中状态
     */
    public static final String ONGOING = "ONGOING";

    /**
     * 已结束状态
     */
    public static final String FINISHED = "FINISHED";

    private TournamentStatus() {
        // 私有构造函数，防止实例化
    }
} 