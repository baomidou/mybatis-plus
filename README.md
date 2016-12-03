![Mybatis-Plus-Logo](http://git.oschina.net/uploads/images/2016/0824/211639_4d931e7f_12260.png "logo")

[![license](https://img.shields.io/github/license/baomidou/mybatis-plus.svg?maxAge=2592000)](http://www.apache.org/licenses/LICENSE-2.0)
[![GitHub release](https://img.shields.io/github/release/baomidou/mybatis-plus.svg?maxAge=2592000)](https://github.com/baomidou/mybatis-plus)


  `如果你喜欢 Hibernate ，可以尝试使用`

- [Hibernate-Plus](http://git.oschina.net/baomidou/hibernate-plus)


> 为简化开发工作、提高生产率而生

# 简介 | Intro

Mybatis 增强工具包 - 只做增强不做改变，简化`CRUD`操作

> 技术讨论 QQ 群 492238239 如满，加群 121472998 [（有钱的捧个钱场【点击捐赠】, 没钱的捧个人场）](http://git.oschina.net/uploads/images/2015/1222/211207_0acab44e_12260.png)

# 优点 | Advantages

- **纯正血统**：完全继承原生 `Mybatis` 的所有特性
- **最少依赖**：仅仅依赖`Mybatis`以及`Mybatis-Spring`
- **性能损耗小**：启动即会自动注入基本CURD ，性能无损耗，直接面向对象操作
- **自动热加载**：Mapper对应的xml可以热加载，大大减少重启Web服务器时间，提升开发效率
- **自动生成代码**：包含自动生成代码类以及Maven插件，通过少量配置，即可快速生成Mybatis对应的xml、mapper、entity、service、serviceimpl层代码，减少开发时间
- **自定义操作**：支持自定义Sql注入，实现个性化操作
- **自定义转义规则**：支持数据库关键词（例如：`order`、`key`等）自动转义，支持自定义关键词
- **多种主键策略**：支持多达4种主键策略，可自由配置，若无将会自动填充，更有充满黑科技的`分布式全局唯一ID生成器`
- **无缝分页插件**：基于Mybatis物理分页，无需关心具体操作，等同于编写基本`selectList`查询
- **性能分析**：自带Sql性能分析插件，开发测试时，能有效解决慢查询
- **全局拦截**：提供全表`delete`、`update`操作智能分析阻断
- **避免Sql注入**：内置Sql注入内容剥离器，预防Sql注入攻击

# 文档 | Documentation

[中文](http://mp.baomidou.com/) | [English](http://mp.baomidou.com/en/)

# 原理 | Principle

[Mybatis-Plus 实践及架构原理](http://git.oschina.net/baomidou/mybatis-plus/attach_files)

# 应用实例 | Demo

[Spring-MVC](https://git.oschina.net/baomidou/mybatisplus-spring-mvc)

[Spring-Boot](https://git.oschina.net/baomidou/mybatisplus-spring-boot)

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

# 期望 | Futures

> 欢迎提出更好的意见，帮助完善 Mybatis-Plus

# 版权 | License

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

# 捐赠 | Donate

> [捐赠记录,感谢你们的支持！](http://git.oschina.net/baomidou/kisso/wikis/%E6%8D%90%E8%B5%A0%E8%AE%B0%E5%BD%95)

![捐赠 mybatis-plus](http://git.oschina.net/uploads/images/2015/1222/211207_0acab44e_12260.png "支持一下mybatis-plus")

# 关注我 | About Me

![程序员日记](http://git.oschina.net/uploads/images/2016/0121/093728_1bc1658f_12260.png "程序员日记")
