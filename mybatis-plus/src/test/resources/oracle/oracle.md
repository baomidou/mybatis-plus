
# 驱动设置

> 官方地址：http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-10201-088211.html
      本地：/mybatis-plus/src/test/resources/ojdbc14.jar

# 导入本地 Maven 库

> mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14 -Dversion=10.2.0.5.0 -Dpackaging=jar -Dfile=D:\jars\ojdbc14.jar

```
<dependency>
    <groupId>com.oracle</groupId>
    <artifactId>ojdbc14</artifactId>
    <version>10.2.0.5.0</version>
</dependency>
```

