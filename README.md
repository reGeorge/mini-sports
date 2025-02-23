---
title: 球赛小程序需求文档
date: 2025-01-28
tags: 
  - 项目文档
---

# 体育赛事管理系统

## 项目简介
这是一个基于 Spring Boot + Vue.js 的体育赛事管理系统，支持赛事创建、报名、比赛进程管理、积分管理等功能。

## 技术栈
### 后端
- Spring Boot 2.7.0
- Spring Security + JWT 认证
- MyBatis
- MySQL 8.0
- Maven

### 前端
- Vue.js
- Vant UI
- Vue Router
- Axios

## 功能特性

### 1. 用户管理
- [x] 用户注册
- [x] 用户登录
- [x] 用户角色管理
- [x] 用户权限控制
- [x] 用户信息管理

### 2. 赛事管理
- [x] 赛事创建
- [x] 赛事编辑
- [x] 赛事列表查询
- [x] 赛事详情查看
- [x] 赛事状态管理（草稿、报名中、进行中、已结束）

### 3. 比赛流程
- [x] 赛事报名
- [x] 报名审核
- [x] 小组赛
  - [x] 自动分组
  - [x] 小组赛程生成
  - [x] 比分记录
  - [x] 小组排名
- [x] 淘汰赛
  - [x] 自动生成对阵表
  - [x] 比分记录
  - [x] 晋级管理

### 4. 积分系统
- [x] 比赛积分规则
- [x] 积分记录
- [x] 积分排名
- [x] 积分历史查询

### 5. 数据统计
- [x] 用户参赛记录
- [x] 用户胜负统计
- [x] 积分变动记录

## 项目结构
```
sports/
├── sports-server/        # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
└── sports-h5/           # 前端项目
    ├── src/
    │   ├── api/
    │   ├── components/
    │   ├── views/
    │   └── router/
    └── package.json
```

## 开发环境要求
- JDK 17
- Node.js 16+
- MySQL 8.0+

## 快速开始

### 后端启动
1. 创建数据库并导入 SQL 文件
2. 修改 `application.properties` 中的数据库配置
3. 运行 Spring Boot 应用

```bash
cd sports-server
mvn spring-boot:run
```

### 前端启动
```bash
cd sports-h5
npm install
npm run serve
```

## API 文档
API 接口文档使用 Swagger 生成，启动后端服务后访问：
```
http://localhost:8088/swagger-ui.html
```

## 贡献指南
1. Fork 本仓库
2. 创建新的功能分支
3. 提交代码
4. 创建 Pull Request

## 许可证
MIT License

## 项目进度

### 已完成功能
1. 用户管理
   - 用户注册/登录
   - 用户信息管理（基本信息、球拍配置、握拍方式等）
   - 权限控制

2. 赛事管理
   - 赛事创建/编辑/删除
   - 赛事状态流转（草稿->报名中->进行中->已结束）
   - 报名人数限制
   - 报名候补功能
     - 人数满时自动进入候补队列
     - 有人取消报名时自动处理候补
     - 候补按报名时间先后排序
   - 报名列表显示
     - 正式报名/已通过在前，候补中次之，已拒绝最后
     - 同一状态内按报名时间排序
   - 用户详情实时查询

3. 积分系统
   - 用户积分记录
   - 积分规则配置
   - 积分实时计算
   - 积分历史查询

4. 比赛系统（部分完成）
   - 分组功能
     - 按照积分进行蛇形分组
     - 支持多种分组策略（1/2/4/8组）
   - 对阵功能
     - 小组循环赛
     - 淘汰赛
   - 比分记录
     - 比分实时更新
     - 自动判定胜负
   - 积分计算
     - 根据比赛结果自动计算积分
     - 积分变更记录

### 待开发功能
1. 比赛系统优化
   - 比赛进度实时展示
   - 比赛数据统计分析
   - 个人战绩统计
   - 对阵图展示优化

2. 排名系统
   - 积分排行榜
   - 等级晋升机制
   - 历史最高积分记录
   - 排名变化趋势分析

3. 数据统计分析
   - 胜率分析
   - 技术统计
   - 历史对战记录分析
   - 数据可视化展示

4. 用户体验优化
   - 比赛实时通知
   - 赛程提醒功能
   - 移动端适配优化
   - 页面加载性能优化

## 技术栈
- 前端：Vue 3 + Vant UI
- 后端：Spring Boot + MyBatis
- 数据库：MySQL

## 一、项目概述

开发一个面向乒乓球运动爱好者的赛事组织和积分管理小程序，帮助用户便捷地参与比赛、记录成绩和追踪个人成长。

## 二、核心功能需求

### 1. 用户系统
- 微信授权登录
- 个人信息管理
  - 基础信息(昵称、头像等)
  - 技术等级/积分
  - 历史对战信息

### 2. 赛事管理
- 赛事发布
  - 比赛类型(单打/双打)
  - 时间地点
  - 参赛要求
  - 费用说明
  - 人数限制
- 赛事报名
  - 在线报名
  - 报名费支付
  - 退赛处理
- 赛程安排
  - 自动分组/对阵
  - 赛程时间表
  - 场地分配

### 3. 比分记录
- 实时比分录入
- 比赛结果确认
- 历史比赛查询
- 数据统计分析

### 4. 积分系统
- 积分规则配置
- 积分实时计算
- 积分排行榜
- 等级晋升机制

## 三、技术方案

### 1. 技术选型
- **前端**
  - Vue3 + Vant UI（移动端H5框架）
  - 一套代码，同时支持H5和小程序
- **后端**
  - SpringBoot（单体应用）
  - MySQL（单数据库）
  - 文件直接存储在服务器

### 2. 开发步骤

#### 2.1 前端快速启动
```bash
# 使用 Vue CLI 创建项目
vue create sports-h5

# 安装 Vant UI
npm i vant

# 配置移动端适配
npm i postcss-px-to-viewport -D
```

#### 2.2 页面开发示例
```vue
<!-- 赛事列表页面 -->
<template>
  <div class="tournament-list">
    <!-- 顶部导航 -->
    <van-nav-bar title="赛事列表" />
    
    <!-- 赛事列表 -->
    <van-list
      v-model:loading="loading"
      :finished="finished"
      @load="onLoad"
    >
      <van-cell v-for="item in list" :key="item.id">
        <template #title>
          <div class="title">{{item.title}}</div>
          <div class="info">
            <span>{{item.startTime}}</span>
            <span>{{item.venue}}</span>
          </div>
        </template>
        <template #right-icon>
          <van-button size="small" @click="onJoin(item)">
            报名
          </van-button>
        </template>
      </van-cell>
    </van-list>
  </div>
</template>
```

#### 2.3 后端精简版
```java
// 主要目录结构
sports-server
  ├── controller    // 接口控制器
  ├── service      // 业务逻辑
  ├── entity      // 数据实体
  ├── mapper      // 数据访问
  └── config      // 基础配置

// 示例代码
@RestController
@RequestMapping("/api/tournament")
public class TournamentController {
    
    @GetMapping("/list")
    public Result list() {
        return Result.success(tournamentService.list());
    }
    
    @PostMapping("/join")
    public Result join(@RequestBody JoinRequest req) {
        tournamentService.join(req);
        return Result.success();
    }
}
```

### 3. 数据库精简设计
```sql
-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    points INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 赛事表
CREATE TABLE tournament (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    start_time DATETIME,
    venue VARCHAR(100),
    max_players INT,
    status TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 报名表
CREATE TABLE registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tournament_id BIGINT,
    user_id BIGINT,
    status TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 4. 开发流程建议

1. **先开发H5版本**
   - 开发和调试更方便
   - 可以在浏览器中预览
   - 更容易分享给他人测试

2. **功能逐步实现**
   - 先实现基础的赛事列表、报名功能
   - 再添加用户登录、积分系统
   - 最后考虑其他扩展功能

3. **测试方法**
   - 使用浏览器开发者工具的手机模式
   - 真机访问H5页面测试
   - postman测试后端接口

### 5. 部署说明

1. **简单部署方案**
```bash
# 前端打包
npm run build
# 将dist目录放到nginx的html目录

# 后端打包
mvn package
# 运行jar包
java -jar sports-server.jar
```

2. **nginx配置示例**
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端页面
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # 后端接口
    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

### 6. 后续优化方向
1. H5转小程序（可使用uni-app重构）
2. 添加文件上传功能
3. 集成微信支付
4. 添加数据统计

## 四、项目实施计划

### 第一阶段：基础框架搭建（已完成）
- [x] 项目初始化
  - [x] 前端项目搭建(Vue3 + Vant)
  - [x] 后端项目搭建(SpringBoot + MyBatis)
  - [x] 数据库环境搭建(MySQL)
- [x] 用户系统开发
  - [x] 基础框架搭建
  - [x] 统一响应处理
  - [x] 全局异常处理
  - [x] 跨域配置
  - [x] API路径统一规范
    - [x] 前端统一使用request.js添加/api前缀
    - [x] 后端Controller统一不使用/api前缀
    - [x] 代理配置统一处理/api前缀
  - [x] 用户登录注册
  - [x] 用户信息管理（含握拍方式和球拍配置）
- [x] 数据库设计
  - [x] 用户表设计
  - [x] 用户认证表设计
  - [x] 用户扩展字段（握拍方式、球拍配置）
- [x] 前端页面开发
  - [x] TabBar 布局
  - [x] 用户登录/注册页面
  - [x] 用户资料查看/编辑页面
  - [x] 积分查询页面
  - [x] 赛事列表页面（基础UI）

### 第二阶段：核心功能开发（进行中）
- [x] 赛事管理模块
  - [x] 设计赛事相关数据表（tournament表、registration表）
  - [x] 实现赛事 CRUD 接口（包含权限控制）
  - [x] 完善赛事列表页面（支持搜索和筛选）
  - [x] 开发赛事创建/编辑表单
  - [x] 实现赛事状态管理
  - [x] 实现赛事报名功能
- [x] 积分系统完善
  - [x] 设计积分规则表(points_rule表)
  - [x] 实现积分计算逻辑
  - [x] 完善积分排行功能
  - [x] 添加等级晋升机制
- [x] 比赛记录模块
  - [x] 设计比赛相关表
    - [x] 比赛阶段表（循环赛/淘汰赛）
    - [x] 分组表（A组/B组）
    - [x] 对阵记录表
  - [x] 实现比赛对阵功能
    - [x] 自动分组/对阵
    - [x] 比分录入和确认
    - [x] 赛程进度管理
  - [ ] 开发比赛历史查询
    - [ ] 个人战绩统计
    - [ ] 对阵记录查询
  - [ ] 数据统计分析
    - [ ] 胜率分析
    - [ ] 技术统计
    - [ ] 排名变化

### 第三阶段：功能完善（待开始）
- [ ] 系统优化
  - [ ] 添加用户认证和授权
  - [ ] 实现文件上传（头像等）
  - [ ] 添加数据缓存
  - [ ] 优化页面性能
- [ ] 其他功能
  - [ ] 消息通知系统
  - [ ] 支付功能
  - [ ] 数据导出功能
  - [ ] 管理员后台

### 下一步计划
1. 完善赛事报名功能
   - 实现报名流程
   - 添加报名限制条件（积分限制、人数限制）
   - 开发报名审核功能
   - 实现报名费用支付

2. 开发积分系统
   - 设计积分规则
   - 实现积分计算
   - 开发排行榜功能

### 开发环境搭建说明

1. **后端环境配置**
```bash
# 数据库配置
- MySQL 8.0+
- 创建数据库: sports_db
- 执行初始化脚本: sports-server/src/main/resources/db/init.sql

# 启动后端服务
cd sports-server
mvn spring-boot:run
```

2. **前端环境配置**
```bash
# 安装依赖
cd sports-h5
npm install

# 启动开发服务器
npm run serve
```

3. **接口测试**
```bash
# 测试用户接口
curl http://localhost:8088/api/user/test
curl http://localhost:8088/api/user/list
```

### 已完成功能
1. 基础框架搭建
   - SpringBoot 基础配置
   - MyBatis 集成
   - 数据库连接池配置(HikariCP)
   - 统一响应处理
   - 全局异常处理
   - 跨域支持

2. 数据库设计
   - 用户表(user)
   - 用户认证表(user_auth)

### 下一步计划
1. 完善用户系统
   - 实现用户注册
   - 实现用户登录
   - 完善用户信息管理

2. 开发赛事管理功能
   - 设计赛事相关表
   - 实现赛事CRUD接口

### 第四阶段：测试优化（2周）
- [ ] 功能测试
- [ ] 性能优化
- [ ] 上线准备

## 五、注意事项

### 1. 安全性考虑
- 用户信息保护
- 支付安全
- 数据备份

### 2. 性能优化
- 首屏加载优化
- 请求响应优化
- 数据缓存策略

### 3. 用户体验
- 界面交互设计
- 操作流程优化
- 错误提示友好化

## 六、后续规划

### 1. 功能扩展
- 引入AI对手推荐
- 添加视频直播功能
- 开发裁判系统

### 2. 运营计划
- 举办线上赛事
- 积分商城
- 广告变现

### 3. 数据应用
- 选手数据分析
- 技术提升建议
- 赛事预测模型

