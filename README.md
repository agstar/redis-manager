# redis-manager
一个仿照 Redis Desktop Manager的redis web管理工具 
+ 基于Java（JDK8）语言构建
+ 前端 vue + elementui
+ 后端 Spring Boot 2.1.1.RELEASE

#### 特点
 + 支持多个Redis节点

#### 运行方式
默认启动端口**11000**

修改端口请到src/main/resources/application.properties修改server.port
或者运行时指定`--server.port=xxx`,在启动脚本中start中添加参数

+ 下载源码
+ Maven打包编译
 `mvn clean package`

当项目完成 后续会考虑docker方式部署 



