<#
.SYNOPSIS
    Maven阿里云镜像配置脚本 - 解决Maven下载依赖慢问题
.DESCRIPTION
    配置阿里云镜像仓库，加速Maven依赖下载
    自动备份原始settings.xml文件
.NOTES
    无需管理员权限
#>

# 设置控制台编码为UTF-8，解决中文显示问题
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    Maven阿里云镜像配置工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查Maven是否已安装
Write-Host "正在检查Maven安装情况..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven" | Select-Object -First 1
    if ($mavenVersion -match "Apache Maven (\d+)\.(\d+)\.(\d+)") {
        Write-Host "✓ Maven 已安装: $mavenVersion" -ForegroundColor Green
    } else {
        Write-Host "✗ Maven 未安装或未配置环境变量" -ForegroundColor Red
        Write-Host "请先运行 install-dev-env.ps1 安装Maven" -ForegroundColor Yellow
        Read-Host "按回车键退出"
        exit 1
    }
} catch {
    Write-Host "✗ Maven 未安装或未配置环境变量" -ForegroundColor Red
    Write-Host "请先运行 install-dev-env.ps1 安装Maven" -ForegroundColor Yellow
    Read-Host "按回车键退出"
    exit 1
}

# 获取用户目录下的.m2文件夹路径
$m2Dir = Join-Path $env:USERPROFILE ".m2"
$settingsFile = Join-Path $m2Dir "settings.xml"

# 创建.m2目录（如果不存在）
if (-not (Test-Path $m2Dir)) {
    Write-Host "创建.m2目录: $m2Dir" -ForegroundColor Gray
    New-Item -ItemType Directory -Path $m2Dir -Force | Out-Null
}

# 备份原始settings.xml（如果存在）
if (Test-Path $settingsFile) {
    $backupFile = Join-Path $m2Dir "settings.xml.backup.$(Get-Date -Format 'yyyyMMdd_HHmmss')"
    Write-Host "备份原始settings.xml到: $backupFile" -ForegroundColor Gray
    Copy-Item -Path $settingsFile -Destination $backupFile -Force
}

# 创建阿里云镜像配置的settings.xml
$settingsContent = @"
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 http://maven.apache.org/xsd/settings-1.2.0.xsd">
  
  <!-- 本地仓库路径（默认在用户目录下的.m2/repository） -->
  <localRepository>${m2Dir}\repository</localRepository>
  
  <!-- 阿里云镜像配置，加速依赖下载 -->
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
  
  <!-- 配置开发环境 -->
  <profiles>
    <profile>
      <id>default</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://maven.aliyun.com/repository/central</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  
  <!-- 激活默认配置 -->
  <activeProfiles>
    <activeProfile>default</activeProfile>
  </activeProfiles>
  
</settings>
"@

# 写入settings.xml文件
Write-Host "正在配置阿里云镜像..." -ForegroundColor Yellow
$settingsContent | Out-File -FilePath $settingsFile -Encoding UTF8

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "配置完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✓ 阿里云镜像已配置成功" -ForegroundColor Green
Write-Host "✓ 配置文件位置: $settingsFile" -ForegroundColor Green
Write-Host ""
Write-Host "配置详情：" -ForegroundColor Yellow
Write-Host "  - 镜像源: 阿里云公共仓库" -ForegroundColor White
Write-Host "  - 本地仓库: $m2Dir\repository" -ForegroundColor White
Write-Host ""
Write-Host "后续操作：" -ForegroundColor Yellow
Write-Host "  1. 重启IDE（如IntelliJ IDEA、VS Code等）" -ForegroundColor White
Write-Host "  2. 执行 mvn clean install 测试是否生效" -ForegroundColor White
Write-Host ""
Read-Host "按回车键退出"