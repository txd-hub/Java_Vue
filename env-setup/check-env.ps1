<#
.SYNOPSIS
    环境自检脚本 - 检测本机开发环境安装情况
.DESCRIPTION
    检测以下工具是否已安装：
    1. JDK 17
    # 2. Maven 3.8+
    # 3. MySQL 8.0
    # 4. Node.js 18+
    # 输出版本号，打印缺失清单
.NOTES
    无需管理员权限
#>

# 设置控制台编码为UTF-8，解决中文显示问题
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "        开发环境自检工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 初始化缺失工具列表
$missingTools = @()

# 检测JDK 17
Write-Host "1. 检测 JDK 17..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | Select-Object -First 1
    if ($javaVersion -match '"(\d+)\.') {
        $majorVersion = [int]$Matches[1]
        if ($majorVersion -eq 17) {
            Write-Host "   ✓ JDK 17 已安装: $javaVersion" -ForegroundColor Green
        } else {
            Write-Host "   ✗ JDK 版本不是17，当前版本: $javaVersion" -ForegroundColor Red
            $missingTools += "JDK 17 (当前版本: $majorVersion)"
        }
    } else {
        Write-Host "   ✗ 无法解析JDK版本" -ForegroundColor Red
        $missingTools += "JDK 17"
    }
} catch {
    Write-Host "   ✗ JDK 未安装" -ForegroundColor Red
    $missingTools += "JDK 17"
}

# 检测Maven 3.8+
Write-Host "2. 检测 Maven 3.8+..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven" | Select-Object -First 1
    if ($mavenVersion -match "Apache Maven (\d+)\.(\d+)\.(\d+)") {
        $major = [int]$Matches[1]
        $minor = [int]$Matches[2]
        if ($major -gt 3 -or ($major -eq 3 -and $minor -ge 8)) {
            Write-Host "   ✓ Maven $major.$minor.$($Matches[3]) 已安装" -ForegroundColor Green
        } else {
            Write-Host "   ✗ Maven 版本过低，需要3.8+，当前: $major.$minor.$($Matches[3])" -ForegroundColor Red
            $missingTools += "Maven 3.8+ (当前版本: $major.$minor.$($Matches[3]))"
        }
    } else {
        Write-Host "   ✗ 无法解析Maven版本" -ForegroundColor Red
        $missingTools += "Maven 3.8+"
    }
} catch {
    Write-Host "   ✗ Maven 未安装" -ForegroundColor Red
    $missingTools += "Maven 3.8+"
}

# 检测MySQL 8.0
Write-Host "3. 检测 MySQL 8.0..." -ForegroundColor Yellow
try {
    $mysqlVersion = mysql --version 2>&1 | Select-String "MySQL" | Select-Object -First 1
    if ($mysqlVersion -match "MySQL (\d+)\.(\d+)") {
        $major = [int]$Matches[1]
        $minor = [int]$Matches[2]
        if ($major -eq 8 -and $minor -eq 0) {
            Write-Host "   ✓ MySQL 8.0 已安装" -ForegroundColor Green
        } else {
            Write-Host "   ⚠ MySQL 版本不是8.0，当前: $major.$minor (可能兼容)" -ForegroundColor Yellow
        }
    } else {
        Write-Host "   ✗ 无法解析MySQL版本" -ForegroundColor Red
        $missingTools += "MySQL 8.0"
    }
} catch {
    Write-Host "   ✗ MySQL 未安装或未配置环境变量" -ForegroundColor Red
    $missingTools += "MySQL 8.0"
}

# 检测Node.js 18+
Write-Host "4. 检测 Node.js 18+..." -ForegroundColor Yellow
try {
    $nodeVersion = node --version 2>&1
    if ($nodeVersion -match "v(\d+)\.(\d+)\.(\d+)") {
        $major = [int]$Matches[1]
        if ($major -ge 18) {
            Write-Host "   ✓ Node.js $major.$($Matches[2]).$($Matches[3]) 已安装" -ForegroundColor Green
        } else {
            Write-Host "   ✗ Node.js 版本过低，需要18+，当前: $major.$($Matches[2]).$($Matches[3])" -ForegroundColor Red
            $missingTools += "Node.js 18+ (当前版本: $major.$($Matches[2]).$($Matches[3]))"
        }
    } else {
        Write-Host "   ✗ 无法解析Node.js版本" -ForegroundColor Red
        $missingTools += "Node.js 18+"
    }
} catch {
    Write-Host "   ✗ Node.js 未安装" -ForegroundColor Red
    $missingTools += "Node.js 18+"
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "自检结果汇总" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if ($missingTools.Count -eq 0) {
    Write-Host "✅ 所有开发环境已正确安装！" -ForegroundColor Green
    Write-Host ""
    Write-Host "您可以开始项目开发了。" -ForegroundColor Green
} else {
    Write-Host "❌ 以下工具未安装或版本不符合要求：" -ForegroundColor Red
    Write-Host ""
    foreach ($tool in $missingTools) {
        Write-Host "   • $tool" -ForegroundColor Red
    }
    Write-Host ""
    Write-Host "请运行 install-dev-env.ps1 脚本自动安装缺失的开发环境。" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")