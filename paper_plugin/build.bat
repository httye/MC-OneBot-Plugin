@echo off
echo 正在构建 MC Binding Paper 插件...

REM 检查是否安装了Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven。请确保Maven已安装并添加到PATH环境变量中。
    echo 你可以从 https://maven.apache.org/download.cgi 下载Maven
    exit /b 1
)

REM 检查是否安装了Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java。请确保Java已安装并添加到PATH环境变量中。
    exit /b 1
)

echo 正在清理之前的构建...
mvn clean

echo 正在编译项目...
mvn compile

echo 正在打包项目...
mvn package

if %errorlevel% equ 0 (
    echo.
    echo 构建成功！
    echo 插件文件已生成在 target/ 目录中
    echo.
    dir target\*.jar
) else (
    echo.
    echo 构建失败！
    pause
    exit /b 1
)

echo.
echo 按任意键继续...
pause >nul