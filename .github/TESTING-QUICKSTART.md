# 测试速查（先读这一页）

面向「我要新增或修改一个测试」的人。完整规则、编号含义、存量基线见 [TESTING.md](TESTING.md)：
那一页是维护台账，按闸门报错里的编号查规则时才需要读，不用从头看。

## 写一个测试，四步

| 步骤 | 做什么 | 说明 |
|---|---|---|
| 1 | 放对位置 | 被测类在哪个模块，测试就放该模块的 `src/test/java`，**包名与被测类一致**；跨模块的行为测试放聚合模块 `mybatis-plus`（它没有 `src/main`，专职组装别的模块） |
| 2 | 起对名字 | 类名 `<被测类>Test`，简单名全仓唯一；Java 与 Kotlin 各测一套语法面时用 `Kt` 前缀消歧，不要合并 |
| 3 | 打一个层标签 | 类上加 `@Tag("unit")`（绝大多数），见下表选层 |
| 4 | 写真断言 | JUnit 5 `@Test` + AssertJ `assertThat(...)`；不许用 `print` 代替断言、不许裸 `assert`、不许 JUnit 4、不许 `Thread.sleep` 当同步 |

格式不用手工管：`./gradlew spotlessApply`（pre-commit hook 会跑 `spotlessCheck`）。

层标签只有五个，选一个：

| 标签 | 什么时候用 |
|---|---|
| `unit` | 默认。纯逻辑、SQL 拼装、工具类 |
| `integration` | 需要 Spring / H2 / 较重的环境装配 |
| `external-db` | 要连真实外部数据库（达梦、Oracle、PostgreSQL、MySQL 等） |
| `stress` | 性能、压力测试 |
| `build-metadata` | 校验构建产物。目前是 6 个 `XxxGeneratedPomTest`，断言发布 pom 的 scope / optional / version |

## 跑测试

| 想干什么 | 命令 |
|---|---|
| 日常全量 | `./gradlew test` |
| 单个类（开发时最常用） | `./gradlew :mybatis-plus-core:test --tests "*StringUtilsTest*"` |
| 按层跑 | `./gradlew testUnit` / `testIntegration` / `testExternalDb` / `testBuildMetadata` / `testStress` |
| 自定义标签组合 | `./gradlew test -PincludeTags=unit,integration` |
| 只跑规范闸门 | `./gradlew :mybatis-plus-core:test --tests "*TestConventionTest*"` |
| 格式检查 / 修复 | `./gradlew spotlessCheck` / `./gradlew spotlessApply` |

默认 `test` 会排除 `external-db`、`stress`、`build-metadata` 三层，所以有两点必须记住：

- 新写的 `build-metadata` 测试要靠 `./gradlew testBuildMetadata` 才跑得到；CI 里对应的是
  `Verify published pom contracts` 这一步，它是该层唯一的线上入口。
- **某层一个用例都没有时，分层任务照样显示 SUCCESS**。漏打标签不会让 CI 变红，别拿绿灯当「跑到了」的证据；
  T3 闸门已改成按「有没有用例」筛测试源，含用例的类没带层标签会直接红。

## 两个固定例外

- `mybatis-plus-up`：测试编译目标是 `--release 21`，而 CI 矩阵含 JDK 17，且它没有任何 build-metadata 用例。
  因此所有全仓命令都要排除它（`-x :mybatis-plus-up:...`），与主构建 `Build with Gradle Wrapper` 一致。
- 共享测试源 `src/test-*`：只存一份，靠各模块 `build.gradle` 里的 `sourceSets.test.java.srcDir` 接线才会被编译。
  漏接不会报「测试变少」，是静默失效，由 T11 闸门逐个核对引用方。

## 为什么发布 pom 也要写测试

发布用的 `pom.xml` 是 Gradle 从 `dependencies {}` 自动生成的，一个依赖的 `scope`、`optional`、版本决定了
用户引包之后 classpath 里有什么。把 `api` 写成 `implementation`、漏标 `optional=true`、升级时带歪版本号，
这些错误在编译和单元测试里**全绿**，只在生成出来的 pom 上才看得见。

所以这 6 个契约测试读 `build/publications/maven/pom-default.xml` 逐项断言（例如 core 的 `cglib` 必须是
`compile` 且 `optional=true`）。它们验证的是构建产物而不是代码行为，且依赖
`generatePomFileForMavenPublication` 先产出 pom，因此单独归在 `build-metadata` 层。
