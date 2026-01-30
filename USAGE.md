# MC OneBot 插件使用说明

这是一个完整的QQ-Minecraft账号绑定系统，包含AstrBot插件和Paper插件两部分。

## 功能特性

### AstrBot插件功能
- `/bd <minecraft_username>` - 在QQ群中绑定Minecraft账号
- `/unbind` - 解绑Minecraft账号
- `/checkbind` - 查看绑定状态
- `/verifybind <qq_id> <minecraft_username>` - 管理员验证绑定状态
- `/mc status` - 查看服务器状态
- `/mc list` - 查看在线玩家
- `/mc say <message>` - 向服务器发送消息

### Paper插件功能
- `/bindqq <qq_number>` - 在Minecraft中绑定QQ号
- `/unbindqq` - 解绑QQ号
- `/checkbind` - 查看绑定状态
- 自动检测玩家绑定状态
- 与AstrBot实时通信

## 安装步骤

### 1. AstrBot插件安装
1. 将 `mc_onebot.py` 和 `pyproject.toml` 放入AstrBot插件目录
2. 确保安装了 `websockets` 依赖
3. 重启AstrBot

### 2. Paper插件安装
1. 进入 `paper_plugin` 目录
2. 运行 `mvn clean package` 编译插件
3. 将生成的JAR文件放入Minecraft服务器的 `plugins` 目录
4. 重启服务器

## 配置说明

### AstrBot配置
Websocket服务器默认监听 `localhost:8080`，Paper插件会自动连接。

### Paper插件配置
编辑 `config.yml`：
```yaml
websocket:
  enabled: true                    # 是否启用Websocket通信
  server_url: "ws://127.0.0.1:8080" # AstrBot Websocket服务器地址
  reconnect_interval: 5000         # 重连间隔(毫秒)
  timeout: 30000                   # 超时时间(毫秒)

database:
  storage_type: "json"             # 存储类型: json 或 mysql
  json_file: "bindings.json"       # JSON文件名
```

## 使用流程

### 方式一：从QQ端绑定
1. 玩家在QQ群中发送 `/bd Steve`（假设Minecraft用户名为Steve）
2. 系统验证用户名格式并检查冲突
3. 绑定成功，数据同步到Minecraft服务器
4. 玩家登录Minecraft时会收到绑定确认消息

### 方式二：从游戏端绑定
1. 玩家在Minecraft中发送 `/bindqq 123456789`（假设QQ号为123456789）
2. 系统验证QQ号格式并检查冲突
3. 绑定成功，数据同步到AstrBot
4. 玩家在QQ中可以使用 `/checkbind` 查看状态

## 技术架构

### 双向通信
- AstrBot运行Websocket服务器（端口8080）
- Paper插件作为Websocket客户端连接
- 实时同步绑定状态和验证信息

### 数据存储
- 绑定数据存储在 `mc_bindings.json` 文件中
- 支持JSON和MySQL两种存储方式
- 数据在两端保持同步

### 安全验证
- 防止重复绑定
- 防止冲突绑定
- 管理员验证命令

## 故障排除

1. **Websocket连接失败**：
   - 检查AstrBot是否正常运行
   - 确认端口8080未被占用
   - 检查防火墙设置

2. **命令无效**：
   - 确认插件已正确加载
   - 检查权限设置

3. **绑定冲突**：
   - 确认用户名和QQ号未被其他用户绑定
   - 使用管理员命令清理错误数据

## 开发扩展

项目支持以下扩展功能：
- 添加更多游戏命令
- 集成更多验证方式
- 支持MySQL数据库
- 添加绑定奖励系统
- 集成更多游戏事件

## 依赖要求

- **AstrBot**: >= 0.4.0
- **Paper**: 1.20.4 或更高版本
- **Java**: 17 或更高版本
- **Maven**: 3.6 或更高版本
- **Python**: 3.8 或更高版本