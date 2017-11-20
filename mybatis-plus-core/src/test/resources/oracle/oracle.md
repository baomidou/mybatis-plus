
# 驱动设置

> 官方地址：http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-10201-088211.html
      本地：/mybatis-plus/src/test/resources/ojdbc14.jar

# 导入本地 Maven 库

> mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14 -Dversion=10.2.0.5.0 -Dpackaging=jar -Dfile=D:\jars\ojdbc14.jar


# 出现异常，使用带 "" 的导入方式！

 [ERROR] The goal you specified requires a project to execute but there is no POM in this directory (/Users/hubin). Please verify you invoked Maven from the correct directory. -> [Help 1]

> mvn install:install-file "-DgroupId=com.oracle" "-DartifactId=ojdbc14" "-Dversion=10.2.0.5.0" "-Dpackaging=jar" "-Dfile=D:\jars\ojdbc14.jar" "-DgeneratePom=true"


```
<dependency>
    <groupId>com.oracle</groupId>
    <artifactId>ojdbc14</artifactId>
    <version>10.2.0.5.0</version>
</dependency>
```



# 如果你不想导入改为如下依赖

```
<dependency>
    <groupId>com.oracle</groupId>
    <artifactId>ojdbc14</artifactId>
    <version>10.2.0.5.0</version>
    <scope>system</scope>
	<systemPath>${project.basedir}/src/test/resources/oracle/ojdbc14.jar</systemPath>
</dependency>
```


