# MC Binding Paper 插件

这是一个用于Minecraft Paper服务器的插件，用于实现QQ号与Minecraft账号的绑定功能，与AstrBot框架配合使用。

## 功能特性

- **QQ-Minecraft账号绑定**: 玩家可以将QQ号与Minecraft账号绑定
- **双向验证**: 支持从QQ和Minecraft两端验证绑定状态
- **Websocket通信**: 与AstrBot框架实时通信
- **数据持久化**: 支持JSON文件和MySQL数据库存储

## 安装方法

1. 将编译好的JAR文件放入Minecraft服务器的 `plugins` 目录
2. 重启服务器
3. 配置 `config.yml` 文件（可选）

## 使用命令

- `/bindqq <qq_number>` - 绑定QQ号到当前Minecraft账号
- `/unbindqq` - 解绑QQ号
- `/checkbind` - 查看绑定状态

## 配置说明

配置文件位于 `config.yml`：

```yaml
websocket:
  enabled: true                    # 是否启用Websocket通信
  server_url: "ws://127.0.0.1:8080" # AstrBot Websocket服务器地址
  reconnect_interval: 5000         # 重连间隔(毫秒)
  timeout: 30000                   # 超时时间(毫秒)

database:
  storage_type: "json"             # 存储类型: json 或 mysql
  json_file: "bindings.json"       # JSON文件名
  mysql:                           # MySQL配置
    host: "localhost"
    port: 3306
    database: "mc_binding"
    username: "root"
    password: "password"
    use_ssl: false

commands:
  bindqq:
    enabled: true
    cooldown: 30                   # 绑定命令冷却时间(秒)
  unbindqq:
    enabled: true
  checkbind:
    enabled: true
```

## 与AstrBot集成

此插件设计用于与AstrBot框架配合使用，通过Websocket协议进行通信：

- 玩家在Minecraft中绑定时，会通知AstrBot服务器
- AstrBot可以向Minecraft服务器发送绑定请求
- 支持实时验证绑定状态

## 构建项目

Windows系统：
```cmd
cd paper_plugin
mvn clean package
```

或者运行 `build.bat` 脚本。

## 依赖

- Java 17+
- Maven 3.6+
- Paper 1.20.4 或更高版本
- AstrBot框架（用于QQ机器人功能）

## 开发说明

项目结构：
- `src/main/java/com/example/mcbinding/` - 主插件代码
- `src/main/java/com/example/mcbinding/commands/` - 命令处理
- `src/main/java/com/example/mcbinding/websocket/` - Websocket通信
- `src/main/resources/` - 配置文件和资源

## 故障排除

1. **插件无法加载**: 检查服务器版本是否兼容
2. **Websocket连接失败**: 检查AstrBot服务器是否运行且地址配置正确
3. **命令无效**: 检查权限设置和插件是否正确加载