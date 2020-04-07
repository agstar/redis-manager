/bin/bash
if [ -f target/redis-manager-0.0.1-SNAPSHOT.jar ];then
  java -jar target/redis-manager-0.0.1-SNAPSHOT.jar
else
 mvn clean package
 java -jar target/redis-manager-0.0.1-SNAPSHOT.jar
fi