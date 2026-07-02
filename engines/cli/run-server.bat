@echo off
setlocal

for %%F in ("target\grindstone-cli-engine-*.jar") do set CLI_JAR=%%~fF
for %%F in ("..\..\grindstone-core\target\grindstone-core-*.jar") do set CORE_JAR=%%~fF

if not defined CLI_JAR (
    echo Could not find CLI engine JAR.
	pause
    exit /b 1
)

if not defined CORE_JAR (
    echo Could not find Grindstone Core JAR.
	pause
    exit /b 1
)

java -cp "%CLI_JAR%;%CORE_JAR%" io.github.tiagofar78.grindstone.cli.network.Server
pause