@echo off
setlocal
cd /d "%~dp0"

echo Building fortune app...
if exist *.class del *.class
javac -encoding UTF-8 fortune.java FortuneCardLabel.java FortuneFace.java FortuneMode.java FortuneDrawType.java FortuneResult.java FortuneImageRepository.java FortuneService.java FortuneController.java FortuneFrame.java FortuneHistoryRecord.java FortuneHistoryService.java
if errorlevel 1 (
    echo Build failed.
    pause
    exit /b 1
)

if exist fortune-app.jar del fortune-app.jar
if exist app_build rmdir /s /q app_build
mkdir app_build\test
copy /y *.class app_build\test\ >nul
jar cfe fortune-app.jar test.fortune -C app_build test
if errorlevel 1 (
    echo Package failed.
    pause
    exit /b 1
)
if exist app_build rmdir /s /q app_build

echo Done: %~dp0fortune-app.jar
pause
