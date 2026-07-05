@echo off
cd /d "%~dp0.."
javac -encoding UTF-8 test\fortune.java test\FortuneCardLabel.java test\FortuneFace.java test\FortuneMode.java test\FortuneResult.java test\FortuneImageRepository.java test\FortuneService.java test\FortuneController.java test\FortuneFrame.java test\FortuneHistoryRecord.java test\FortuneHistoryService.java
if errorlevel 1 pause & exit /b 1
java test.fortune
