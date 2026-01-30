@echo off
echo.
echo ==========================================
echo   MC OneBot 插件快速部署脚本
echo ==========================================
echo.

REM 检查Java
echo 检查Java环境...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java。请先安装Java 17+
    pause
    exit /b 1
)

REM 检查Maven
echo 检查Maven环境...
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo 警告: 未找到Maven，Paper插件将无法编译
    echo 请从 https://maven.apache.org/download.cgi 下载并安装Maven
    set MAVEN_MISSING=1
)

REM 检查Python
echo 检查Python环境...
where python >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Python。请先安装Python 3.8+
    pause
    exit /b 1
)

if "%MAVEN_MISSING%"=="1" (
    echo.
    echo ==========================================
    echo   AstrBot插件部署
    echo ==========================================
    echo 将以下文件复制到AstrBot插件目录:
    echo - mc_onebot.py
    echo - pyproject.toml
    echo.
    echo 安装依赖:
    echo pip install websockets
    echo.
) else (
    echo.
    echo ==========================================
    echo   编译Paper插件
    echo ==========================================
    cd paper_plugin
    echo 正在编译Paper插件...
    mvn clean package
    if %errorlevel% equ 0 (
        echo.
        echo Paper插件编译成功！
        echo 插件文件位置: paper_plugin/target/mcbinding-1.0.0.jar
        echo.
    ) else (
        echo.
        echo Paper插件编译失败！
        pause
        exit /b 1
    )
    cd ..
)

echo ==========================================
echo   部署完成！
echo ==========================================
echo.
echo 使用说明:
echo 1. 将mc_onebot.py和pyproject.toml放入AstrBot插件目录
echo 2. 将编译好的JAR文件放入Minecraft服务器plugins目录  
echo 3. 重启AstrBot和Minecraft服务器
echo 4. 查看USAGE.md了解详细使用方法
echo.
echo 项目结构:
echo - AstrBot插件: mc_onebot.py (支持/bd命令)
echo - Paper插件: paper_plugin/ (支持/bindqq命令)
echo - 配置文件: config.json, paper_plugin/config.yml
echo - 绑定数据: mc_bindings.json
echo.
echo 按任意键退出...
pause >nul