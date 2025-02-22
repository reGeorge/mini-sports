package com.example.sports.constant;

/**
 * 积分规则常量
 */
public class PointsRule {
    
    /**
     * 根据用户当前积分获取胜利时的加分
     * @param currentPoints 当前积分
     * @return 胜利加分
     */
    public static int getWinPoints(int currentPoints) {
        if (currentPoints <= 10) return 9;
        if (currentPoints <= 40) return 12;
        if (currentPoints <= 70) return 16;
        if (currentPoints <= 100) return 20;
        if (currentPoints <= 130) return 25;
        if (currentPoints <= 160) return 30;
        if (currentPoints <= 190) return 35;
        if (currentPoints <= 220) return 40;
        if (currentPoints <= 250) return 45;
        return 50;
    }

    /**
     * 根据用户当前积分获取失败时的减分
     * @param currentPoints 当前积分
     * @return 失败减分
     */
    public static int getLosePoints(int currentPoints) {
        if (currentPoints <= 10) return 9;
        if (currentPoints <= 40) return 8;
        if (currentPoints <= 70) return 7;
        if (currentPoints <= 100) return 6;
        if (currentPoints <= 130) return 5;
        if (currentPoints <= 160) return 4;
        if (currentPoints <= 190) return 3;
        if (currentPoints <= 220) return 2;
        if (currentPoints <= 250) return 1;
        return 0;
    }
} 