<#
SpringBoot+Vue3 开发环境一键安装脚本
需要管理员权限运行
winget 安装: JDK17, Maven, MySQL8.0, Node20
#>

# 判断管理员权限
$isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")
if (-not $isAdmin) {
    Write-Host "错误：请右键以【管理员身份】运行此脚本！" -ForegroundColor Red
    pause
    exit 1
}

Write-Host "===== 开始安装开发环境 =====" -ForegroundColor Cyan

# 1.安装 Temurin‑17 JDK
Write-Host "1/4 正在安装 JDK 17 (Eclipse Temurin)"
winget install EclipseAdoptium.Temurin.17.JDK -h

# 2.安装 Maven 3.9
Write-Host "2/4 正在安装 Apache Maven"
winget install Apache.Maven -h

# 3.安装 MySQL 8.0
Write-Host "3/4 正在安装 MySQL 8.0 Community"
winget install Oracle.MySQL.8.0 -h

# 4.安装 Node.js 20 LTS
Write-Host "4/4 正在安装 Node.js 20 LTS"
winget install OpenJS.NodeJS.LTS.20 -h


Write-Host "`n===== 全部软件安装完成！ =====" -ForegroundColor Green
Write-Host "👉 请【重启电脑】，环境变量才能生效！" -ForegroundColor Yellow
pause
