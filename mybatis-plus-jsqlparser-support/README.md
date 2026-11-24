# JSqlParser 多版本源码维护

四个版本模块保留各自的 JSqlParser 依赖和发布坐标，分别编译、测试和发布。

- `src/main-shared/java`：四个版本共用的生产源码，由各版本模块的 `main` 源集引用，不单独编译或发布。
- `src/main-shared-5x/java`：仅 5.0、5.2、5.4 的 `main` 源集引用，存放 5.x 共用的生产源码，例如 `DynamicTableNameVisitor`。
- 各版本模块的 `src/main/java`：与 JSqlParser 版本相关的实现。
- `src/test-shared/java`：四个版本共用的测试源码，由各版本模块的 `test` 源集引用，不单独编译或发布。

共享类保持原 Java 包名；同一个类不能同时存在于共享目录和消费模块的源码目录。
修改共享源码后，需运行四个版本模块的测试，并保持 4.9 的 Java 8、5.x 的 Java 11 编译目标。
源码包与 Javadoc 使用各版本模块的 main 源集，因此也包含共享源码。
新增版本模块时，同时更新 `TestConventionTest.scanMainPackages` 中的共享源码消费模块清单。

## AST 版本适配

`BaseMultiTableInnerInterceptor`、`DataPermissionInterceptor` 和 `PaginationInnerInterceptor` 的主体在共享目录维护。
各版本在 `extension.parser` 包中提供 `JsqlParserAdapter`，由编译期选择对应实现，不使用反射或运行时版本判断。
适配范围包括括号表达式、JOIN 右侧节点、CTE 查询节点和表达式 Visitor 签名。

适配方法必须保留原版本的遍历顺序和节点语义。例如，5.x 的括号提取沿用第一个元素的处理方式，不能顺带扩展为列表遍历。
涉及公开方法签名、继承扩展点或业务行为差异的类继续保留分版本实现，不通过源码聚合改变其契约。

## 序列化缓存

FST、Fury 和 Fory 的工厂及 Caffeine 缓存实现由四个版本共享。每个版本的
`JsqlParserCacheTypes` 维护有序的 AST 注册清单，FST 只注册其中标为共享的类型，
Fury 和 Fory 注册全部类型。修改清单时需保留已有类型的注册顺序，以免改变缓存格式。

### 耗时观测

`JsqlParserSerialPerfTest`（test-shared，四版本共享）是序列化耗时冒烟测试，
对比 jdk、FST、Fury、Fory 四种序列化，noSerial 段为纯解析基线。
每段耗时以 INFO 日志输出（全仓测试日志统一走根目录 `logback.xml`）。
单次运行、JIT 未充分预热，数值仅供量级参考。

想要耗时对比表：把下面提示词发给有本仓库访问能力的 AI，它会自行运行测试、提取输出并直接给出表格：

```text
汇总 mybatis-plus 四个 JSqlParser 版本模块（mybatis-plus-jsqlparser-4.9 / -5.0 / -5.2 / -5.4，位于
mybatis-plus-jsqlparser-support 下）的 JsqlParserSerialPerfTest 耗时：
对每个模块运行该测试，从各模块 build/test-results/test/TEST-*JsqlParserSerialPerfTest.xml 的 system-out
提取 logger 为 com.baomidou.mybatisplus.test.extension.parser.JsqlParserSerialPerfTest 的 INFO 行，
行格式为 "框架 serialize N ms / deserialize N ms"，其中 noSerial 行是解析基线。
汇总成一张 Markdown 表格：行按 4.9、5.0、5.2、5.4 顺序排列，
列为 parse 基线耗时，以及 jdk、fst、fury、fory 各框架的序列化/反序列化耗时（单位 ms）。
表格之后用不超过 3 行总结：哪个框架整体最快最稳、哪些框架在哪个方向明显偏慢、单次运行观测的局限。
```
