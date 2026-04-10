$ErrorActionPreference = "Stop"

if (!(Test-Path -Path lib)) { New-Item -ItemType Directory -Path lib }
if (!(Test-Path -Path "lib\jnativehook-2.2.2.jar")) {
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/github/kwhat/jnativehook/2.2.2/jnativehook-2.2.2.jar" -OutFile "lib\jnativehook-2.2.2.jar"
}
if (!(Test-Path -Path "lib\flatlaf-3.4.jar")) {
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4/flatlaf-3.4.jar" -OutFile "lib\flatlaf-3.4.jar"
}

if (!(Test-Path -Path "target\classes")) { New-Item -ItemType Directory -Force -Path "target\classes" }

Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName > sources.txt
javac -cp "lib\jnativehook-2.2.2.jar;lib\flatlaf-3.4.jar" -d target\classes @sources.txt
Remove-Item sources.txt

echo "Compilation complete. Starting JavaAutoClicker..."
java -cp "target\classes;lib\jnativehook-2.2.2.jar;lib\flatlaf-3.4.jar" com.autoclicker.Main
