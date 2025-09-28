<p align="center">
  <a href="https://github.com/baomidou/mybatis-plus">
   <img alt="Mybatis-Plus-Logo" src="https://raw.githubusercontent.com/baomidou/logo/master/mybatis-plus-logo-new-mini.png">
  </a>
</p>

<p align="center">
  Born To Simplify Development
</p>

<p align="center">

  <a href="https://github.com/baomidou/mybatis-plus">
    <img alt="code style" src="https://img.shields.io/github/stars/baomidou/mybatis-plus?style=social">
  </a>

  <a href="https://gitee.com/baomidou/mybatis-plus">
    <img alt="code style" src="https://gitee.com/baomidou/mybatis-plus/badge/star.svg">
  </a>

  <a href="https://gitcode.com/baomidou/mybatis-plus">
    <img alt="code style" src="https://gitcode.com/baomidou/mybatis-plus/star/badge.svg">
  </a>

  <a href="https://search.maven.org/search?q=g:com.baomidou%20a:mybatis-*">
    <img alt="maven" src="https://img.shields.io/maven-central/v/com.baomidou/mybatis-plus.svg?style=flat-square">
  </a>

</p>

[企业版 Mybatis-Mate 高级特性](https://gitee.com/baomidou/mybatis-mate-examples)

添加 `微信 wx153666` 备注进 mp 群

> 不允许非法项目使用，后果自负

# Special user

<p>
  <a href="https://doc.flowlong.com?from=mp" target="_blank">
   <img alt="aizuda-Logo" src="https://foruda.gitee.com/images/1715955628416785121/954c16ef_12260.png" width="160px" height="50px">
  </a>
  <a href="https://gitee.com/gz-yami/mall4j?from=mp" target="_blank">
   <img alt="mall4j-Logo" src="https://foruda.gitee.com/images/1716776021837872678/87883b39_12260.gif" width="160px" height="50px">
  </a>
</p>

## What is MyBatis-Plus?

MyBatis-Plus is a powerful and enhanced toolkit of MyBatis for simplifying development.
It provides efficient, and out-of-the-box features (such as code generation, conditional query builders, pagination plugins...), effectively saving development time

## Links

- [Documentation](https://baomidou.com)
- [Code Generator](https://github.com/baomidou/generator)
- [Samples](https://github.com/baomidou/mybatis-plus-samples)
- [Showcase](https://github.com/baomidou/awesome-mybatis-plus)
- [企业版 Mybatis-Mate 高级特性](https://gitee.com/baomidou/mybatis-mate-examples)

## Features

- Fully compatible with MyBatis
- Auto configuration on startup
- Out-of-the-box interfaces for operate database
- Powerful and flexible where condition wrapper
- Multiple strategy to generate primary key
- Lambda-style API
- Almighty and highly customizable code generator
- Automatic paging operation
- SQL Inject defense
- Support active record
- Support pluggable custom interface
- Build-in many useful extensions

## Getting started

- Add MyBatis-Plus dependency
    - Latest
      Version: [![Maven Central](https://img.shields.io/maven-central/v/com.baomidou/mybatis-plus.svg)](https://search.maven.org/search?q=g:com.baomidou%20a:mybatis-*)
    - Maven:
    - SpringBoot2
      ```xml
      <dependency>
          <groupId>com.baomidou</groupId>
          <artifactId>mybatis-plus-boot-starter</artifactId>
          <version>Latest Version</version>
      </dependency>
      ```
    - SpringBoot3
      ```xml
      <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>Latest Version</version>
      </dependency>
      ```
    - SpringBoot4 `^3.5.13`
      ```xml
      <dependency>
      <groupId>com.baomidou</groupId>
      <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
      <version>Latest Version</version>
      </dependency>
      ```
    - `^3.5.9` may need additional citations
        - jdk11+
      ```xml
      <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-jsqlparser</artifactId>
        <version>Latest Version</version>
      </dependency>
      ```
        - jdk8
      ```xml
      <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-jsqlparser-4.9</artifactId>
        <version>Latest Version</version>
      </dependency>
      ```

    - Gradle
    - SpringBoot2
      ```groovy
      compile group: 'com.baomidou', name: 'mybatis-plus-boot-starter', version: 'Latest Version'
      ```
    - SpringBoot3
      ```groovy
      compile group: 'com.baomidou', name: 'mybatis-plus-spring-boot3-starter', version: 'Latest Version'
      ```
- Modify mapper file extends BaseMapper interface

  ```java
  public interface UserMapper extends BaseMapper<User> {

  }
  ```

- Use it
  ``` java
  List<User> userList = userMapper.selectList(
          new QueryWrapper<User>()
                  .lambda()
                  .ge(User::getAge, 18)
  );
  ```
  MyBatis-Plus will execute the following SQL
    ```sql
    SELECT * FROM user WHERE age >= 18
    ```

> This showcase is just a small part of MyBatis-Plus features. If you want to learn more, please refer to
> the [documentation](https://baomidou.com).

## License

MyBatis-Plus is under the Apache 2.0 license. See the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
file for details.
