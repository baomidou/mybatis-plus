# CHANGELOG

## [v2.0.9] 2017.06.26
###Mybaits-Plus
####主体功能
- 修正乐观锁和逻辑删除冲突问题
- 处理在生成注入SQL时之前没有考虑到存在且打开下划线配置情况
- 修复EntityWrapper继承关系问题
- Wrapper添加条件判断
- 性能分析插件支持记录日志提示
- Wrapper重写了toString方式,解决之前Debug时显示为null给用户造成错觉
- 处理Sequence非毫秒内并发偶数居多问题
- 忽略策略优化处理更改了注解的属性
- 注入Sql的方式优化,去除之前XML注入方式
- 处理逻辑删除出现2个Where的问题
- 添加其他数据库序列的实现方式,并开放出接口给用户自行扩展
- 乐观锁优化调整
- 优化Wrapper中Where AND OR 去除之前基于反射方式实现,提高代码运行效率
- 处理不添加mybatis-config.xml主键无法填充问题
- MybatisPlus添加支持gradle构建方式
- Wrapper 添加 `and()` `or()` 方法
- 优化GlobalConfiguration,抽离出GlobalConfigUtils减少耦合
- 修复Sqlserver2008与SqlServer2005分页问题
- 新增自动识别数据库,减少用户显式配置
- 优化分页插件减少用户显示配置属性
- 自动填充字段问题解决
- 新增PageHelper,获取当前线程来管理分页(之前老用户最好不要使用,改方式只用户适用MybatisPageHelper用户习惯)
- 大幅度的添加测试用例(感谢K神支持)
- 代码的其他优化
- 添加了JSqlparser的依赖以后不用手动去添加该Jar包

####代码生成
- 支持逻辑删除方式生成
- 支持乐观锁方式生成
- 修复生成器不能识别sqlServer的自增主键代码生成器不能识别SqlServer自增主键的问题
- 支持Lombok方式生成
- 支持构建模式方式生成
- 添加Clob和Blob类型转换
- 修复Oracle的Number类型字段转换错误问题

###Mybatis-Plus-Boot-Start [1.0.2]
####主体功能
- 处理AR模式devtool替换数据源失效问题
- 添加逻辑删除支持
- 添加序列支持

## [v2.0.8] 2017.05.15
- Wrapper添加设置对象sqlSelect
- 兼容无注解情况
- 乐观锁去除默认short实现,优化绑定注册器在扫描阶段绑定. 测试改为h2环境.
- 优化热加载,去除mapper路径配置.
- 减少刷新Mapper配置
- 修复tableFiled value 为空情况，开启下划线命名
- sequence 升级提示
- 开放表信息、预留子类重写
- 修改Idwork测试
- 支持 devtools
- fixed 259 支持 xml resultMap 公共字段生成
- fixed pulls 28 支持属性重载


## [v2.0.6  2.0.7] 2017.04.20
- 新增 逻辑删除
- 新增 Oracle Sequence
- 新增 jdk1.8 时间类型
- 完善支持乐观锁
- 完善字段填充器，支持更新填充
- 升级 mybatis 依赖为 3.4.4
- 代码调整优化，支持 wrapper limit 等逻辑
- 修复 Id 策略 auto bug ，生成器 bug 其他


## [v2.0.5] 2017.03.25

- 修复分页连接池没有关闭的bug
- issues fixed 217
- IMetaObjectHandler当主键类型是AUTO或者INPUT的时候不起效的bug
- 修复 like 占位符问题
- 生成代码的时候如果目录不存在则新建


## [v2.0.3 - v2.0.4] 2017.03.22

- 优化Wrapper代码结构
- 优化原有数据库连接获取
- 解决Page初始化问题(之前只能通过构造方法生效,现在可以通过setget也可以生效)
- 支持乐观锁插件
- 改造Wrapper让JDBC底层来处理参数,更好的与PreparedStatement结合
- 修复相关错误日志提示级别
- Wrapper开放isWhere方法,现在可以自定义是否拼接"WHERE"
- JDK版本向下兼容,之前相关代码用到了1.7新特性,当前版本解除
- sqlserver生成bug修复以及代码优化
- 优化MybatisPlus,SqlSession获取
- 解决未配置切点的情况下获取的sqlSession提交不属于当前事务的问题以及多个sqlSession造成的事务问题
- 增强执行sql类,sqlRunner
- Model添加序列化ID,避免以后在修改Model后没有设置序列号ID时序列号ID可以会变动的情况
- 添加重写默认BaseMapper测试用例
- 感谢各路小伙伴提问的好的建议以及贡献代码,就不一一点名了


## [v2.0.2] 2017.02.13
- 修复全局配置不起作用 2.0.1 逻辑
- 去除byId强制配置类型
- Wrapper Page 等程序优化
- 优化AR模式自动关闭数据库连接(之前需要手动设置事务)
- 优化代码生成器，下划线名称注解不处理驼峰，支持自定义更多的模板例如 jsp html 等
- 新增 service 层测试
- sql日志记录整合至性能分析插件.
- 处理多数据源分页插件支持多重数据库


## [v2.0.1] 2017.01.15

- 解决EntityWrapper对布尔类型构造sql语句错误
- 全局配置初始化日志提示调整
- Mybatis依赖升级至3.4.2,Mybatis-Spring依赖升级至1.3.1
- Service中补充方法(selectObjs,selectMaps)
- 解决selectCount数据库返回null报错问题
- 支持PostgreSql代码生成
- 拓展支持外部提供转义字符以及关键字列表
- 开放数据库表无主键依然注入MP的CRUD(无主键不能使用MP的xxById方法)
- 解决EntityWrapper拼接SQL时,首次调用OR方法不起作用的问题
- sqlServer代码生成(基于2008版本)
- 解决生成代码时未导入BigDecimal问题.
- 释放自动读取数据库时的数据库连接
- 优化全局校验机制(机制为EMPTY增加忽略Date类型)
- 优化注入,避免扫描到BaseMapper
- 优化注入,去除多余注入方法
- SQLlikeType改名为SqlLike
- 解决热加载关联查询错误问题
- SqlQuery改名为SqlRunner
- 优化完善代码生成器
- 修复代码生成器未导入@tableName
- 全局配置需要手动添加MP的默认注入类,更改为自动注入简化配置
- Wrapper增加ne方法
- 修复Mybatis动态参数无法生成totalCount问题
- 代码结构优化，生成器模板优化
- 解决issus[138,140,142,148,151,152,153,156,157]，具体请查看里程碑[mybatis-plus 2.0.1 计划](http://git.oschina.net/baomidou/mybatis-plus/milestones/2)中所有issus

## [v2.0.0] 2016.12.11

- 支持全局大写命名策略
- 自动分页Count语句优化
- 优化现有全局配置策略
- 优化全局验证策略
- 优化代码生成器(之前硬编码，现使用模板形式)
- 优化注入通用方法ByMap逻辑
- 添加自动选择数据库类型
- 改善SqlExplainInterceptor（自行判断MySQL版本不支持该拦截器则直接放行（版本过低小于5.6.3））
- 修复部分特殊字符字符多次转义的问题
- 优化现有EntityWrapper添加Wrapper父类以及Condition链式查询
- Wrapper类使LIKE方法兼容多种数据库
- 优化日志使用原生Mybatis自带的日志输出提示信息
- 修复使用缓存导致使用分页无法计算Count值
- 修复PerformanceInterceptor替换`?`导致打印SQL不准确问题，并添加格式化SQL选项
- 添加多种数据库支持，请查看DBType
- 添加字符串类型字段非空校验策略（字符串类型自动判断非空以及非空字符串）
- Wrapper添加类似QBC查询(eq、gt、lt等等)
- 支持AR模式（需继承Model）
- 合并所有Selective通用方法（例如:去除之前的insert方法并把之前的insetSelective改名为insert）
- 解决sql剥离器会去除`--`的情况
- 支持MySQL关键词，自动转义
- 精简底层Service、Mapper继承结构
- 不喜欢在XML中写SQL的福音，新增执行SQL方式，具体请查看SqlQuery
- 优化代码结构
- 解决issus[95,96,98,100,103,104,108,114,119,121,123,124,125,126,127,128,131,133,134,135]，具体请查看里程碑[mybatis-plus 2.0 计划](http://git.oschina.net/baomidou/mybatis-plus/milestones/1)中所有issus

## [v1.4.9] 2016.10.28

- ServiceImpl去除@Transactional注解、去除Slf4j依赖
- 解决使用EntityWrapper查询时，参数为特殊字符时，存在sql注入问题
- 调整Mybatis驼峰配置顺序 MybatisPlus > Mybatis
- 优化分页插件并修复分页溢出设置不起作用问题
- 去除DBKeywordsProcessor，添加MySQL自动转义关键词
- 代码生成器新增支持TEXT、TIME、TIMESTAMP类型生成
- 新增批量插入方法
- 代码生成器新增Controller层代码生成
- 调整EntityWrapper类部分List入参为Collection
- 代码生成器优化支持 resultMap

## [v1.4.8] 2016.10.12

- insertOrUpdate增加主键空字符串判断
- 支持Mybatis原生驼峰配置 mapUnderscoreToCamelCase 开关设置
- 支持 TableField FieldStrategy 注解全局配置
- SelectOne、SelectCount方法支持EntityWrapper方式
- oracle 代码生成器支持 Integer Long Dobule 类型区分
- 修复INPUT主键策略InsertOrUpdate方法Bug
- EntityWrapper IN 添加可变数组支持
- 基础Mapper、Servcie通用方法PK参数类型更改至Serializable
- 当selectOne结果集不唯一时,添加警告提示(需开启日志warn模式)
- baseService添加logger,子类直接调用logger不用重新定义(需slf4j依赖)

## [v1.4.7] 2016.09.27

- 主键注解 I 改为 PK 方便理解，去掉 mapper 注解
- 性能分析插件，特殊处理 $ 符内容
- 添加自动提交事务说明，新增事务测试
- 支持 resultMap 实体结果集映射
- 增加#TableField(el = "")表达式，当该Field为对象时, 可使用#{对象.属性}来映射到数据表、及测试
- 新增 typeHanler 级联查询支持
- 新增验证字段策略枚举类
- 代码生成器支持实体构建者模型设置
- 代码生成器新增实体常量生成支持
- CRUD 新增 insertOrUpdate 方法
- 解决MessageFormat.format格式化数字类型sql错误
- EntityWrapper添加 EXISTS、IN、BETWEEN AND(感谢D.Yang提出)方法支持
- 支持 mysql5.7+ json enum 类型，代码生成
- 支持无XML依然注入CRUD方法
- 修改Mybatis原生配置文件加载顺序

## [v1.4.6] 2016.09.05

- 新增无 @TableId 注解跳过注入SQL
- 支持非表映射对象插入不执行填充
- xxxByMap 支持 null 查询

## [v1.4.5] 2016.08.28

- 新增 XML 修改自动热加载功能
- 添加自动处理EntityWrapper方法中的MessageFormat Params类型为字符串的参数
- 新增表公共字段自动填充功能

## [v1.4.4] 2016.08.25

- entitywrapper所有条件类方法支持传入null参数，该条件不会附件到SQL语句中
- TSQLPlus更名为TSqlPlus与整体命名保持一致。
- 修复mysql关键字bug----将关键字映射转换加上``符号，增加xml文件生成时可自定义文件后缀名
- 关闭资源前增加非空判断,避免错误sql引起的空指针错误,增加选择 current>pages 判断
- TSQL 相关类实现序列化支持 dubbo
- 增加 mybatis 自动热加载插件
- 支持数据库 order key 等关键词转义 curd 操作

## [v1.4.3] 2016.08.23

- 优化 Sequence 兼容无法获取 mac 情况
- 兼容用户设置 ID 空字符串，自动填充
- 纯大写命名，转为小写属性
- 修改EntityWrapper符合T-SQL语法标准的条件进行方法封装定义
- 升级 1.4.3 测试传递依赖

## [v1.4.0] 2016.08.17

- 增加自定义 select 结果集，优化 page 分页
- 未考虑 函数，去掉 field 优化
- 新增 delete update 全表操作禁止执行拦截器

## [v1.3.9] 2016.08.09

- 修复 bug
- 解决插入 map 异常
- 插入 map 不处理，原样返回
- 优化 IdWorker 生成器
- 支持自定义 LanguageDriver
- 支持代码生成自定义类名
- 升级 mybatis 3.4.1 依赖

## [v1.3.6] 2016.07.28

- 支持全局表字段下划线命名设置
- 增加自定义 注入 sql 方法
- 优化分页总记录数为0不执行列表查询逻辑
- 自动生成 xml 基础字段增加 AS 处理
- 支持字段子查询

## [v1.3.5] 2016.07.24

- 升级 1.3.5 支持全局表字段下划线命名设置
- 添加发现设置多个主键注解抛出异常
- 添加无主键主键启动异常
- 去掉重置 getDefaultScriptingLanuageInstance
- 修改歧义重载方法

## [v1.3.3] 2016.07.15

- 处理 SimpleDateFormat 非现场安全问题
- 修改 oracle 分页 bug 修复
- oracle TIMESTAMP 生成支持 bug 修复

## [v1.3.2] 2016.07.12

- service 暴露 sqlSegment 的方法调用
- 新增 sql 执行性能分析 plugins
- 新增 deleteByMap ， selectByMap

## [v1.3.0] 2016.07.07

- 支持 like 比较等查询 sqlSegment 实现
- 支持 typeAliasesPackage 通配符扫描, 无 count 分页查询
- mybatis mapper 方法调用执行原理测试
- 添加 IOC 演示用例

## [v1.2.17] 2016.06.15

- 优化 代码生成器 感谢 yanghu pull request
- 调整 sql 加载顺序 xmlSql > curdSql
- 支持 CURD 二级缓存
- 增加缓存测试，及特殊字符测试

## [v1.2.15] 2016.04.27

- 新增 支持oracle 自动代码生成，测试 功能
- 新增 UUID 策略
- 演示demo 点击 spring-wind
- 新增支持单表 count 查询

## [v1.2.12] 2016.04.22

- 添加 service 层支持泛型 id 支持，自动生成代码优化
- 升级 mybatis 为 3.4.0 ，mybatis-spring 为 1.3.0

## [v1.2.11] 2016.04.18

- 新增批量更新，支持 oracle 批量操作
- 去掉，移植至 spring-wind 的文档
- 支持 jdk1.5 修改 param 描述
- 添加数据库类型

## [v1.2.9] 2016.04.10

- EntityWrapper 新增无 order by 构造方法
- MailHelper 重载 sendMail 方法
- 新增 String 主键ID 支持 CommonMapper
- 原来方法 selectList 分离为 selectList ， selectPage 两个方法
- 优化代码生成器，添加文档说明、其他

## [v1.2.8] 2016.04.02

- 优化生成代码处理大写字段，支持自动生成 entity mapper service 文件
- 优化分页 index 超出逻辑，新增 5 个 CRUD 操作方法
- 开放模板引擎 getHtmltext 方法
- 优化邮件发送配置添加说明文档
- 添加文档说明、其他

## [v1.2.6] 2016.03.29

- 优化代码 service 层封装，抽离 list 、 page 方法
- 优化分页 count sql 语句
- 改进 mail 工具类
- 完善 framework 对 spring 框架的支持
- 添加文档说明、其他

## [v1.2.5] 2016.03.25

- 独立支持id泛型的 baseMapper
- 更完善的自动生成工具
- 支持实体封装排序
- 分页插件完善
- 抽离 service 主键泛型支持

## [v1.2.2] 2016.03.14

- 注解 ID 区分 AUTO 数据库自增，ID_WORKER 自动填充自定义自增ID , INPUT 手动输入 。
- 优化代码及自动生成器功能。
- 其他
