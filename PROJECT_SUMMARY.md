# MC OneBot 插件项目总结

## 项目概述
这是一个完整的QQ-Minecraft账号绑定系统，包含AstrBot插件和Paper插件两部分，实现了在QQ群中发送 `/bd 我的世界游戏名字` 即可绑定账号的核心功能。

## 核心功能

### 1. AstrBot插件（QQ端）
- **绑定命令**: `/bd <minecraft_username>` - 在QQ群中直接绑定Minecraft账号
- **解绑命令**: `/unbind` - 解绑Minecraft账号  
- **查询命令**: `/checkbind` - 查看绑定状态
- **管理命令**: `/verifybind <qq_id> <minecraft_username>` - 管理员验证绑定
- **服务器命令**: `/mc status/list/say` - Minecraft服务器相关功能

### 2. Paper插件（游戏端）
- **绑定命令**: `/bindqq <qq_number>` - 在Minecraft中绑定QQ号
- **解绑命令**: `/unbindqq` - 解绑QQ号
- **查询命令**: `/checkbind` - 查看绑定状态
- **自动提示**: 玩家登录时自动提示绑定状态

## 技术架构

### 双向通信系统
- **AstrBot**: 运行Websocket服务器（端口8080）
- **Paper**: 作为Websocket客户端连接
- **实时同步**: 绑定数据在两端保持同步

### 数据存储
- **本地存储**: JSON文件存储绑定数据
- **扩展支持**: 可配置MySQL数据库存储
- **数据同步**: 两端数据实时同步

## 项目结构

```
mc_onebot/
├── mc_onebot.py              # AstrBot插件主文件
├── pyproject.toml           # Python项目配置
├── config.json              # AstrBot配置文件
├── README.md                # 项目说明
├── USAGE.md                 # 使用说明
├── setup.bat                # 快速部署脚本
├── .gitignore               # Git忽略文件
├── PROJECT_SUMMARY.md       # 项目总结
├── .github/workflows/       # GitHub Actions工作流
│   └── build.yml            # 自动编译配置
└── paper_plugin/            # Paper插件目录
    ├── pom.xml              # Maven配置
    ├── build.bat            # Windows构建脚本
    ├── README.md            # Paper插件说明
    ├── src/main/java/       # Java源代码
    │   └── com/example/mcbinding/
    │       ├── MCBindingPlugin.java           # 主插件类
    │       ├── commands/                      # 命令处理
    │       │   ├── BindQQCommand.java
    │       │   ├── UnbindQQCommand.java
    │       │   └── CheckBindCommand.java
    │       └── websocket/                     # Websocket通信
    │           └── WebSocketClient.java
    └── src/main/resources/
        ├── plugin.yml       # Paper插件配置
        └── config.yml       # 插件配置文件
```

## 部署方式

### 方式一：手动部署
1. 将 `mc_onebot.py` 和 `pyproject.toml` 放入AstrBot插件目录
2. 进入 `paper_plugin` 目录，运行 `mvn clean package` 编译
3. 将生成的JAR文件放入Minecraft服务器 `plugins` 目录
4. 重启AstrBot和Minecraft服务器

### 方式二：GitHub Actions自动编译
- 推送代码到GitHub仓库，自动触发编译
- 生成的插件文件可在Actions Artifacts中下载

## 使用流程

### 主要使用方式（QQ群绑定）
1. 玩家在QQ群中发送：`/bd Steve`（假设Minecraft用户名为Steve）
2. 系统自动验证用户名格式（3-16字符，字母数字下划线）
3. 检查是否已存在绑定冲突（QQ已绑定或游戏名被其他QQ绑定）
4. 绑定成功，数据同步到Minecraft服务器
5. 玩家登录游戏时会收到"你的QQ号已成功绑定到此Minecraft账号"的消息

### 辅助使用方式（游戏内绑定）
1. 玩家在Minecraft中发送：`/bindqq 123456789`
2. 系统验证QQ号格式并检查冲突
3. 绑定成功，数据同步到AstrBot

## 安全特性

- **格式验证**: 严格验证Minecraft用户名和QQ号格式
- **冲突检测**: 防止重复绑定和冲突绑定
- **权限控制**: 管理员命令权限验证
- **实时同步**: 两端数据一致性保证

## 依赖要求

- **AstrBot**: >= 0.4.0
- **Paper**: 1.20.4 或更高版本  
- **Java**: 17 或更高版本
- **Python**: 3.8 或更高版本
- **websockets**: >= 11.0

## 项目特色

✅ **一键绑定**: QQ群中发送 `/bd 游戏名` 即可完成绑定  
✅ **双向验证**: 支持QQ和游戏两端验证绑定状态  
✅ **实时同步**: 绑定数据在两端实时同步  
✅ **冲突检测**: 防止重复绑定和冲突绑定  
✅ **安全验证**: 管理员验证命令确保数据准确性  
✅ **自动通知**: 玩家登录时自动提示绑定状态  
✅ **自动编译**: GitHub Actions支持自动编译部署  
✅ **完整文档**: 详细的使用说明和配置文档

## 核心实现

项目完全实现了您要求的功能：用户只需要在QQ群中发送 `/bd 我的世界游戏名字` 即可完成绑定，无需在游戏内进行任何操作。AstrBot插件会处理QQ消息，验证格式，检查冲突，执行绑定，并通过Websocket通知Minecraft服务器，实现完整的绑定流程。