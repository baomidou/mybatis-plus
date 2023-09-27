* fix: 修复Insert无字段时执行SQL报错.
* fix: 修复高版本JDK下lambda无法执行IDEA调试.
* fix: 修复LambdaQuery中select,groupBy,orderBy,orderByAsc,orderByDesc提示的警告,新增对应doXxx方法支持重写(不兼容改动,api方法做了final处理).
* fix: 修复inject-sql-session-on-mapper-scan无配置提示.
* fix: 修复@OrderBy搭配@TableId排序字段错误(不兼容改动,com.baomidou.mybatisplus.core.metadata.TableInfo.orderByFields调整了类型).
* fix: 修复Service中根据主键逻辑删除时类型不匹配导致的错误.
* fix: 修复分页插件Count与自定义ResultHandler冲突.
* feat: 新增自增主键字段是否允许插入控制,可使用方法注入覆盖Insert(boolean ignoreAutoIncrementColumn)或Insert(String name, boolean ignoreAutoIncrementColumn)控制自增主键是否支持自定义写入行为.
* feat: ActiveRecord模式下deleteById(逻辑删除)方法支持自动填充功能.
* feat: 代码生成器元数据信息公开tableName与columnName字段访问.
* feat: 新增mybatis-plus-spring-boot3-starter与mybatis-plus-spring-boot3-starter-test支持SpringBoot3.
* feat: 支持插件缺省注入,当无MybatisPlusInterceptor注入时,支持com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor自动注入.
* feat: 升级源码Jdk开发版本至Java21.
* feat: 升级gradle-wrapper至8.4-rc-1.
* feat: 升级kotlin-gradle-plugin至1.9.20-Beta.
* feat: 升级SpringBoot2.x版本至2.7.15.
* feat: 升级lombok至1.18.30.
* feat: BaseMapper新增流式查询方法对大数据查询支持.
* opt: mybatis-plus-extension中mybatis-spring依赖修改为可选依赖(不兼容改动,如果项目在非spring或非springBoot下使用到了请手动添加依赖).
* opt: spring-boot-starter减少无用的配置提示(不兼容改动,调整了com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties.configuration类型).
*


