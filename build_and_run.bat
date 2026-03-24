@echo off
if not exist lib mkdir lib
if not exist lib\jnativehook-2.2.2.jar (
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/github/kwhat/jnativehook/2.2.2/jnativehook-2.2.2.jar' -OutFile 'lib\jnativehook-2.2.2.jar'"
)
if not exist lib\flatlaf-3.4.jar (
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4/flatlaf-3.4.jar' -OutFile 'lib\flatlaf-3.4.jar'"
)
javac -cp "lib\jnativehook-2.2.2.jar;lib\flatlaf-3.4.jar" src\main\java\com\autoclicker\*.java
echo Compilation complete.
