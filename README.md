# 🏙️ 本地化 Urban Discovery 城市探店

> 一个基于 Spring Boot + Redis 构建的高性能本地生活服务平台，支持商户探店 🏪、社区分享 📝、优惠券秒杀 ⚡、附近搜索 📍 等功能。

## ⚠️ 声明

**本项目为个人学习借鉴作品。**

项目原型与核心思路来源于 **黑马程序员** 相关课程（原项目常被称为 "黑马点评 / hmdp"）。本人在学习过程中，基于原项目进行了本地化改造、代码重构与品牌重命名（Urban Discovery 城市探店），仅用于个人技术学习与能力沉淀，**无任何商业用途，也不存在抄袭意图**。

若涉及版权问题，请联系删除或修改。

## 🛠️ 技术栈

- ☕ **后端框架**：Spring Boot 2.3.x
- 🗃️ **持久层**：MyBatis-Plus 3.4.x
- ⚡ **缓存/中间件**：Redis
- 🐬 **数据库**：MySQL 5.7+
- 🌐 **前端反向代理**：Nginx
- 🧰 **工具库**：Hutool, Lombok

## ✨ 核心功能

| 模块 | 说明 |
|------|------|
| 👤 用户服务 | 基于 Redis 的短信验证码登录、注册、Token 刷新机制 |
| 🏪 商户查询 | 商户信息缓存、缓存穿透/雪崩/击穿的防护策略 |
| ⚡ 优惠券秒杀 | 分布式锁 + Redis 预热 + 异步下单，保障高并发场景下的库存一致性 |
| 📝 探店笔记 | 博客发布、点赞、关注 Feed 流推送（推模式） |
| 📍 附近搜索 | 基于 Redis GEO 的周边商户检索 |
| 📅 用户签到 | 基于 Redis BitMap 的签到与连续签到统计 |

## 📁 项目结构

```
urban-discovery/
├── src/main/java/com/urbandiscovery/     # 后端业务代码
├── src/main/resources/
│   ├── application.yml                   # 本地运行时配置（已加入 .gitignore，不上传）
│   ├── application.yml.example           # 配置模板
│   ├── mapper/                           # MyBatis-Plus Mapper XML
│   └── db/urbandiscovery.sql             # 数据库初始化脚本
├── nginx/                                # Nginx 配置与前端页面源码
│   ├── nginx.conf                        # Nginx 反向代理与前端入口配置
│   └── html/urbandiscovery/              # 前端静态页面（HTML / CSS / JS）
├── urbandiscovery-nginx/                 # Windows 下可直接运行的 Nginx 1.18.0 环境
│   └── nginx-1.18.0/
│       ├── conf/nginx.conf               # 已配置好的反向代理规则
│       ├── html/urbandiscovery/          # 前端静态页面
│       ├── logs/                         # 运行日志
│       └── nginx.exe                     # Nginx 可执行文件
├── pom.xml
└── README.md
```

## 🚀 快速开始

### 1. 🧰 环境准备

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+
- Nginx 1.18+（用于前端页面与反向代理）

### 2. 🗄️ 数据库初始化

创建数据库并导入初始数据：

```sql
CREATE DATABASE urban_discovery CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

执行 `src/main/resources/db/urbandiscovery.sql` 初始化表结构与测试数据。

### 3. ⚙️ 后端配置

复制配置模板：

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

根据实际情况修改 `application.yml` 中的数据库连接与 Redis 配置。**请勿将包含真实账号密码的 `application.yml` 提交到仓库。**

### 4. ▶️ 启动后端服务

```bash
mvn spring-boot:run
```

服务默认启动在 `http://localhost:8081` 🎉

### 5. 🌐 部署前端页面（Nginx）

本项目提供两种前端部署方式，任选其一即可：

#### 方式一：使用仓库内置的 Nginx 环境（Windows 推荐）✅

仓库已集成一套配置好的 Nginx 1.18.0 环境，适合 Windows 本地快速启动：

1. 进入 `urbandiscovery-nginx/nginx-1.18.0/` 目录。
2. 启动 Nginx：
   - 双击 `nginx.exe`
   - 或在命令行执行：`start nginx.exe`
3. 访问 `http://localhost:8080`

> 该目录下的 `conf/nginx.conf` 与仓库 `nginx/nginx.conf` 内容一致，已配置好反向代理规则。

#### 方式二：自行安装 Nginx 🔧

1. 下载并解压 [Nginx](https://nginx.org/)。
2. 将本仓库 `nginx/html/urbandiscovery/` 目录复制到 Nginx 安装目录的 `html/` 下。
3. 将本仓库 `nginx/nginx.conf` 覆盖 Nginx 安装目录的 `conf/nginx.conf`（建议先备份原配置）。
4. 启动 Nginx：
   - Windows：`start nginx`
   - Linux / macOS：`sudo nginx`
5. 访问 `http://localhost:8080`

> 前端默认通过 Nginx 反向代理访问后端接口（`location /api` 转发到 `127.0.0.1:8081`）。

## 🔧 配置说明

- `application.yml` 中的 `spring.datasource.url` 默认指向 `urban_discovery` 数据库。
- `SystemConstants.IMAGE_UPLOAD_DIR` 为图片上传目录，部署时请修改为实际 Nginx 静态资源目录。
- 生产环境请务必修改默认端口、数据库密码、Redis 密码等敏感配置，并妥善保管。

## 💡 亮点设计

- **缓存策略**：采用空值缓存、随机 TTL、互斥锁等多重手段解决缓存三件套（穿透、雪崩、击穿）。
- **秒杀优化**：通过 Redis 预减库存、Lua 脚本保证原子性，结合异步消息队列削峰填谷。
- **Session 共享**：基于 Redis 实现分布式登录态，支持 Token 自动续期。
- **Feed 流**：使用 Sorted Set 实现按时间戳排序的关注流推送。

## 📝 学习记录

本项目改造点：

- 将原 `com.hmdp` 包结构重构为 `com.urbandiscovery`。
- 将项目品牌统一为 "Urban Discovery 城市探店"。
- 将数据库、SQL 脚本、前端目录、Nginx 配置等品牌相关命名统一调整。
- 集成可直接运行的 Windows 版 Nginx 1.18.0 环境，降低本地启动门槛。
- 补充前端部署说明与免责声明。

## 📄 许可证

[MIT](LICENSE)
