# 本地化 Urban Discovery 城市探店

> 一个基于 Spring Boot + Redis 构建的高性能本地生活服务平台，支持商户探店、社区分享、优惠券秒杀、附近搜索等功能。

## 技术栈

- **后端框架**：Spring Boot 2.3.x
- **持久层**：MyBatis-Plus 3.4.x
- **缓存/中间件**：Redis
- **数据库**：MySQL 5.7+
- **工具库**：Hutool, Lombok

## 核心功能

| 模块 | 说明 |
|------|------|
| 用户服务 | 基于 Redis 的短信验证码登录、注册、Token 刷新机制 |
| 商户查询 | 商户信息缓存、缓存穿透/雪崩/击穿的防护策略 |
| 优惠券秒杀 | 分布式锁 + Redis 预热 + 异步下单，保障高并发场景下的库存一致性 |
| 探店笔记 | 博客发布、点赞、关注 Feed 流推送（推模式） |
| 附近搜索 | 基于 Redis GEO 的周边商户检索 |
| 用户签到 | 基于 Redis BitMap 的签到与连续签到统计 |

## 项目结构

```
urban-discovery/
├── src/main/java/com/urbandiscovery/          # 业务代码
├── src/main/resources/
│   ├── application.yml               # 本地运行时配置（已加入 .gitignore，不上传）
│   ├── application.yml.example       # 配置模板
│   └── db/urbandiscovery.sql                   # 数据库初始化脚本
├── pom.xml
└── README.md
```

## 快速开始

### 1. 环境准备

- JDK 1.8+
- MySQL 5.7+
- Redis 5.0+

### 2. 数据库初始化

创建数据库并导入初始数据：

```sql
CREATE DATABASE urban_discovery CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

执行 `src/main/resources/db/urbandiscovery.sql` 初始化表结构与测试数据。

### 3. 配置文件

复制配置模板：

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

根据实际情况修改 `application.yml` 中的数据库连接与 Redis 配置。

### 4. 运行

```bash
mvn spring-boot:run
```

服务默认启动在 `http://localhost:8081`

## 亮点设计

- **缓存策略**：采用空值缓存、随机 TTL、互斥锁等多重手段解决缓存三件套（穿透、雪崩、击穿）。
- **秒杀优化**：通过 Redis 预减库存、Lua 脚本保证原子性，结合异步消息队列削峰填谷。
- **Session 共享**：基于 Redis 实现分布式登录态，支持 Token 自动续期。
- **Feed 流**：使用 Sorted Set 实现按时间戳排序的关注流推送。

## 许可证

[MIT](LICENSE)
