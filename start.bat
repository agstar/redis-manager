@echo off
if exist "target/redis-manager-0.0.1-SNAPSHOT.jar" (
    java -jar target/redis-manager-0.0.1-SNAPSHOT.jar
 )

else (
    maven clean package
    java -jar target/redis-manager-0.0.1-SNAPSHOT.jar
)
