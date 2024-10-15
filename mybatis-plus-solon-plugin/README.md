```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>mybatis-plus-solon-plugin</artifactId>
</dependency>
```

#### 1、描述

数据扩展插件，为 Solon Data 提供基于 mybatis-plus 的框架适配，以提供ORM支持。除通用体验外，还包括 Solon 适配特有的方式，如下：


#### 2、强调多数据源支持

* 强调多数据源的配置。例：db1...，db2...
* 强调带 name 的 DataSource Bean
* 强调使用 @Db("name") 的数据源注解


@Db 可注入类型：

| 支持类型 | 说明 |
| -------- | -------- |
| Mapper.class     | 注入 Mapper。例：`@Db("db1") UserMapper userMapper`     |
| IService  | 注入 IService。例：`@Db("db1") UserService userService`     |
| MybatisConfiguration     | 注入 MybatisConfiguration，一般仅用于配置。例：`@Db("db1") MybatisConfiguration db1Cfg` |
| GlobalConfig     | 注入 GlobalConfig，一般仅用于配置。例：`@Db("db1") GlobalConfig db1Gc` |
| SqlSessionFactory     | 注入 SqlSessionFactory。例：`@Db("db1") SqlSessionFactory db1` （不推荐直接使用） |


#### 3、 数据源配置与构建（具体参考：[《数据源的配置与构建，支持分片、动态等不同类型的数据源》](https://solon.noear.org/article/794)）

```yml
#数据源配置块（名字按业务需要取，与 @Db 那边对起来就好）
solon.dataSources:
  db1!:
    class: "com.zaxxer.hikari.HikariDataSource"
    jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456

# 配置数据源对应的 mybatis 信息（要与 DataSource bean 的名字对上）
mybatis.db1:
    typeAliases:    #支持包名 或 类名（大写开头 或 *）//支持 ** 或 * 占位符
        - "demo4021.model"
        - "demo4021.model.*" #这个表达式同上效果
    typeHandlers: #支持包名 或 类名（大写开头 或 *）//支持 ** 或 * 占位符
        - "demo4021.dso.mybaits.handler"
        - "demo4021.dso.mybaits.handler.*" #这个表达式同上效果
    mappers:        #支持包名 或 类名（大写开头 或 *）或 xml（.xml结尾）//支持 ** 或 * 占位符
        - "demo4021.**.mapper"
        - "demo4021.**.mapper.*" #这个表达式同上效果
        - "classpath:demo4021/**/mapper.xml"
        - "classpath:demo4021/**/mapping/*.xml"
    configuration:  #扩展配置（要与 MybatisConfiguration 类的属性一一对应）
        cacheEnabled: false
        mapperVerifyEnabled: false #如果为 true，则要求所有 mapper 有 @Mapper 主解
        mapUnderscoreToCamelCase: true
    globalConfig:   #全局配置（要与 GlobalConfig 类的属性一一对应）
        banner: false
        metaObjectHandler: "demo4031.dso.MetaObjectHandlerImpl"
        dbConfig:
            logicDeleteField: "deleted"


#
#提示：使用 "**" 表达式时，范围要尽量小。不要用 "org.**"、"com.**" 之类的开头，范围太大了，会影响启动速度。
#
```

其中 `configuration` 配置节对应的实体为：`com.baomidou.mybatisplus.core.MybatisConfiguration`（相关项，可参考实体属性）


其中 `globalConfig` 配置节对应的实体为：`com.baomidou.mybatisplus.core.config.GlobalConfig`（相关项，可参考实体属性）


#### 4、关于 mappers 配置的补说明（必看）

* 思路上，是以数据源为主，去关联对应的 mapper（为了多数据源方便）
* 如果配置了 xml ，则 xml 对应的 mapper 可以不用配置（会自动关联进去）

```
mybatis.db1:
    mappers:
        - "classpath:demo4021/**/mapper.xml"
```

* 如果没有对应 xml 文件的 mapper，必须配置一下

```
mybatis.db1:
    mappers:
        - "demo4021.**.mapper.*"
```

#### 5、代码应用

```java
//数据源相关的定制（可选）
@Configuration
public class Config {
    //调整 db1 的配置（如：增加插件）// (配置可以解决的，不需要这块代码)
    @Bean
    public void db1_cfg(@Db("db1") MybatisConfiguration cfg,
                        @Db("db1") GlobalConfig globalConfig) {

        //增加 mybatis-plus 的自带分页插件
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        cfg.addInterceptor(plusInterceptor);

        //cfg.setCacheEnabled(false);

        //globalConfig.setIdentifierGenerator(null);
    }
}

//应用
@Component
public class AppService{
    //可用 @Db 或 @Db("db1") 注入
    @Db
    AppMapper appMapper; //xml sql mapper

    //可用 @Db 或 @Db("db1")
    @Db
    BaseMapper<App> appBaseMapper; //base mapper

    @Db
    AppServicePlus appServicePlus;

    public void test(){
        //三种不同接口的样例
        App app1 = appMapper.getAppById(12);
        App app2 = appBaseMapper.getById(12);
    }
}


@Component
public class AppServicePlusImpl extends ServiceImpl<AppxMapperPlus, AppxModel> implements AppServicePlus {

}
```

#### 6、分页查询

可用：[mybatis-plus-extension-solon-plugin](/article/231) 自带的分页能力


**具体可参考：**

[https://gitee.com/noear/solon-examples/tree/main/4.Solon-Data/demo4031-mybatisplus](https://gitee.com/noear/solon-examples/tree/main/4.Solon-Data/demo4031-mybatisplus)
