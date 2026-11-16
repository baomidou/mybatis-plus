# JSqlParser 多版本源码维护

四个版本模块保留各自的 JSqlParser 依赖和发布坐标，分别编译、测试和发布。

- `src/main-shared/java`：四个版本共用的生产源码，由各版本模块的 `main` 源集引用，不单独编译或发布。
- 各版本模块的 `src/main/java`：与 JSqlParser 版本相关的实现。
- `mybatis-plus-jsqlparser-common`：继续承载不依赖特定 JSqlParser 版本的公共模块。

共享类保持原 Java 包名；同一个类不能同时存在于共享目录和消费模块的源码目录。
修改共享源码后，需运行四个版本模块的测试，并保持 4.9 的 Java 8、5.x 的 Java 11 编译目标。
源码包与 Javadoc 使用各版本模块的 main 源集，因此也包含共享源码。
新增版本模块时，同时更新 `TestConventionTest.scanMainPackages` 中的共享源码消费模块清单。

## AST 版本适配

`BaseMultiTableInnerInterceptor`、`DataPermissionInterceptor` 和 `PaginationInnerInterceptor` 的主体在共享目录维护。
各版本在 `extension.plugins.inner` 包中提供包私有的 `JsqlParserAdapter`，由编译期选择对应实现，不使用反射或运行时版本判断。
适配范围包括括号表达式、JOIN 右侧节点、CTE 查询节点和表达式 Visitor 签名。

适配方法必须保留原版本的遍历顺序和节点语义。例如，5.x 的括号提取沿用第一个元素的处理方式，不能顺带扩展为列表遍历。
涉及公开方法签名、继承扩展点或业务行为差异的类继续保留分版本实现，不通过源码聚合改变其契约。
