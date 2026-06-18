@echo off
cd /d "%~dp0"
if not exist fortune-app.jar (
    call build_app.bat
)
java -jar fortune-app.jar
if errorlevel 1 pause
