# MC OneBot 插件 - QQ-Minecraft账号绑定系统

一个完整的QQ-Minecraft账号绑定解决方案，包含AstrBot插件和Paper插件两部分。

## 核心功能

### QQ端绑定（主要功能）
- `/bd <minecraft_username>` - 在QQ群中直接绑定Minecraft账号
- `/unbind` - 解绑Minecraft账号
- `/checkbind` - 查看绑定状态

### 游戏端功能
- `/bindqq <qq_number>` - 在Minecraft中绑定QQ号
- `/unbindqq` - 解绑QQ号
- `/checkbind` - 查看绑定状态

### 服务器管理
- `/mc status` - 查看服务器状态
- `/mc list` - 查看在线玩家
- `/mc say <message>` - 向服务器发送消息
- `/verifybind <qq_id> <minecraft_username>` - 管理员验证绑定

## 使用方法

### 方式一：QQ群绑定（推荐）
1. 玩家在QQ群中发送：`/bd Steve`（假设Minecraft用户名为Steve）
2. 系统自动验证用户名格式
3. 检查是否已存在绑定冲突
4. 绑定成功，数据同步到Minecraft服务器
5. 玩家登录游戏时会收到绑定确认消息

### 方式二：游戏内绑定
1. 玩家在Minecraft中发送：`/bindqq 123456789`（假设QQ号为123456789）
2. 系统验证QQ号格式并检查冲突
3. 绑定成功，数据同步到AstrBot

## 技术架构

- **AstrBot插件**：运行Websocket服务器（端口8080），处理QQ消息
- **Paper插件**：作为Websocket客户端，处理游戏内命令
- **实时同步**：两端数据保持同步，支持双向验证
- **数据存储**：JSON文件存储绑定数据

## 安装方法

1. **AstrBot插件安装**：
   - 将 `mc_onebot.py` 和 `pyproject.toml` 放入AstrBot插件目录
   - 安装依赖：`pip install websockets`

2. **Paper插件安装**：
   - 进入 `paper_plugin` 目录
   - 运行 `mvn clean package` 编译
   - 将生成的JAR文件放入Minecraft服务器 `plugins` 目录

3. 重启AstrBot和Minecraft服务器

## 依赖要求

- **AstrBot**: >= 0.4.0
- **Paper**: 1.20.4 或更高版本
- **Java**: 17 或更高版本
- **Python**: 3.8 或更高版本
- **websockets**: >= 11.0

## 特色功能

- ✅ **一键绑定**：QQ群中发送 `/bd 游戏名` 即可完成绑定
- ✅ **双向验证**：支持QQ和游戏两端验证绑定状态
- ✅ **实时同步**：绑定数据在两端实时同步
- ✅ **冲突检测**：防止重复绑定和冲突绑定
- ✅ **安全验证**：管理员验证命令确保数据准确性
- ✅ **自动通知**：玩家登录时自动提示绑定状态

## 配置说明

插件支持JSON和MySQL两种存储方式，可配置Websocket连接参数，详细配置请查看 `USAGE.md`。

## 开发状态

此插件已完全实现QQ-Minecraft账号绑定功能，支持您要求的在QQ群中发送 `/bd 游戏名字` 进行绑定的核心功能。