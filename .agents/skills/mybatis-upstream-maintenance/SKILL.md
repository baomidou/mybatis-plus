---
name: mybatis-upstream-maintenance
description: 维护 MyBatis-Plus 仓库 mybatis-plus-up 模块的上游源码副本、版本记录、增强源码映射和补丁。当用户要求执行或调整 up、patch、check，复制上游类，维护 Boot 2/3/4 多版本源码或验证独立编译时使用；普通业务 Mapper、SQL 或数据库功能开发不适用。
---

# MyBatis-Plus 上游源码维护

以普通 Java 源码和标准 diff 展示改动，复用仓库已有构建、格式化与校验机制。根据用户要求选择操作，不把一次维护任务扩大为整个仓库的升级。

## 先确认当前实现

定位包含 `mybatis-plus-up` 的仓库，从仓库根目录操作。先读取以下文件，实际内容优先于本 skill 中的示例：

- `mybatis-plus-up/build.gradle`：依赖来源、分组、sourceSet 和任务编排。
- `mybatis-plus-up/upstream/*.properties`：按上游分组拆分的版本、本地模块名和类对应关系。
- `mybatis-plus-up/src/main/java/com/baomidou/mybatisplus/SourcePatchTool.java`：记录解析、提取、diff 与补丁还原校验。
- 根项目 `build.gradle`：实际依赖版本变量及现有 Spotless 配置。
- 对应 core、spring 或 starter 项目的 `build.gradle`：实际依赖配置。

检查现有改动和相关源码后再实施。仅分析方案或解释代码时，不刷新源码、不生成补丁。

## 目录与记录

上游副本放在 `mybatis-plus-up/src/main/java`，保留源码里的原包名、类名和许可证。core 副本放在 `core` 分组目录，Boot 多版本使用稳定的 `boot2`、`boot3`、`boot4` 存储目录，Spring 多版本使用 `spring2`、`spring3`、`spring4`；这些前缀不加入 Java 的 `package`。

当前 Boot 分组目录为：

```text
src/main/java/boot2/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java
src/main/java/boot3/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java
src/main/java/boot4/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java
```

版本和关系按组写入 `upstream/*.properties`，目前包括 `core.properties`、`spring2.properties`、`spring3.properties`、`spring4.properties`、`boot2.properties`、`boot3.properties`、`boot4.properties`。文件名标识上游分组，每个文件独立解析，版本只更新回所属文件。内容只使用 `version`、`moudle`、`上游全限定类名=本地全限定类名`；保留用户要求的字段拼写 `moudle`，不再添加 `.copy`、`.mapping` 或分组前缀。

```properties
version=2.3.2
moudle=mybatis-plus-boot-starter
org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration=com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration
```

示例版本不作为固定要求；以当前目标项目实际解析出的版本为准。`moudle` 使用本地模块的唯一项目名，必要时使用限定的 Gradle 项目路径。工具根据模块和全限定类名定位 `src/main/java` 中的增强实现，标准 `.patch` 与上游副本同目录、同文件名主体。

Spring 2/3/4 均复制 `org.mybatis.spring.SqlSessionFactoryBean`，并映射到 `mybatis-plus-spring` 中同一个 `MybatisSqlSessionFactoryBean`。不同分组允许共享增强类，同一分组仍禁止重复增强目标。

只需复制的类写成 `上游全限定类名=?`；它们参与 `up`、版本与副本检查及对应源集编译，不参与增强补丁生成与校验，更新版本记录时保留 `?`。配置沿用 `Properties.load` 读取，没有 `=` 的条目按空值处理。其他条目表示需要维护补丁，复制阶段可以把本地类值暂时留空，此时只执行 `up`；不要为此引入额外模式。`patch`、`check` 要求除值为 `?` 以外的对应关系完整，遇到空值应提示补全而不是跳过。当前 Boot 分组只复制 `MybatisAutoConfiguration`，对应各 starter 已有的 `MybatisPlusAutoConfiguration`。新增类时按用户范围调整记录与 sourceSet 包含规则。

## 三个操作入口

统一使用现有 `up`、`patch`、`check`。不要新增单独的复制、提取、版本更新或校验操作任务。sourceSet 自动生成的编译任务作为内部依赖挂到 `check`。

### up：刷新基线

根据每个文件列出的上游全限定类名，从实际依赖源码 JAR 提取对应 Java 文件，再调用根项目现有 `spotlessApply`。本地类值是否填写不影响提取；只有提取与格式化成功后，才能更新记录版本。

- 只复制用户指定或已记录的类，不为了补齐编译依赖复制整个上游模块。
- 上游原包名保持不变，增强实现放在用户指定的目标项目。
- 刷新基线会覆盖副本；先检查副本中是否有尚待保留的人工改动。
- 提取、格式化失败时，不把版本记录推进到新版本，也不继续生成升级补丁。

### patch：生成差异

先执行根项目现有 `spotlessApply`，格式化成功后读取每条本地类值不为 `?` 的类对应关系的上游副本和增强源码，生成可还原目标源码的标准 unified diff。值为 `?` 的类不参与增强补丁生成。`patch` 必须依赖格式化任务，而不只是声明执行顺序；格式化失败时不能生成补丁。所有参与增强补丁的关系必须完整，本地类未填或文件缺失时应失败并提示，不自动略过。

维护增强实现时，直接编辑目标 Java，再运行 `patch` 和 `check`。格式化使用根项目已有范围，可能调整其中的 Java 文件，但单独 `patch` 不重新提取上游副本或更新版本记录。补丁可以包含包名、类名、导入和业务代码的实际差异。相同源码对应空补丁，属于合法结果。

### check：只读验证

核对记录版本与对应项目解析出的依赖版本；检查所有登记的副本存在；仅对本地类值不为 `?` 的增强映射验证补丁与两份源码一致，并在临时目录应用补丁，确认能还原增强源码。值为 `?` 的类不要求本地映射或增强补丁。同时运行现有测试和 core、Spring、Boot 分组编译。

Spring、Boot 对相邻两组都登记的同名上游类生成 2→3、3→4 的源码差异，保存在较新分组的 `类名.upstream.patch`，不依赖增强映射是否为 `?`。由 `patch` 生成，`check` 校验差异并验证补丁能还原较新源码，不新增任务入口。

`check` 不调用 `up`，不刷新副本、patch 或版本记录。检查失败时指出具体分组、文件及原因，按用户授权修复；不要通过自动重写补丁、修改记录版本或忽略失败使检查通过。

## 多版本编译隔离

同一个上游模块的多个版本分别使用独立源码 configuration，且不合并到同一依赖图中选择版本。目录分组只解决文件覆盖，不能解决依赖冲突和同包同名类的编译冲突。

每组使用独立 sourceSet、源码根目录、编译 classpath 和输出目录。主 `main` 源集排除这些分组，主工具 JAR 和运行时 classpath 不包含它们的编译输出。

- `boot2`：源码根目录 `src/main/java/boot2`，当前编译 release 为 8。
- `core`：源码根目录 `src/main/java/core`，按 `core.properties` 的上游类包含源码，当前编译 release 为 8；二进制 MyBatis 依赖提供其他类，编译 classpath 与目标 core 模块的 `compileClasspath` 保持一致。
- `boot3`、`boot4`：分别使用同名目录，当前编译 release 为 17。
- `spring2`、`spring3`、`spring4`：分别使用同名目录和独立依赖图，`mybatis-spring` 版本使用根项目的 `mybatisSpringVersion`、`mybatisSpring3Version`、`mybatisSpring4Version`；其他编译依赖由对应 Boot 版本的 BOM 管理。Spring 2 的 release 为 8，Spring 3/4 为 17。
- Spring、Boot 版本从各组隔离的 `*Upstream` configuration 解析；core 按目标 core 模块的 `compileClasspath` 解析。
- `MybatisAutoConfiguration` 位于 `mybatis-spring-boot-autoconfigure` 的源码 JAR，不能把聚合 starter 的源码 JAR 当成该类的来源。
- 同包的其他类和框架依赖由相应版本的二进制依赖提供，不额外复制 Java 文件。
- 区分“工具构建成功”“分组源码编译成功”和“补丁校验成功”；排除编译的副本不能声称已通过编译验证。

## 格式化与执行

不在 `up` 模块重复应用 Spotless 插件或重新定义格式化配置。根项目已有规则覆盖 `mybatis-plus-up/src/**/*.java`。保持“提取 → 原有格式化 → 更新版本”的顺序，避免为排序新增操作入口。

普通 `up` 和 `patch` 使用根项目既有格式化范围。局部验证需要限定影响时，可用已有的 `spotlessIdeHook` 参数选择相关 Java 文件的绝对路径，多个路径以逗号分隔；不为此修改全局目标范围。

使用当前构建所需的 JDK 21，先确认本机安装路径，必要时仅为本次终端设置 `JAVA_HOME`。不要为了运行这些任务擅自升级 Lombok 或修改系统默认 JDK。Windows 在仓库根目录执行：

```powershell
.\gradlew.bat :mybatis-plus-up:up
.\gradlew.bat :mybatis-plus-up:patch
.\gradlew.bat :mybatis-plus-up:check
```

只执行用户当前需要的操作：只读检查只运行 `check`；修改增强实现后运行 `patch`、`check`；升级上游时先运行 `up`，审阅新基线并迁移增强实现，然后再生成和检查补丁。

升级时不要直接用“新版基线 + 旧增强实现”覆盖旧补丁，否则会把上游新增代码记成删除。文本能合并或补丁能应用，不代表业务行为已正确迁移；涉及分页、Wrapper 参数别名或自动配置行为的变化，应验证对应行为。

## 验收与反馈

根据本次改动验证任务编排、版本匹配、补丁还原或相关分组编译。必要时比较单独 `check` 前后的源码、patch 和版本记录哈希；调整编译隔离时检查主 JAR 不包含分组类。

用中文简要说明处理的分组、类、版本、文件位置、执行过的检查及结果。复制或编译任务未完成时明确说明，不能把任务注册成功当作操作成功。
