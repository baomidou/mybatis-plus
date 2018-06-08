<p align="center">
  <a href="https://github.com/baomidou/mybatis-plus">
   <img alt="Mybatis-Plus-Logo" src="http://git.oschina.net/uploads/images/2016/0824/211639_4d931e7f_12260.png">
  </a>
</p>

<p align="center">
  为简化开发工作、提高生产率而生
</p>

<p align="center">
  <a href="http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.baomidou%22%20AND%20a%3A%22mybatis-plus%22">
    <img alt="maven" src="https://img.shields.io/maven-central/v/com.baomidou/mybatis-plus.svg?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="code style" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

# 简介 | Intro

Mybatis 增强工具包 - 只做增强不做改变，简化`CRUD`操作

技术讨论 QQ 群 ： 576493122 🈵  、  648281531 、  643509491

# 优点 | Advantages

- **无侵入**：Mybatis-Plus 在 Mybatis 的基础上进行扩展，只做增强不做改变，引入 Mybatis-Plus 不会对您现有的 Mybatis 构架产生任何影响，而且 MP 支持所有 Mybatis 原生的特性
- **依赖少**：仅仅依赖 Mybatis 以及 Mybatis-Spring
- **损耗小**：启动即会自动注入基本CURD，性能基本无损耗，直接面向对象操作
- **预防Sql注入**：内置Sql注入剥离器，有效预防Sql注入攻击
- **通用CRUD操作**：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
- **多种主键策略**：支持多达4种主键策略（内含分布式唯一ID生成器），可自由配置，完美解决主键问题
- **支持热加载**：Mapper 对应的 XML 支持热加载，对于简单的 CRUD 操作，甚至可以无 XML 启动
- **支持ActiveRecord**：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可实现基本 CRUD 操作
- **支持代码生成**：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码，支持模板引擎，更有超多自定义配置等您来使用（P.S. 比 Mybatis 官方的 Generator 更加强大！）
- **支持自定义全局通用操作**：支持全局通用方法注入（ Write once, use anywhere ）
- **支持关键词自动转义**：支持数据库关键词（order、key......）自动转义，还可自定义关键词
- **内置分页插件**：基于Mybatis物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于写基本List查询
- **内置性能分析插件**：可输出Sql语句以及其执行时间，建议开发测试时启用该功能，能有效解决慢查询
- **内置全局拦截插件**：提供全表 delete 、 update 操作智能分析阻断，预防误操作

# 文档 | Documentation

[中文](http://mp.baomidou.com/)

# 原理 | Principle

[Mybatis-Plus 实践及架构原理](http://git.oschina.net/baomidou/mybatis-plus/attach_files)

# 应用实例 | Demo

[Spring-MVC](https://git.oschina.net/baomidou/mybatisplus-spring-mvc)

[Spring-Boot](https://git.oschina.net/baomidou/mybatisplus-spring-boot)

[mybatisplus-sharding-jdbc](https://gitee.com/baomidou/mybatisplus-sharding-jdbc)

[SSM-实战 Demo](http://git.oschina.net/juapk/SpringWind)

# 下载地址 | Download

[点此去下载](http://maven.aliyun.com/nexus/#nexus-search;quick~mybatis-plus)

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus</artifactId>
    <version>maven 官方最新版本为准</version>
</dependency>
```

# 结构目录 | Architecture

![项目结构说明](http://git.oschina.net/uploads/images/2016/0821/161516_58956b85_12260.png "项目结构说明")

# 其他开源项目 | Other Project

- [基于Cookie的SSO中间件 Kisso](http://git.oschina.net/baomidou/kisso)
- [Java快速开发框架 SpringWind](http://git.oschina.net/juapk/SpringWind)
- [基于Hibernate扩展 Hibernate-Plus](http://git.oschina.net/baomidou/hibernate-plus)

# 王者荣耀
![MPTrophy](https://gitee.com/uploads/images/2018/0102/101803_2fdba060_12260.jpeg)

# 期望 | Futures

> 欢迎提出更好的意见，帮助完善 Mybatis-Plus

# 版权 | License

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

# 捐赠 | Donate

> [捐赠记录,感谢你们的支持！](http://git.oschina.net/baomidou/kisso/wikis/%E6%8D%90%E8%B5%A0%E8%AE%B0%E5%BD%95)

![捐赠 mybatis-plus](http://git.oschina.net/uploads/images/2015/1222/211207_0acab44e_12260.png "支持一下mybatis-plus")

# 关注我 | About Me

![程序员日记](http://git.oschina.net/uploads/images/2016/0121/093728_1bc1658f_12260.png "程序员日记")
