# MyBatis-Plus Agent 指南

## 适用范围

- 用户的明确要求优先。子目录有更具体的 `AGENTS.md` 时，按其说明执行。
- 开始前查看工作区状态，阅读相关构建脚本、实现和测试。模块路径见 [`settings.gradle`](settings.gradle)。变更聚焦当前任务，保留用户已有的修改。

## 实现与兼容性

- 日常使用 JDK 21 和 `./gradlew`。编译目标以各模块的 `compileJava.options.release` 和 Kotlin `jvmTarget` 为准。改动编译目标时运行 `verifyBytecodeVersion`。
- 沿用模块现有风格，使用 `./gradlew spotlessApply` 修复 Java 格式。新增依赖前先核对现有依赖，版本沿用根构建脚本或 BOM 的管理方式。
- 修改公共 API、配置或默认行为时，检查源码、二进制和行为兼容性，并同步相关 Javadoc 或使用文档。
- 修改 Mapper、Wrapper、SQL 注入或拦截器时，按影响检查生成的 SQL 和绑定参数，包括空条件、逻辑删除、插件顺序和数据库方言。数据值优先使用参数绑定，动态标识符和原生 SQL 片段须明确其来源及校验边界。
- 修改共享解析逻辑时覆盖各 JSqlParser 适配模块。修改自动配置时检查受影响的 Boot 版本及注册资源，通用用例沿用共享测试源集。涉及 AOT 或反射提示时，参考 [Native Image CI](.github/workflows/native-image.yml)。

## 测试与验证

- 测试写法、标签和分层命令见 [`.github/TESTING.md`](.github/TESTING.md)。修复缺陷时覆盖原触发条件，新增公共方法时配套单元测试。
- 优先运行受影响模块的测试，例如 `./gradlew :mybatis-plus-core:test`。跨模块改动按影响扩大范围，构建命令见 [Gradle CI](.github/workflows/build.yml)。
- 交付前审阅 diff，运行 `git diff --check`。最终回复使用简体中文，说明改动、实际验证结果和相关的验证缺口。

## Git 与 Pull Request

- 常规 PR 以 `3.0` 为目标分支。提交或 PR 说明变更动机、影响范围、兼容性风险和验证结果，按需关联 Issue。
- 推送、创建或合并 PR、改写历史及破坏性 Git 操作需有用户明确授权。

## Review 指南

- 优先检查本次改动引入或暴露的缺陷和回归，尤其是 SQL 语义、并发、资源释放和兼容性。每条问题给出位置、触发条件和影响，注明尚未验证的推断。
- 总结和行内评论使用简体中文，保留技术标识符、错误原文和严重程度标识。没有阻塞问题时也给出结论。个人风格偏好、无关历史问题和未运行全仓测试本身均不构成合并阻塞。
