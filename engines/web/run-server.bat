@echo off
setlocal

set "JAVA_HOME=C:\Users\tiago\AppData\Roaming\Kiro\User\globalStorage\pleiades.java-extension-pack-jdk\java\latest"
set "PATH=%JAVA_HOME%\bin;%PATH%"

for %%F in ("target\grindstone-web-engine-*.jar") do set WEB_JAR=%%~fF

if not defined WEB_JAR (
    echo Could not find Web engine JAR. Run: mvn -q install -DskipTests -pl engines/web -am
    pause
    exit /b 1
)

java -jar "%WEB_JAR%"
