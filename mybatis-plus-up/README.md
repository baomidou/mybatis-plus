# 上游源码与补丁维护

本模块保存格式化后的上游源码副本，通过普通 Java 源码和标准 diff 展示增强实现的改动。
统一使用 `up`、`patch`、`check` 三个操作入口。

## 快速开始

开启新的维护对话时，可以使用以下开场模板，调用项目内的中文 skill：

```text
$mybatis-upstream-maintenance

继续维护 mybatis-plus-up 模块，先读取项目内 skill、README 和现有配置，沿用当前的 up / patch / check 流程。

本次需求：……
```

在“本次需求”中说明要新增的类、调整的分组或任务行为。
如果只讨论方案，补充“先分析，不修改文件、不执行任务”。
skill 位于仓库的 `.agents/skills/mybatis-upstream-maintenance/SKILL.md`。

## 版本与对应关系

`upstream/*.properties` 按分组记录上游依赖版本和源码对应关系，目前包括：

- `core.properties`
- `spring2.properties`、`spring3.properties`、`spring4.properties`
- `boot2.properties`
- `boot3.properties`
- `boot4.properties`

文件名标识上游分组，每个文件只包含 `version`、`moudle` 和“上游全限定类名=本地全限定类名”。
各组独立维护，因此同一个上游类可以登记在不同分组中。

```properties
version=2.3.2
moudle=mybatis-plus-boot-starter
org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration=com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration
```

`moudle` 保留当前约定的拼写，值为本地 Gradle 模块的唯一项目名，也支持限定的 Gradle 项目路径。
工具根据模块名和全限定类名定位增强源码，不使用 `.copy` 或 `.mapping` 前缀。

目前登记的类包括 `MapperMethod`、`MapperProxy`、`SqlSessionFactoryBean`，以及三个 Boot 版本的
`MybatisAutoConfiguration`。新增对应关系时，编辑所属分组的 properties 文件，并确认编译源集的包含规则。

只复制上游源码时写成 `上游全限定类名=?`：跳过增强补丁生成与校验，仍检查版本、副本并参与对应源集编译。
本地类值留空时只能执行 `up`；执行 `patch`、`check` 前必须补全对应关系。

## 源码与格式化

上游副本统一放在 `src/main/java` 下，保留原包名、类名和许可证。分组目录不加入 Java 的 `package`：

```text
src/main/java/core/org/apache/ibatis/binding/MapperMethod.java
src/main/java/core/org/apache/ibatis/binding/MapperProxy.java
src/main/java/spring2/org/mybatis/spring/SqlSessionFactoryBean.java
src/main/java/spring3/org/mybatis/spring/SqlSessionFactoryBean.java
src/main/java/spring4/org/mybatis/spring/SqlSessionFactoryBean.java
src/main/java/boot2/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java
src/main/java/boot3/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java
src/main/java/boot4/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java
```

标准 `.patch` 与对应上游 Java 文件同目录、同文件名主体。
Spring、Boot 的 2→3、3→4 源码差异保存为较新分组目录中的 `.upstream.patch`，由 `patch` 生成、`check` 校验。
上游副本不参与 `main` 源集的编译和打包，避免覆盖工具依赖中的同名类；各分组通过独立源集编译。

格式化直接复用根项目已有的 Spotless 任务，其范围已覆盖 `mybatis-plus-up/src/**/*.java`。
本模块不重复应用 Spotless 插件，也不另设格式化配置。

## 三个任务

使用 JDK 21，在仓库根目录执行：

```shell
./gradlew :mybatis-plus-up:up
./gradlew :mybatis-plus-up:patch
./gradlew :mybatis-plus-up:check
```

- `up`：从实际依赖的源码 JAR 中提取已登记的上游类，调用根项目 `spotlessApply`，格式化成功后更新版本记录。
  源码 JAR 的版本必须与对应 core、spring 或 starter 模块实际解析出的依赖版本一致。此任务不生成补丁。
- `patch`：先执行根项目 `spotlessApply`，格式化成功后读取每组对应关系中的两份 Java 源码，
  生成标准 unified diff，并保存到上游副本同级目录。格式化沿用根项目既有范围，可能调整其中的 Java 文件。
  不重新提取上游副本，也不更新版本记录；格式化失败时不生成补丁。
  补丁头两侧均使用增强源码的仓库相对路径，包名、类名变更仍保留在差异中。
- `check`：核对记录版本与实际依赖版本，检查补丁是否与两份源码一致，再在临时目录应用补丁，确认能够还原增强源码。
  同时运行模块测试，并编译 core、Spring、Boot 各源集。不会刷新副本、补丁或版本记录。

需要安装 Git 并加入 `PATH`。补丁生成使用 Git 的 diff 引擎，还原校验在隔离的临时目录中使用 Git 的补丁引擎。
比较源码和生成补丁时，换行符统一为 LF。

## 修改与升级

直接编辑目标模块中的增强 Java 实现，然后执行 `patch`、`check`；`patch` 会先调用现有格式化任务。
当两份源码完全一致时，空补丁是合法结果。

修改上游依赖版本后，先执行 `up` 并审阅基线变化，再将相关上游改动迁移到增强实现，最后生成和检查补丁。
不要直接用“新版基线 + 旧增强实现”覆盖旧补丁，否则上游新增代码会被记录为删除。

## 独立编译

### core

`core` 源集以 `src/main/java/core` 为源码根目录，只编译 `core.properties` 登记的上游类。
其他类由上游 MyBatis 二进制依赖提供，编译依赖版本与目标 core 模块的 `compileClasspath` 保持一致。

编译使用 `release = 8`，输出到 `build/classes/java/core`。`coreClasses` 已纳入 `check`，
输出不进入主工具 JAR 或其运行时 classpath。

### Spring 2/3/4

`spring2`、`spring3`、`spring4` 分别维护 `mybatis-spring` 2/3/4 的 `SqlSessionFactoryBean`，
均与 `mybatis-plus-spring` 中的 `MybatisSqlSessionFactoryBean` 生成补丁。
各组独立解析源码和编译依赖，版本使用根项目对应的 `mybatisSpringVersion`、`mybatisSpring3Version`、`mybatisSpring4Version`。
编译依赖由对应 Boot 版本的 BOM 管理；Spring 2 使用 `release = 8`，Spring 3/4 使用 `release = 17`。

### Boot 2/3/4

每组复制 properties 登记的上游类。`MybatisAutoConfiguration` 的增强补丁
对比对应 starter 中已有的 `MybatisPlusAutoConfiguration`。

各组使用独立的源集、源码 JAR configuration、编译 classpath 和输出目录，避免同包同名类及依赖版本冲突。
版本从各组独立的 `*Upstream` 配置解析，该配置也为编译依赖提供版本约束。
同包的其他类和框架依赖由对应版本的上游二进制 starter 提供，无需额外复制源码。

编译输出分别位于 `build/classes/java/boot2`、`boot3`、`boot4`，不进入主工具 JAR 或其运行时 classpath。
Boot 2 使用 `release = 8`，Boot 3、Boot 4 使用 `release = 17`。

`check` 包含这些源集的编译，但不会自动刷新副本；副本缺失时检查失败，需要执行 `up`。
没有额外的自定义复制、提取或校验任务，源集编译任务由 Gradle 自动生成。
