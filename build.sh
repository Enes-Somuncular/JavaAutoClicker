#!/bin/bash
mkdir -p lib
if [ ! -f "lib/jnativehook-2.2.2.jar" ]; then
    curl -L -o lib/jnativehook-2.2.2.jar https://repo1.maven.org/maven2/com/github/kwhat/jnativehook/2.2.2/jnativehook-2.2.2.jar
fi
if [ ! -f "lib/flatlaf-3.4.jar" ]; then
    curl -L -o lib/flatlaf-3.4.jar https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4/flatlaf-3.4.jar
fi
javac -cp "lib/jnativehook-2.2.2.jar:lib/flatlaf-3.4.jar" src/main/java/com/autoclicker/*.java
echo "Compilation complete."
