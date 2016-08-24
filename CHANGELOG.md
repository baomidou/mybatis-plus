# CHANGELOG

## [v1.4.3](http://www.oschina.net/news/76419/mybatis-plus-1-4-3) 2016.08.23
- 优化 Sequence 兼容无法获取 mac 情况
- 兼容用户设置 ID 空字符串，自动填充
- 纯大写命名，转为小写属性
- 修改EntityWrapper符合T-SQL语法标准的条件进行方法封装定义
- 升级 1.4.3 测试传递依赖

## [v1.4.0](http://www.oschina.net/news/76219/mybatis-plus-1-4-0) 2016.08.17
- 增加自定义 select 结果集，优化 page 分页
- 未考虑 函数，去掉 field 优化
- 新增 delete update 全表操作禁止执行拦截器

## [v1.3.9](http://www.oschina.net/news/75970/mybatis-plus-1-3-9) 2016.08.09
- 修复 bug
- 解决插入 map 异常
- 插入 map 不处理，原样返回
- 优化 IdWorker 生成器
- 支持自定义 LanguageDriver
- 支持代码生成自定义类名
- 升级 mybatis 3.4.1 依赖

## [v1.3.6](http://www.oschina.net/news/75624/mybatis-plus-1-3-6) 2016.07.28
- 支持全局表字段下划线命名设置
- 增加自定义 注入 sql 方法
- 优化分页总记录数为0不执行列表查询逻辑
- 自动生成 xml 基础字段增加 AS 处理
- 支持字段子查询

## [v1.3.5](http://www.oschina.net/news/75489/mybatis-plus-1-3-5) 2016.07.24
- 升级 1.3.5 支持全局表字段下划线命名设置
- 添加发现设置多个主键注解抛出异常
- 添加无主键主键启动异常
- 去掉重置 getDefaultScriptingLanuageInstance
- 修改歧义重载方法

## [v1.3.3](http://www.oschina.net/news/75238/mybatis-plus-1-3-3) 2016.07.15
- 处理 SimpleDateFormat 非现场安全问题
- 修改 oracle 分页 bug 修复
- oracle TIMESTAMP 生成支持 bug 修复

## [v1.3.2](http://www.oschina.net/news/75138/mybatis-plus-1-3-2) 2016.07.12
- service 暴露 sqlSegment 的方法调用
- 新增 sql 执行性能分析 plugins
- 新增 deleteByMap ， selectByMap

## [v1.3.0](http://www.oschina.net/news/74998/mybatis-plus-1-3-0) 2016.07.07
- 支持 like 比较等查询 sqlSegment 实现
- 支持 typeAliasesPackage 通配符扫描, 无 count 分页查询
- mybatis mapper 方法调用执行原理测试
- 添加 IOC 演示用例

## [v1.2.17](http://www.oschina.net/news/74333/mybatis-plus-1-2-17) 2016.06.15
- 优化 代码生成器 感谢 yanghu pull request
- 调整 sql 加载顺序 xmlSql > curdSql
- 支持 CURD 二级缓存
- 增加缓存测试，及特殊字符测试

## [v1.2.15](http://www.oschina.net/news/72895/mybatis-plus-1-2-15) 2016.04.27
- 新增 支持oracle 自动代码生成，测试 功能
- 新增 UUID 策略
- 演示demo 点击  spring-wind
- 新增支持单表 count 查询

## [v1.2.12](http://www.oschina.net/news/72746/mybatis-plus-1-2-12) 2016.04.22
- 添加 service 层支持泛型 id 支持，自动生成代码优化
- 升级 mybatis 为 3.4.0 ，mybatis-spring 为 1.3.0

## [v1.2.11](http://www.oschina.net/news/72592/mybatis-plus-1-2-11) 2016.04.18
- 新增批量更新，支持 oracle 批量操作
- 去掉，移植至 spring-wind 的文档
- 支持 jdk1.5 修改 param 描述
- 添加数据库类型

## [v1.2.9](http://www.oschina.net/news/72331/mybatis-plus-1-2-9) 2016.04.10
- EntityWrapper 新增无 order by 构造方法
- MailHelper 重载 sendMail 方法
- 新增 String 主键ID 支持 CommonMapper
- 原来方法 selectList 分离为 selectList ， selectPage 两个方法
- 优化代码生成器，添加文档说明、其他

## [v1.2.8](http://www.oschina.net/news/72091/mybatis-plus-1-2-8) 2016.04.02
- 优化生成代码处理大写字段，支持自动生成 entity  mapper  service 文件
- 优化分页 index 超出逻辑，新增 5 个 CRUD 操作方法
- 开放模板引擎 getHtmltext 方法
- 优化邮件发送配置添加说明文档
- 添加文档说明、其他

## [v1.2.6](http://www.oschina.net/news/71979/mybatis-plus-1-2-6) 2016.03.29
- 优化代码 service 层封装，抽离 list 、 page 方法
- 优化分页 count sql 语句
- 改进 mail 工具类
- 完善 framework 对 spring 框架的支持
- 添加文档说明、其他

## [v1.2.5](http://www.oschina.net/news/71887/mybatis-plus-1-2-5) 2016.03.25
- 独立支持id泛型的 baseMapper
- 更完善的自动生成工具
- 支持实体封装排序
- 分页插件完善
- 抽离 service 主键泛型支持

## [v1.2.2](http://www.oschina.net/news/71505/mybatis-plus-1-2-2) 2016.03.14
- 注解 ID 区分  AUTO 数据库自增，ID_WORKER 自动填充自定义自增ID , INPUT 手动输入 。
- 优化代码及自动生成器功能。
- 其他
