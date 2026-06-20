# 🏙️ 本地化 Urban Discovery 城市探店

> 一个基于 Spring Boot + Redis 构建的高性能本地生活服务平台，支持商户探店 🏪、社区分享 📝、优惠券秒杀 ⚡、附近搜索 📍 等功能。

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

---

## 🚀 公网部署实战指南

如果你希望把项目部署到云服务器上，让外网可以访问，下面是经过真实部署验证的完整流程。

### 1. 服务器与环境准备

推荐配置（最低可运行）：

| 组件 | 推荐配置 |
|------|---------|
| 服务器 | 1 核 2G 以上，CentOS 7+ / Ubuntu 20.04+ |
| JDK | 1.8+ |
| MySQL | 5.7+ |
| Redis | 5.0+ |
| Nginx | 1.18+ |

安装基础依赖：

```bash
# CentOS
yum install -y java-1.8.0-openjdk-devel nginx mariadb-server redis git

# Ubuntu
apt update
apt install -y openjdk-8-jdk nginx mysql-server redis-server git
```

### 2. 数据库与 Redis 部署

#### 2.1 导入数据库

```bash
mysql -u root -p
```

```sql
CREATE DATABASE urban_discovery CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
EXIT;
```

```bash
mysql -u root -p urban_discovery < src/main/resources/db/urbandiscovery.sql
```

> 💡 建议单独创建一个业务账号，不要直接用 root。

#### 2.2 启动 Redis

```bash
# 启动 Redis
redis-server --daemonize yes

# 建议修改 redis.conf 绑定 0.0.0.0 并设置密码
vim /etc/redis.conf
# 修改：bind 0.0.0.0
# 修改：requirepass 你的密码
```

### 3. 后端打包与部署

#### 3.1 修改生产环境配置

将 `src/main/resources/application.yml.example` 复制为 `application.yml`，并根据服务器实际环境修改：

```yaml
server:
  port: 8081
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://你的服务器IP:3306/urban_discovery?useSSL=false&serverTimezone=UTC
    username: 业务账号
    password: 密码
  redis:
    host: 你的服务器IP
    port: 6379
    password: Redis密码
```

> ⚠️ 如果 MySQL 是 8.0，驱动类名一定要改成 `com.mysql.cj.jdbc.Driver`。

#### 3.2 打包

```bash
mvn clean package -DskipTests
```

打包成功后，jar 包位于 `target/urban-discovery-0.0.1-SNAPSHOT.jar`。

#### 3.3 上传到服务器并启动

```bash
# 上传 jar 到服务器（本地执行）
scp target/urban-discovery-0.0.1-SNAPSHOT.jar root@你的服务器IP:/opt/urban-discovery/

# 服务器上启动
nohup java -jar /opt/urban-discovery/urban-discovery-0.0.1-SNAPSHOT.jar > /opt/urban-discovery/app.log 2>&1 &
```

查看是否启动成功：

```bash
tail -f /opt/urban-discovery/app.log
curl http://127.0.0.1:8081/shop-type/list
```

### 4. Nginx 公网部署

#### 4.1 上传前端资源

```bash
# 把前端页面复制到 Nginx 目录
mkdir -p /usr/share/nginx/html/urbandiscovery
cp -r nginx/html/urbandiscovery/* /usr/share/nginx/html/urbandiscovery/
```

#### 4.2 配置 Nginx

将仓库中的 `nginx/nginx.conf` 上传到 `/etc/nginx/nginx.conf` 或 `/etc/nginx/conf.d/urbandiscovery.conf`。

公网部署时，建议把 `server_name` 改成你的域名或服务器 IP：

```nginx
server {
    listen       80;
    server_name  你的域名或公网IP;

    location / {
        root   /usr/share/nginx/html/urbandiscovery;
        index  index.html index.htm;
    }

    location /api {
        proxy_pass http://127.0.0.1:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

检查配置并重载：

```bash
nginx -t
nginx -s reload
```

### 5. 图片上传目录配置

项目中 `SystemConstants.IMAGE_UPLOAD_DIR` 定义了图片上传目录。公网部署时，必须改成 Nginx 能直接访问的静态资源目录。

例如：

```java
public static final String IMAGE_UPLOAD_DIR = "/usr/share/nginx/html/urbandiscovery/imgs/";
```

然后确保目录存在并授权：

```bash
mkdir -p /usr/share/nginx/html/urbandiscovery/imgs
chmod -R 755 /usr/share/nginx/html/urbandiscovery
```

### 6. 防火墙与安全组

确保以下端口开放：

| 端口 | 用途 |
|------|------|
| 80 | HTTP 访问 |
| 443 | HTTPS（可选） |
| 8081 | 后端接口（建议只开放内网或 localhost） |
| 3306 | MySQL（建议只开放内网） |
| 6379 | Redis（建议只开放内网并设密码） |

云服务器需要在安全组里放行对应端口。

### 7. HTTPS 部署（可选）

如果有域名，建议使用 Nginx 配置 HTTPS：

```nginx
server {
    listen 80;
    server_name 你的域名;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name 你的域名;

    ssl_certificate /path/to/你的证书.crt;
    ssl_certificate_key /path/to/你的私钥.key;

    location / {
        root /usr/share/nginx/html/urbandiscovery;
        index index.html;
    }

    location /api {
        proxy_pass http://127.0.0.1:8081;
    }
}
```

### 8. 进程保活（推荐）

生产环境不要用 `nohup` 裸跑，建议用 `systemd` 管理后端服务。

创建 `/etc/systemd/system/urban-discovery.service`：

```ini
[Unit]
Description=Urban Discovery Backend
After=syslog.target network.target

[Service]
User=root
WorkingDirectory=/opt/urban-discovery
ExecStart=/usr/bin/java -jar /opt/urban-discovery/urban-discovery-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动并设为开机自启：

```bash
systemctl daemon-reload
systemctl start urban-discovery
systemctl enable urban-discovery
```

### 9. 真实部署踩坑记录

- **MySQL 驱动问题**：MySQL 8.0 必须将 driver-class-name 改为 `com.mysql.cj.jdbc.Driver`，并升级 pom.xml 中驱动版本到 8.x。
- **Redis 连接失败**：如果 Redis 配置了密码，application.yml 中一定要加 `password`；如果是云服务器，记得安全组放行 6379。
- **图片上传后访问 404**：99% 是因为 `IMAGE_UPLOAD_DIR` 路径配错了，或者 Nginx 静态目录没权限。
- **Nginx 反向代理 502**：检查后端服务是否真的在 8081 端口启动，以及防火墙是否放行。
- **时区问题**：数据库 URL 建议加上 `serverTimezone=Asia/Shanghai`，避免时间字段偏差。
- **端口被占用**：Windows 上 8080 经常被占用，可以改成 8088 等不常用端口。

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

---

## ⚠️ 声明

**本项目为个人学习借鉴作品。**

项目原型与核心思路来源于 **黑马程序员** 相关课程（原项目常被称为 "黑马点评 / hmdp"）。本人在学习过程中，基于原项目进行了本地化改造、代码重构与品牌重命名（Urban Discovery 城市探店），仅用于个人技术学习与能力沉淀，**无任何商业用途，也不存在抄袭意图**。

若涉及版权问题，请联系删除或修改。
