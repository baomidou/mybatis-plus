# 测试规范

适用于 `src/test` 下的全部 Java/Kotlin 测试。条目编号 T1..T10，与闸门文件
[mybatis-plus-core/src/test/java/com/baomidou/mybatisplus/test/convention/TestConventionTest.java](../mybatis-plus-core/src/test/java/com/baomidou/mybatisplus/test/convention/TestConventionTest.java)
里各用例的断言消息一一对应：红了就按消息里的编号回本文档查。消息里的 T0 是扫描自检
（定位不到仓库根、或一个测试文件都没扫到就直接失败），不是规范条目。

```bash
./gradlew :mybatis-plus-core:test --tests "*TestConventionTest*"   # 只跑规范闸门
./gradlew test                                                    # 全量测试已包含它
```

格式（缩进、import 顺序、无用 import）由 spotless 管，`./gradlew spotlessApply` 即可；
本规范只管写法不管格式。

状态含义：**机器** = 违规即红；**冻结** = 存量已登记，只准降不准升；**未执行** = 目前只是本文档的约定，靠人工评审。

| 编号 | 条目 | 状态 | 基线（2026-10-21 实测） |
|---|---|---|---|
| T1 | 测试与被测类同模块、包镜像 | 冻结 | 34 处缺口，全部是 4 个 JSqlParser 版本模块的整文件复制（其余模块已零缺口） |
| T2 | 命名：`<被测类>Test`、简单名全仓唯一、只用 `Test` 后缀 | 机器 + 冻结 | 后缀零违规；历史同名 26 组 |
| T3 | 每个测试类属于一个层 | 未执行（仅词表是机器） | 仓库里还没有 `@Tag` |
| T4 | 只用 AssertJ，禁裸 `assert`、禁 JUnit 4、禁通配导入 Jupiter | 机器 + 冻结 | 裸 `assert`/JUnit 4/Jupiter 通配导入零违规；可转换 JUnit 断言存量 0（已清零） |
| T5 | 每个用例必须有断言；catch 不许吞异常；禁 `Thread.sleep` 当同步 | 机器 + 冻结 | 零断言 7（6 个禁用 + 1 个整包不执行）、无用例 `*Test` 0（已清零，改成零容忍）、吞异常 catch 0（零容忍）、sleep 1；`assertThat` 不链断言方法零违规 |
| T6 | `@Disabled` 必须写清原因 | 机器 + 冻结 | 无理由零违规；禁用 6 |
| T7 | 枚举型公共契约必须配漂移闸门 | 未执行 | 已有闸门 7 处：URL→`DbType`、方言全量、`IdType`、`FieldStrategy`、SQL 注入清单、wrapper 关键字表、WHERE 片段装配 |
| T8 | 测试实体类名/表名全局唯一 | 机器 + 冻结 | 共用表名 6 处已登记（`h2user`、`key_generator_model`、`sys_user`、`t_simple`、`xxx`、`entity`），新增即红 |
| T9 | 禁 `@EnabledOnJre` 式永久跳过 | 机器 | 零违规 |
| T10 | 生产缺陷走 issue + 特征化测试 | 未执行 | — |

## T1 测试放对位置

测试类的包应与被测类的包一致，且放在被测类所在模块。跨模块的行为测试放聚合模块
`mybatis-plus`（它没有 `src/main`，专职组装别的模块）。理由：改一个类时能靠包名找到它的测试，
模块级 `./gradlew :xxx:test` 才会真正覆盖到该模块。

## T2 一个被测类只有一处测试

类名 `<被测类>Test`，简单名在全仓唯一。新增测试类不允许用 `*Tests`、`*TestCase`、`*IT` 后缀。
多版本（JSqlParser 4.9/5.0/5.2、Boot 2/3/4）的支持靠共享源——公共基类或 `sourceSets` 复用同一份
测试代码，**不要复制文件**。现存 26 组同名类多数是 starter 与 JSqlParser 版本支持模块的整文件复制，
去重一组就从 `REGISTERED_DUPLICATE_NAMES` 里删掉对应项；两份内容互补的就合成一份
（core 的 `ReflectionKitTest` 已经这样并掉 `test.pom` 下那份）。

## T3 分层

层名固定五个：`unit`、`integration`、`external-db`、`build-metadata`、`stress`。
现在只机器保证"用了 `@Tag` 就必须来自这五个"；"每个类恰好一个层标签"要等按标签过滤的执行入口
（`testUnit` / `testIntegration` / …）落地后才强制。在此之前新增测试请顺手带上标签，
别等补分层时一次性大改。

需要真实数据库、本机中间件（ZooKeeper、Redis）的用例属于 `external-db`，当前一律
`@Disabled` + 原因，等入口就位后换成标签。

## T4 断言只用 AssertJ

- 禁止 Java 的 `assert` 语句和 Kotlin 的 `assert()`：测试任务默认不开 `-ea`，写了等于没写。
- 禁止 JUnit 4 的 `org.junit.Test`/`@Before`/`@After`/`@Ignore`/`org.junit.runner.*`：
  与 Jupiter 混用会让用例计数不可信，`@Ignore` 也不会被 Jupiter 执行。
- 存量里的 JUnit 风格断言按**模块成批**转换，一个模块一个提交，不做跨模块的机械扫射：
  这套测试文件半年内改了 334 个，一次性重写会和日常提交撞车。
- 新文件只用 AssertJ，不要再引 `org.junit.jupiter.api.Assertions`。存量欠账已在 2026-10-21 逐模块清零
  （core、extension、spring、spring-boot-starter（12 个）、`mybatis-plus-jsqlparser-support` 4 个子模块
  （30 个、702 处调用）、`mybatis-plus`（41 个、711 处调用）、`mybatis-plus-generator`（23 个、464 处调用）、
  `mybatis-plus-up`（1 个、103 处调用）），`MAX_JUNIT_ASSERTION_FILES` **只能是 0**，调大等于允许有人塞回去。
  `assertAll`、`assertTimeout` 系列 AssertJ 没有对应物，留在原处是正确写法，不计入欠账。
- 禁止 `import org.junit.jupiter.api.*;`：它把 `Assertions` 一起请进文件，按类名找 JUnit 断言的扫描会整个漏掉，
  上一批欠账盘点就这么少算了 10 个文件。要什么就导什么。
- 换成 AssertJ 时注意**数值断言的重载**：JUnit 的 `assertEquals(3, shortValue)` 会把两边都放宽成 `int`，
  AssertJ 的 `assertThat(shortValue).isEqualTo(3)` 里 3 装箱成 `Integer`，和 `Short` 永远不相等，直接判红。
  比 `short`/`byte`/`char` 时把字面量显式转型（`isEqualTo((short) 3)`），别指望编译器。
- 机械转换只保证"等价"，不保证"惯用"：`assertThat(list.size()).isEqualTo(3)`、`assertThat(list.isEmpty()).isFalse()`
  能编译能红，但失败信息只报一个数字，看不出断的是哪个集合。落到这类形状就地改成
  `hasSize(3)`、`isEmpty()`/`isNotEmpty()`、`containsKey(k)`，别留着下次再改。
- 取 `assertThrows` 返回值的写法不能机械换：AssertJ 没有"返回异常对象的 assertThrows"。
  改成 `assertThatThrownBy(...).isInstanceOf(X.class).hasMessageContaining("...")` 一条链，
  顺带把"异常消息里有没有这句话"从 `assertTrue(error.getMessage().contains(...))`（消息为 null 直接 NPE）
  变成会正常判红的断言。

## T5 绿必须有意义

一个用例要么断言，要么删掉。禁止这几种"永远绿"的写法：

- 零断言的 `@Test`（只调用不验证）；
- `assertThat(x);` 光造断言对象、后面不链断言方法（`assertThatCode`/`assertThatThrownBy` 同理）：
  它看着像在断言，实际永远绿；下面的零断言扫描按字样判，认不出这种写法，所以由独立规则盯收尾；
- `try { ... } catch (Exception e) { log... }` 吞异常，不 `fail()`：已经落成机器检查，零容忍。
  catch 里只允许三种写法——重新抛出（或 `fail()`）、当场断言、把异常收到变量/集合里留给后面的断言；
  只打日志或只 `printStackTrace()` 都算红。真要让某条路径静默，就别写 catch，改成 `assertThatCode(...)`；
- 检查发现问题只打日志不失败（warn-only）；
- 用 `main()` 冒充测试；确需保留演示入口就命名 `*Sample` 并明确它不是用例。
  被测代码要求测试类本身是启动类时（`CollectUtilsTest`）例外，但必须写清这个 `main()` 为什么不能删；
- 用 `Thread.sleep` 当同步手段。

基线数字是存量欠账：补一个断言就把常量调小一格，禁止调大。用例调用本文件或测试基类里会断言的辅助方法
（如 `WrapperTestSupport#logSqlWhere`）算已经断言，不计入 7。名为 `*Test` 却不含用例的基类和夹具
已经清零：这类文件一律改名 `*Support`/`*Fixture`/`*Sample`，不留登记豁免，新增即红。

`MAX_ASSERTION_FREE_CASES` 与 sleep 登记停在 7 和 1，是因为剩下的都补不动：
6 个要连真实外部库（MySQL、PostgreSQL、Oracle、达梦）或本机 ZooKeeper，用例本身已经 `@Disabled` 并写明依赖；
最后 1 个 `PostgresqlTest#testTask` 连执行机会都没有——根 `build.gradle` 的 Test 任务用
`exclude("**/postgresql/**")`、`exclude("**/gaussdb/**")`、`exclude("**/phoenix/**")` 整包排除，
共 18 个测试源文件（`PhoenixTest`、`PostgresqlTest`、`GaussdbTest` 三个测试类加各自的实体、Mapper 与配置）
编译但不运行，既不进用例计数，也不走 T6 的理由登记。
要治它得改主干构建脚本：改成 `@Tag("external-db")` + 按标签过滤，让"跳过"在测试源码里看得见。
在那之前，规范扫描仍会把这些文件扫进来，所以它们的欠账记在基线里，别当成已经解决。

## T6 `@Disabled` 必须说清依赖谁

```java
@Disabled("需要本地 MySQL: jdbc:mysql://127.0.0.1:3306/baomidou，尚未进入 external-db 分层")
```

空理由或只写"临时跳过"不合格。能对应 issue 的带上 issue 号。禁止用 `@Disabled` 压掉一个本来就红的
用例——那是掩盖失败，不是豁免。

## T7 枚举型公共契约配漂移闸门

`DbType`、`IDialect` 实现、注入器、拦截器、生成器策略这类"新增一个值就得改测试"的公共契约，
用全枚举 + 集合相等断言，新增未登记即红。现成参照：
[JdbcUtilsTest](../mybatis-plus/src/test/java/com/baomidou/mybatisplus/test/toolkit/JdbcUtilsTest.java)
（URL → `DbType` 识别）与
[IDialectTest](../mybatis-plus-extension/src/test/java/com/baomidou/mybatisplus/extension/plugins/pagination/dialects/IDialectTest.java)
（方言全量）。注解与枚举成员名也是同类契约——`mybatis-plus-annotation` 的
[IdTypeTest](../mybatis-plus-annotation/src/test/java/com/baomidou/mybatisplus/annotation/IdTypeTest.java)、
[FieldStrategyTest](../mybatis-plus-annotation/src/test/java/com/baomidou/mybatisplus/annotation/FieldStrategyTest.java)
钉的是"配置里写的名字"和"`key >= 3` 才自动生成主键"这条分界线。
SQL 注入器同样按这张清单来：
[DefaultSqlInjectorTest](../mybatis-plus-core/src/test/java/com/baomidou/mybatisplus/core/injector/DefaultSqlInjectorTest.java)
用自建实体 + `MapperBuilderAssistant` 直接驱动注入器，钉住"有主键注入 13 条 / 无主键只剩 8 条"、
13 条语句各自的 `SqlCommandType`、`BaseMapper` 抽象方法必须都能拿到语句、逻辑删除把 3 个 `delete*`
改写成 UPDATE 且 `select*` 带上 `AND deleted=0`、`mapperRegistryCache` 对重复注入的拦截。
注入清单和 `BaseMapper` 方法表是两条会各自漂移的线，所以断言写成集合相等而不是条数。
wrapper 那一侧同理：
[SqlKeywordTest](../mybatis-plus-core/src/test/java/com/baomidou/mybatisplus/core/enums/SqlKeywordTest.java)
钉 24 个关键字各自渲染出的 SQL 文本，
[MatchSegmentTest](../mybatis-plus-core/src/test/java/com/baomidou/mybatisplus/core/conditions/segments/MatchSegmentTest.java)
钉 9 个开关各自认哪些片段——新增一个关键字或改判定条件而没同步整表，就会红。
[MergeSegmentsTest](../mybatis-plus-core/src/test/java/com/baomidou/mybatisplus/core/conditions/segments/MergeSegmentsTest.java)
钉这套开关的消费端：自动补 AND、悬空 and/or 被吃掉、`not()` 只作用下一个条件、
`APPLY` 标记只留原文、group by/having/order by 分流与拼接顺序。
闸门必须用 `SetEquals`/`containsExactlyInAnyOrder`，不能只 `isTrue` 打个警告。

## T8 测试夹具名字全局唯一

框架有按类名/表名缓存的静态状态（`TableInfoHelper` 的 `TABLE_INFO_CACHE` 与按表名后写覆盖前写的
`TABLE_NAME_INFO_CACHE`、`LambdaUtils` 的 `CACHE`）且没有 `clearAll()`，同名实体会跨用例串状态。
新增测试实体类和表名带主题前缀，约定 `zz_<主题>_user` 这种形式，保证全仓不撞。
表名这一半已经由 `sharedFixtureTableNamesStayRegistered` 机器盯住：两个以上测试实体共用一个
`@TableName` 就红，存量的 6 处（同一张表测不同注解/主键类型、kotlin 与多模板的平行夹具、占位表名）
登记在 `REGISTERED_SHARED_TABLE_NAMES`，只准减不准加。

## T9 不许按 JRE 版本门禁

`@EnabledOnJre`、`@DisabledForJreRange` 这类写法在当前工具链上等于永久静默跳过：本地和 CI 都不会跑，
也不会报红。真要跳过就用 `@Disabled` + 原因（T6）；版本兼容性由多 JDK 的构建矩阵验证，
不要在单个用例上门禁。

## T10 发现生产缺陷

主干代码不在测试侧改动范围内。流程：提 issue → 用特征化测试钉住**现状行为**（断言写当前实际结果，
注释标 issue 号），修复上线后把断言改成期望行为。特征化测试不允许写成"跟着实现走"的空断言。

---

## 尚未机器执行

T3 的"每类必带层标签"、T5 的 warn-only 检查（catch 吞异常已经由机器盯住，剩下的"发现问题只打日志"
还散在用例正文里没法机械化判）、T6 的"必须带 issue 号"、T7、T10 目前只靠评审。
这些条目还在建设中，落成检查之前不要拿它们当阻塞合并的理由。

## 例外怎么登记

例外只有两类落点，都在闸门文件顶部：`MAX_ASSERTION_FREE_CASES`、`MAX_DISABLED_CASES`、
`MAX_PACKAGE_MISMATCHES` 三个数量常量，和 `REGISTERED_DUPLICATE_NAMES`、`REGISTERED_THREAD_SLEEP_FILES`、
`REGISTERED_SHARED_TABLE_NAMES` 三个登记表。只准降不准升；要放宽必须在评审里写清为什么。
清零的欠账不留登记表：可转换 JUnit 断言已经是 `MAX_JUNIT_ASSERTION_FILES = 0`，
名为 `*Test` 却不含用例的基类与夹具已经全部改名 `*Support`/`*Fixture`/`*Sample`，改成零容忍。
不引入 ArchUnit/Checkstyle：这些规则都是纯文本级别的写法检查，自研一个文件、零新依赖，
失败信息还能直接指到具体路径。
