mkdir lib -Force
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/github/kwhat/jnativehook/2.2.2/jnativehook-2.2.2.jar" -OutFile "lib\jnativehook-2.2.2.jar"
Get-ChildItem -Path src\main\java\com\autoclicker -Filter *.java | Select-Object -ExpandProperty FullName > sources.txt
javac -cp "lib\jnativehook-2.2.2.jar" @sources.txt
echo "Compilation complete. To run the application, use:"
echo "java -cp `"src\main\java;lib\jnativehook-2.2.2.jar`" com.autoclicker.Main"
