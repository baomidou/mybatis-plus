# MyBatis 分页插件
---
> pagination 是一个简易的MyBatis物理分页插件，使用`org.apache.ibatis.session.RowBounds`及其子类作为分页参数，禁用MyBatis自带的内存分页。

## 分页原理
简单来说就是通过拦截StatementHandler重写sql语句，实现数据库的物理分页

## 如何使用插件 
> **mybatis** 配置文件中配置插件 [mybatis-config.xml]
```
<plugins>
    <!-- 
     | 分页插件配置 
     | 插件提供二种方言选择：1、默认方言 2、自定义方言实现类，两者均未配置则抛出异常！
     | dialectType 数据库方言  
     |             默认支持  mysql  oracle  hsql  sqlite  postgre  sqlserver
     | dialectClazz 方言实现类
     |              自定义需要实现 com.baomidou.mybatisplus.plugins.pagination.IDialect 接口
     | -->
    <!-- 配置方式一、使用 MybatisPlus 提供方言实现类 -->
    <plugin interceptor="com.baomidou.mybatisplus.plugins.PaginationInterceptor">
        <property name="dialectType" value="mysql" />
    </plugin>
    <!-- 配置方式二、使用自定义方言实现类 -->
    <plugin interceptor="com.baomidou.mybatisplus.plugins.PaginationInterceptor">
        <property name="dialectClazz" value="xxx.dialect.XXDialect" />
        <!--支持aliDruid与jsqlparser 默认default-->
        <property name="optimizeType" value="aliDruid" />
    </plugin>
    <!-- SQL 执行性能分析，开发环境使用，线上不推荐。 maxTime 指的是 sql 最大执行时长 -->
    <plugin interceptor="com.baomidou.mybatisplus.plugins.PerformanceInterceptor">
        <property name="maxTime" value="100" />
        <!--添加打印SQL格式化-->
        <property name="format" value="true"/>
    </plugin>
    <!-- SQL 执行分析拦截器 stopProceed 发现全表执行 delete update 是否停止运行 -->
    <plugin interceptor="com.baomidou.mybatisplus.plugins.SqlExplainInterceptor">
        <property name="stopProceed" value="false" />
    </plugin>
</plugins>
```
