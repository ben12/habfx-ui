@echo off

if EXIST "%JAVA_HOME%" set PATH=%PATH%;%JAVA_HOME%\bin

javaw -fullversion 1>nul 2>&1 || goto :nojava

for /f tokens^=2-5^ delims^=.-_^" %%j in ('javaw -fullversion 2^>^&1') do @set "jver=%%j%%k" && @set "jupd=%%m"

if %jver% LSS 18 goto :nojava
if %jver% EQU 18 if %jupd% LSS 40 goto :nojava

goto :end

:nojava
echo Java 8 update 40 or newer version must be installed
pause
exit 1

:end

rem remove "/min" added by appassembler
set JAVACMD=start javaw
