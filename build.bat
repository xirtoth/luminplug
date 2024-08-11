@echo off
REM Navigate to the project directory
echo Navigating to project directory...
cd /d E:\batclient\luminplug

REM Clean and build the project
echo Running mvn clean install...
mvn clean install

REM Check if the build was successful
if %errorlevel% neq 0 (
    echo Build failed
    exit /b %errorlevel%
)

REM Copy the JAR file to the plugins directory
set JAR_FILE=target\luminplug-1.0-SNAPSHOT.jar
set DEST_DIR=C:\Users\henry\batclient\plugins

echo Checking if JAR file exists...
if exist %JAR_FILE% (
    echo Copying JAR file to %DEST_DIR%...
    copy %JAR_FILE% %DEST_DIR%
    echo JAR file copied to %DEST_DIR%
) else (
    echo JAR file not found
    exit /b 1
)

REM Start the BatMUD program
set BATMUD_PATH=D:\SteamLibrary\steamapps\common\BatMUD\BatMUD.exe

echo Checking if BatMUD executable exists...
if exist %BATMUD_PATH% (
    echo Starting BatMUD...
    start "" "%BATMUD_PATH%"
    echo BatMUD started
) else (
    echo BatMUD executable not found
    exit /b 1
)

echo Script completed successfully
call apply.bat