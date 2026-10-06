@echo off
setlocal

cd /d "%~dp0.."

echo Compiling grindstone-core...
call mvn -q -pl grindstone-core compile
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)

set "MAIN_CLASS=io.github.tiagofar78.grindstone.games.blindtag.debug.MapViewer"

:loop
cls
java -cp grindstone-core\target\classes %MAIN_CLASS%
echo.
choice /n /c yq /m "Press Y for a new map, Q to quit"
if errorlevel 2 goto :end
goto :loop

:end
