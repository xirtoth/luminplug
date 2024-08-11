@echo off
REM Define the source JAR file and destination directory
set JAR_FILE=E:\batclient\luminplug\target\luminplug-1.0-SNAPSHOT.jar
set DEST_DIR=C:\Users\henry\batclient\plugins

REM Check if the JAR file exists
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