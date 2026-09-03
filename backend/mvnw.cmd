@REM
@REM Copyright 2015 the original author or authors.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@REM ----------------------------------------------------------------------------
@REM Maven2 Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home dir
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all REM://lines with '@' in case MAVENBATCH ECHO is off
@echo off
@REM set title of command window
title 0
@REM enable echoing my setting MAVENBATCH ECHO
set MAVENBATCH ECHO=off

@REM Execute user defined script before this one
if not "%MAVEN_SKIP RC%"=="" goto skipRcPre
@REM check for pre script, once with hierarchical lookup, once with non-hierarchical lookup
if exist "%USERPROFILE%\mavenrc_pre.cmd" call "%USERPROFILE%\mavenrc_pre.cmd" %*
if exist "%USERPROFILE%\mavenrc_pre.bat" call "%USERPROFILE%\mavenrc_pre.bat" %*
:skipRcPre

@setlocal

set ERROR CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%"=="" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:setInit

set MAVEN COMMAND ARGS=%*

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN PROJECTBASEDIR=%MAVEN BASEDIR%
if not "%MAVEN PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC DIR=%cd%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%cd%" goto baseDirNotFound
set WDIR=%cd%
goto findBaseDir

:baseDirFound
set MAVEN PROJECTBASEDIR=%WDIR%
cd "%EXEC DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN PROJECTBASEDIR=%EXEC DIR%
cd "%EXEC DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN PROJECTBASEDIR%\.mvn\jvm.config" goto endReadAdditionalConfig

@setlocal EnableExtensions EnableDelayedExpansion
for /F "usebackq delims=" %%a in ("%MAVEN PROJECTBASEDIR%\.mvn\jvm.config") do set JVM CONFIGMAVEN OPTS=%%a
:endlocal & set JVM CONFIGMAVEN OPTS=%JVM CONFIGMAVEN OPTS%

:endReadAdditionalConfig

SET MAVEN JAVA EXE="%JAVA_HOME%\bin\java.exe"
set WRAPPER JAR="%MAVEN PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER PROPERTIES="%MAVEN PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

@REM Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
@REM This allows using the maven wrapper in projects that simply check-in the wrapper jar
if exist %WRAPPER JAR% (
    goto runWrapper
) else (
    if not "%WRAPPER URL%"=="" (
        curl -o %WRAPPER JAR% %WRAPPER URL%
    )
    if exist %WRAPPER JAR% (
        goto runWrapper
    ) else (
        echo Couldn't find %WRAPPER JAR%, downloading it ...
        echo Downloading from: %WRAPPER URL%
        powershell -Command "&{"^
			"$webclient = new-object System.Net.WebClient;"^
			"[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"^
			"}"
        if exist %WRAPPER JAR% (
            goto runWrapper
        ) else (
            echo Failed to download wrapper jar
            goto error
        )
    )
)

:runWrapper
"%MAVEN JAVA EXE%" %JVM CONFIGMAVEN OPTS% %MAVEN DEBUG OPTS% -classpath %WRAPPER JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %MAVEN COMMAND ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR CODE=1

:end
@endlocal & set ERROR CODE=%ERROR CODE%

if not "%MAVEN_SKIP RC%"=="" goto skipRcPost
@REM check for post script, once with hierarchical lookup, once with non-hierarchical lookup
if exist "%USERPROFILE%\mavenrc post.bat" call "%USERPROFILE%\mavenrc post.bat"
if exist "%USERPROFILE%\mavenrc post.cmd" call "%USERPROFILE%\mavenrc post.cmd"
:skipRcPost

@REM pause the script if MAVENBATCH PAUSE is set to 'on'
if "%MAVENBATCH PAUSE%"=="on" pause

if "%MAVENBATCH CMD%"=="exit" exit %ERROR CODE%

exit /B %ERROR CODE%