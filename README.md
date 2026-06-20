# 🏙️ Urban Discovery 城市探店

> 一个基于 **Spring Boot + Redis** 构建的高性能本地生活服务平台，支持商户探店 🏪、社区分享 📝、优惠券秒杀 ⚡、附近搜索 📍 等功能。

---

## ✨ 项目亮点

- **🛡️ 缓存策略**：采用空值缓存、随机 TTL、互斥锁等多重手段，解决缓存穿透、雪崩、击穿问题。
- **⚡ 秒杀优化**：通过 Redis 预减库存、Lua 脚本保证原子性，结合异步消息队列削峰填谷。
- **🔑 Session 共享**：基于 Redis 实现分布式登录态，支持 Token 自动续期。
- **📰 Feed 流**：使用 Sorted Set 实现按时间戳排序的关注流推送。
- **🌐 开箱即用**：仓库集成 Windows 版 Nginx 1.18.0 与前端静态页面，本地可一键启动。

---

## 🛠️ 技术栈

- ☕ **后端框架**：Spring Boot 2.3.x
- 🗃️ **持久层**：MyBatis-Plus 3.4.x
- ⚡ **缓存/中间件**：Redis
- 🐬 **数据库**：MySQL 5.7+
- 🌐 **前端反向代理**：Nginx
- 🧰 **工具库**：Hutool, Lombok

---

## 📦 核心功能

| 模块 | 说明 |
|------|------|
| 👤 用户服务 | 基于 Redis 的短信验证码登录、注册、Token 刷新机制 |
| 🏪 商户查询 | 商户信息缓存、缓存穿透/雪崩/击穿的防护策略 |
| ⚡ 优惠券秒杀 | 分布式锁 + Redis 预热 + 异步下单，保障高并发场景下的库存一致性 |
| 📝 探店笔记 | 博客发布、点赞、关注 Feed 流推送（推模式） |
| 📍 附近搜索 | 基于 Redis GEO 的周边商户检索 |
| 📅 用户签到 | 基于 Redis BitMap 的签到与连续签到统计 |

---

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
│       ├── logs/                         # 运行日志（不上传仓库）
│       └── nginx.exe                     # Nginx 可执行文件
├── pom.xml
└── README.md
```

---

## 👋 写给新手

如果你以前只写过 Java 课设，没有真正部署过项目，别担心。本文档后半部分会把每一步都拆细，包括：

- 本地 Windows 上一键跑起来
- 买一台云服务器后部署到公网
- 常见问题怎么排查

**建议先本地跑通，再尝试公网部署。** 本地跑通说明代码没问题，公网部署只是换一台服务器重复操作。

---

## 🚀 快速开始（本地 Windows）

### 第 0 步：确认电脑已安装以下软件

| 软件 | 最低版本 | 下载/说明 |
|------|---------|----------|
| JDK | 1.8 | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 或 [Adoptium](https://adoptium.net/) |
| Maven | 3.6+ | [Maven 官网](https://maven.apache.org/download.cgi) |
| MySQL | 5.7+ | [MySQL 官网](https://dev.mysql.com/downloads/installer/) |
| Redis | 5.0+ | [Redis Windows 版](https://github.com/tporadowski/redis/releases) |

> 💡 不知道自己装没装？在命令行输入 `java -version`、`mvn -v`、`mysql --version`、`redis-cli --version`，能显示版本号就是装好了。

### 第 1 步：下载本项目

```bash
# 如果你已经安装了 Git
git clone https://github.com/caolihong666/urban-discovery.git

# 如果没装 Git，直接点 GitHub 网页上的绿色 Code -> Download ZIP 也行
```

下载后用 IDEA 打开 `urban-discovery` 目录。

### 第 2 步：创建数据库

打开 MySQL 客户端（比如 Navicat、DBeaver，或命令行），执行：

```sql
CREATE DATABASE urban_discovery CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

然后导入初始数据：

```bash
# 在 urban-discovery 项目根目录执行
mysql -u root -p urban_discovery < src/main/resources/db/urbandiscovery.sql
```

执行时会提示你输入数据库密码，输入后回车即可。

> 💡 如果提示 `mysql 不是内部或外部命令`，说明 MySQL 的 bin 目录没加到环境变量。可以直接把 SQL 文件拖到 Navicat 里执行。

### 第 3 步：配置后端

1. 找到 `src/main/resources/application.yml.example`
2. 复制一份，重命名为 `application.yml`
3. 打开 `application.yml`，修改下面几项：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/urban_discovery?useSSL=false&serverTimezone=Asia/Shanghai
    username: root              # 改成你的数据库用户名
    password: 你的数据库密码     # 改成你的数据库密码
  redis:
    host: localhost
    port: 6379
    # 如果你的 Redis 没有密码，这行可以删掉
    # password: 
```

> ⚠️ **重要**：`application.yml` 已经加入 `.gitignore`，不会上传到 GitHub。所以你可以放心写真实密码。但千万不要手动把它 `git add` 进去。

### 第 4 步：启动 Redis

Windows 下启动 Redis：

```bash
redis-server.exe redis.windows.conf
```

看到 `Ready to accept connections` 就是成功了。

### 第 5 步：启动后端

在 IDEA 里找到 `UrbanDiscoveryApplication.java`，右键 → Run。

或者命令行：

```bash
mvn spring-boot:run
```

看到类似 `Started UrbanDiscoveryApplication in x.x seconds` 就是启动成功。

测试一下接口：

```bash
curl http://localhost:8081/shop-type/list
```

如果有 JSON 数据返回，说明后端正常。

### 第 6 步：启动前端

#### 方式一：使用仓库内置的 Nginx（推荐）✅

1. 打开文件夹 `urbandiscovery-nginx/nginx-1.18.0/`
2. 双击 `nginx.exe`
3. 浏览器访问 `http://localhost:8080`

> 如果 8080 端口被占用，打开 `conf/nginx.conf`，把 `listen 8080;` 改成 `listen 8088;`，保存后重新启动 Nginx。

#### 方式二：自己安装 Nginx

1. 下载 [Nginx](https://nginx.org/en/download.html) 并解压
2. 把本仓库 `nginx/html/urbandiscovery/` 复制到 Nginx 的 `html/` 目录下
3. 用本仓库 `nginx/nginx.conf` 覆盖 Nginx 的 `conf/nginx.conf`
4. 命令行执行 `start nginx`
5. 访问 `http://localhost:8080`

---

## 🌍 公网部署实战指南（云服务器版）

### 0. 你需要准备什么？

- 一台云服务器（阿里云 / 腾讯云 / 华为云都可以），系统推荐 **CentOS 7+** 或 **Ubuntu 20.04+**
- 一个域名（可选，但强烈建议配一个，HTTPS 更安全）
- 会基础的 Linux 命令

### 1. 购买服务器后的第一件事

拿到服务器后，记录三个信息：

- **公网 IP**：例如 `123.45.67.89`
- **用户名**：通常是 `root`
- **密码或密钥**：用于远程登录

用 SSH 工具连接服务器：

```bash
# Windows 可以用 PowerShell、Xshell、FinalShell
ssh root@123.45.67.89
```

### 2. 安装基础环境

连接上服务器后，按你的系统选择命令：

#### CentOS

```bash
yum update -y
yum install -y java-1.8.0-openjdk-devel nginx mariadb-server redis git
```

#### Ubuntu

```bash
apt update
apt install -y openjdk-8-jdk nginx mysql-server redis-server git
```

### 3. 启动 MySQL / Redis

#### CentOS

```bash
systemctl start mysqld
systemctl enable mysqld
systemctl start redis
systemctl enable redis
```

#### Ubuntu

```bash
systemctl start mysql
systemctl enable mysql
systemctl start redis
systemctl enable redis
```

### 4. 导入数据库

```bash
# 进入 MySQL
mysql -u root -p
```

```sql
CREATE DATABASE urban_discovery CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER 'urban'@'%' IDENTIFIED BY '你的密码';
GRANT ALL PRIVILEGES ON urban_discovery.* TO 'urban'@'%';
FLUSH PRIVILEGES;
EXIT;
```

把项目里的 SQL 文件上传到服务器：

```bash
# 本地执行
scp src/main/resources/db/urbandiscovery.sql root@123.45.67.89:/root/
```

然后导入：

```bash
# 服务器上执行
mysql -u urban -p urban_discovery < /root/urbandiscovery.sql
```

### 5. 给 Redis 设置密码

```bash
vim /etc/redis.conf
```

找到并修改：

```
# bind 127.0.0.1 改为下面这行，允许外网访问（可选，看需求）
bind 0.0.0.0

# 取消注释并设置密码
requirepass 你的Redis密码
```

保存后重启：

```bash
systemctl restart redis
```

> ⚠️ 如果不需要外网访问 Redis，建议只监听 `127.0.0.1`，并设置强密码。

### 6. 打包后端项目

在本地项目根目录执行：

```bash
mvn clean package -DskipTests
```

打包成功后，jar 包在：

```
target/urban-discovery-0.0.1-SNAPSHOT.jar
```

### 7. 上传 jar 包到服务器

在服务器上创建目录：

```bash
mkdir -p /opt/urban-discovery
```

本地执行上传：

```bash
scp target/urban-discovery-0.0.1-SNAPSHOT.jar root@123.45.67.89:/opt/urban-discovery/
```

### 8. 修改生产环境配置

在服务器上创建 `application.yml`：

```bash
vim /opt/urban-discovery/application.yml
```

内容如下：

```yaml
server:
  port: 8081
spring:
  application:
    name: urban-discovery
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/urban_discovery?useSSL=false&serverTimezone=Asia/Shanghai
    username: urban
    password: 你的数据库密码
  redis:
    host: 127.0.0.1
    port: 6379
    password: 你的Redis密码
  jackson:
    default-property-inclusion: non_null
mybatis-plus:
  type-aliases-package: com.urbandiscovery.entity
logging:
  level:
    com.urbandiscovery: debug
```

> ⚠️ MySQL 8.0 一定要用 `com.mysql.cj.jdbc.Driver`，否则启动会报错。

### 9. 启动后端服务

简单启动（测试用）：

```bash
cd /opt/urban-discovery
nohup java -jar urban-discovery-0.0.1-SNAPSHOT.jar --spring.config.location=application.yml > app.log 2>&1 &
```

查看日志：

```bash
tail -f /opt/urban-discovery/app.log
```

测试接口：

```bash
curl http://127.0.0.1:8081/shop-type/list
```

### 10. 配置 Nginx（让外网能访问前端）

#### 10.1 上传前端页面

本地执行：

```bash
# 先把前端页面上传到服务器
scp -r nginx/html/urbandiscovery root@123.45.67.89:/opt/
```

服务器上移动到 Nginx 目录：

```bash
mkdir -p /usr/share/nginx/html/urbandiscovery
cp -r /opt/urbandiscovery/* /usr/share/nginx/html/urbandiscovery/
```

#### 10.2 修改 Nginx 配置

```bash
vim /etc/nginx/conf.d/urbandiscovery.conf
```

写入：

```nginx
server {
    listen       80;
    server_name  你的域名或公网IP;

    location / {
        root   /usr/share/nginx/html/urbandiscovery;
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://127.0.0.1:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```

检查并重载：

```bash
nginx -t
nginx -s reload
```

### 11. 配置图片上传目录

找到代码中的 `SystemConstants.IMAGE_UPLOAD_DIR`，改成服务器上的路径：

```java
public static final String IMAGE_UPLOAD_DIR = "/usr/share/nginx/html/urbandiscovery/imgs/";
```

然后重新打包上传。

服务器上创建目录并授权：

```bash
mkdir -p /usr/share/nginx/html/urbandiscovery/imgs
chmod -R 755 /usr/share/nginx/html/urbandiscovery
```

### 12. 开放端口

云服务器需要在控制台的安全组里放行：

| 端口 | 说明 |
|------|------|
| 80 | HTTP |
| 443 | HTTPS（可选） |
| 8081 | 后端接口（建议只开放内网或 localhost） |

同时关闭防火墙或放行：

```bash
# CentOS
firewall-cmd --zone=public --add-port=80/tcp --permanent
firewall-cmd --zone=public --add-port=443/tcp --permanent
firewall-cmd --reload

# Ubuntu
ufw allow 80
ufw allow 443
ufw reload
```

### 13. 配置 HTTPS（可选但推荐）

如果你有域名，可以申请免费 SSL 证书（阿里云 / 腾讯云 / Let's Encrypt）。

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

### 14. 设置后端开机自启

创建 systemd 服务：

```bash
vim /etc/systemd/system/urban-discovery.service
```

写入：

```ini
[Unit]
Description=Urban Discovery Backend
After=syslog.target network.target

[Service]
User=root
WorkingDirectory=/opt/urban-discovery
ExecStart=/usr/bin/java -jar /opt/urban-discovery/urban-discovery-0.0.1-SNAPSHOT.jar --spring.config.location=/opt/urban-discovery/application.yml
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启用并启动：

```bash
systemctl daemon-reload
systemctl start urban-discovery
systemctl enable urban-discovery
```

常用命令：

```bash
systemctl status urban-discovery   # 查看状态
systemctl stop urban-discovery     # 停止
systemctl restart urban-discovery  # 重启
```

---

## ❓ 常见问题排查

### Q1：后端启动报错 `Communications link failure`

**原因**：连不上 MySQL。

**解决**：
- 检查 MySQL 是否启动：`systemctl status mysqld` 或 `systemctl status mysql`
- 检查用户名密码是否正确
- 检查数据库名是否为 `urban_discovery`

### Q2：后端启动报错 `Could not create connection to database server`

**原因**：MySQL 8.0 驱动不匹配。

**解决**：把 `driver-class-name` 改成 `com.mysql.cj.jdbc.Driver`，并把 `pom.xml` 中 MySQL 驱动版本升级到 8.x：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### Q3：前端页面空白或 404

**原因**：Nginx 没找到前端文件，或路径不对。

**解决**：
- 检查 `root` 指向的目录是否存在
- 检查 `nginx.conf` 是否已重载：`nginx -s reload`

### Q4：前端能打开，但接口报 502

**原因**：Nginx 连不上后端。

**解决**：
- 检查后端是否在 8081 端口运行：`curl http://127.0.0.1:8081/shop-type/list`
- 检查 Nginx 配置里的 `proxy_pass` 是否正确

### Q5：图片上传成功，但访问 404

**原因**：`IMAGE_UPLOAD_DIR` 路径没改。

**解决**：修改 `SystemConstants.IMAGE_UPLOAD_DIR` 为服务器上 Nginx 静态目录的真实路径，重新打包部署。

### Q6：Windows 上 8080 端口被占用

```bash
# 查看占用 8080 的进程
netstat -ano | findstr :8080

# 结束进程（PID 替换成上面的数字）
taskkill /PID 进程号 /F
```

---

## 🔐 安全与隐私提醒

### 1. 敏感文件不要提交

以下文件已加入 `.gitignore`，默认不会上传：

- `src/main/resources/application.yml`
- `src/main/resources/application-*.yml`
- `src/main/resources/application-*.properties`
- `*.env`
- Nginx 日志文件

**如果你手动 `git add` 过这些文件，请立即停止并检查。**

### 2. 生产环境密码要复杂

- 数据库密码不要用 `123456`、`root` 这种
- Redis 一定要设置密码
- 不要用 root 账号跑应用，建议单独创建业务账号

### 3. 端口暴露原则

| 端口 | 建议 |
|------|------|
| 80 / 443 | 必须开放 |
| 8081 | 只让 Nginx 访问，外网不要直接暴露 |
| 3306 | 不要对外开放 |
| 6379 | 不要对外开放 |

### 4. 日志中可能有隐私信息

Nginx 日志会记录用户 IP、手机号、请求参数等，**不要上传到 GitHub**。本项目已把 `urbandiscovery-nginx/nginx-1.18.0/logs/` 加入 `.gitignore`。

---

## 🔧 配置说明

- `application.yml` 中的 `spring.datasource.url` 默认指向 `urban_discovery` 数据库。
- `SystemConstants.IMAGE_UPLOAD_DIR` 为图片上传目录，部署时请修改为实际 Nginx 静态资源目录。
- 生产环境请务必修改默认端口、数据库密码、Redis 密码等敏感配置，并妥善保管。

---

## 📝 学习记录

本项目改造点：

- 将原 `com.hmdp` 包结构重构为 `com.urbandiscovery`。
- 将项目品牌统一为 "Urban Discovery 城市探店"。
- 将数据库、SQL 脚本、前端目录、Nginx 配置等品牌相关命名统一调整。
- 集成可直接运行的 Windows 版 Nginx 1.18.0 环境，降低本地启动门槛。
- 补充前端部署说明、公网部署指南与安全隐私提醒。

---

## 📄 许可证

[MIT](LICENSE)

---

## ⚠️ 声明

**本项目为个人学习借鉴作品。**

项目原型与核心思路来源于 **黑马程序员** 相关课程（原项目常被称为 "黑马点评 / hmdp"）。本人在学习过程中，基于原项目进行了本地化改造、代码重构与品牌重命名（Urban Discovery 城市探店），仅用于个人技术学习与能力沉淀，**无任何商业用途，也不存在抄袭意图**。

若涉及版权问题，请联系删除或修改。
