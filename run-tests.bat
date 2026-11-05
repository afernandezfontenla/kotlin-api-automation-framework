@echo off
rem Runs the whole test suite on Windows.
rem It tells every Java process (including the Gradle daemon) to trust the Windows
rem certificate store, which fixes "PKIX path building failed" behind antivirus/proxy HTTPS inspection.
rem This is a local workaround only: it is NOT part of the build, so CI on Linux is unaffected.
cd /d "%~dp0"
set "JAVA_TOOL_OPTIONS=-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT"
call gradlew.bat --stop
call gradlew.bat test --console=plain
echo.
echo Report: build\reports\tests\test\index.html
rem pause