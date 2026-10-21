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
| T1 | 测试与被测类同模块、包镜像 | 冻结 | 66 处缺口 |
| T2 | 命名：`<被测类>Test`、简单名全仓唯一、只用 `Test` 后缀 | 机器 + 冻结 | 后缀零违规；历史同名 27 组 |
| T3 | 每个测试类属于一个层 | 未执行（仅词表是机器） | 仓库里还没有 `@Tag` |
| T4 | 只用 AssertJ，禁裸 `assert`、禁 JUnit 4 | 机器 | 零违规 |
| T5 | 每个用例必须有断言；禁 `Thread.sleep` 当同步 | 冻结 | 零断言 30（含 6 个已禁用用例）、无用例 `*Test` 9、sleep 1 |
| T6 | `@Disabled` 必须写清原因 | 机器 + 冻结 | 无理由零违规；禁用 6 |
| T7 | 枚举型公共契约必须配漂移闸门 | 未执行 | — |
| T8 | 测试实体类名/表名全局唯一 | 未执行 | — |
| T9 | 禁 `@EnabledOnJre` 式永久跳过 | 机器 | 零违规 |
| T10 | 生产缺陷走 issue + 特征化测试 | 未执行 | — |

## T1 测试放对位置

测试类的包应与被测类的包一致，且放在被测类所在模块。跨模块的行为测试放聚合模块
`mybatis-plus`（它没有 `src/main`，专职组装别的模块）。理由：改一个类时能靠包名找到它的测试，
模块级 `./gradlew :xxx:test` 才会真正覆盖到该模块。

## T2 一个被测类只有一处测试

类名 `<被测类>Test`，简单名在全仓唯一。新增测试类不允许用 `*Tests`、`*TestCase`、`*IT` 后缀。
多版本（JSqlParser 4.9/5.0/5.2、Boot 2/3/4）的支持靠共享源——公共基类或 `sourceSets` 复用同一份
测试代码，**不要复制文件**。现存 27 组同名类多数是 starter 与 JSqlParser 版本支持模块的整文件复制，
去重一组就从 `REGISTERED_DUPLICATE_NAMES` 里删掉对应项。

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
- 存量里的 JUnit 4 风格断言不做批量转换，改到哪个文件就顺手换成 AssertJ。

## T5 绿必须有意义

一个用例要么断言，要么删掉。禁止这几种"永远绿"的写法：

- 零断言的 `@Test`（只调用不验证）；
- `try { ... } catch (Exception e) { log... }` 吞异常，不 `fail()`；
- 检查发现问题只打日志不失败（warn-only）；
- 用 `main()` 冒充测试；确需保留演示入口就命名 `*Sample` 并明确它不是用例；
- 用 `Thread.sleep` 当同步手段。

基线数字是存量欠账：补一个断言就把常量调小一格，禁止调大。用例调用本文件或测试基类里会断言的辅助方法
（如 `BaseWrapperTest#logSqlWhere`）算已经断言，不计入 30。名为 `*Test` 却不含用例的基类和夹具
登记在 `REGISTERED_CASE_FREE_FILES`，夹具改名成 `*Fixture`/`*Support` 可以直接从登记表消失，
不必凑数加用例。

## T6 `@Disabled` 必须说清依赖谁

```java
@Disabled("需要本地 MySQL: jdbc:mysql://127.0.0.1:3306/baomidou，尚未进入 external-db 分层")
```

空理由或只写"临时跳过"不合格。能对应 issue 的带上 issue 号。禁止用 `@Disabled` 压掉一个本来就红的
用例——那是掩盖失败，不是豁免。

## T7 枚举型公共契约配漂移闸门

`DbType`、`IDialect` 实现、注入器、拦截器、生成器策略这类"新增一个值就得改测试"的公共契约，
用全枚举 + 集合相等断言，新增未登记即红。现成参照：
[JdbcUtilsTest](../mybatis-plus-core/src/test/java/com/baomidou/mybatisplus/test/toolkit/JdbcUtilsTest.java)
（URL → `DbType` 识别）与
[IDialectTest](../mybatis-plus-extension/src/test/java/com/baomidou/mybatisplus/test/extension/plugins/pagination/dialects/IDialectTest.java)
（方言全量）。闸门必须用 `SetEquals`/`containsExactlyInAnyOrder`，不能只 `isTrue` 打个警告。

## T8 测试夹具名字全局唯一

框架有按类名/表名缓存的静态状态（`TableInfoHelper` 的 `TABLE_INFO_CACHE`、`LambdaUtils` 的 `CACHE`）
且没有 `clearAll()`，同名实体会跨用例串状态。新增测试实体类和表名带主题前缀，约定
`zz_<主题>_user` 这种形式，保证全仓不撞。

## T9 不许按 JRE 版本门禁

`@EnabledOnJre`、`@DisabledForJreRange` 这类写法在当前工具链上等于永久静默跳过：本地和 CI 都不会跑，
也不会报红。真要跳过就用 `@Disabled` + 原因（T6）；版本兼容性由多 JDK 的构建矩阵验证，
不要在单个用例上门禁。

## T10 发现生产缺陷

主干代码不在测试侧改动范围内。流程：提 issue → 用特征化测试钉住**现状行为**（断言写当前实际结果，
注释标 issue 号），修复上线后把断言改成期望行为。特征化测试不允许写成"跟着实现走"的空断言。

---

## 尚未机器执行

T3 的"每类必带层标签"、T5 的吞异常与 warn-only、T6 的"必须带 issue 号"、T7、T8、T10 目前只靠评审。
这些条目还在建设中，落成检查之前不要拿它们当阻塞合并的理由。

## 例外怎么登记

例外只有两类落点，都在闸门文件顶部：`MAX_ASSERTION_FREE_CASES`、`MAX_DISABLED_CASES`、
`MAX_PACKAGE_MISMATCHES` 三个数量常量，和 `REGISTERED_DUPLICATE_NAMES`、`REGISTERED_CASE_FREE_FILES`、
`REGISTERED_THREAD_SLEEP_FILES` 三个登记表。只准降不准升；要放宽必须在评审里写清为什么。
不引入 ArchUnit/Checkstyle：这些规则都是纯文本级别的写法检查，自研一个文件、零新依赖，
失败信息还能直接指到具体路径。
