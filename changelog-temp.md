fix: 修复在选择springdoc文档注释时entity描述异常
fix: 在主键的`IdType`为`AUTO`的情况下,`Table#getAllInsertSqlColumnMaybeIf("xx.")`所生成sql错误问题
feat: `wrapper#apply`支持配置`mapping`比如`column={0,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler}`
fix: 租户插件支持`update set subSelect`的情况
feat: `updateWrapper#setSql`方法支持`动态入参`参考`wrapper#apply`方法
perf: `ktWrapper`加强泛型限制
fix: 修复高版本Jdk提示非法反射警告(Illegal reflective access by com.baomidou.mybatisplus.core.toolkit.SetAccessibleAction)
fix: 修复高版本Jdk插件动态代理反射错误 (Unable to make field protected java.lang.reflect.InvocationHandler java.lang.reflect.Proxy.h accessible)
fix: 修复路径替换将原有的“.”替换成了文件分隔符“/”
fix: 修复Beetl模板引擎无法生成注释
fix: 修复Types.DOUBLE类型无法映射
fix: 修复转换父类公共字段报错
fix: 修复生成器无法通过cfg.取值
fix: 修复单元测试下MockBean时事务回滚失败
fix: 修复Warpper类nonEmptyOfWhere方法命名不规范,导致Ognl未正确缓存带来的执行开销
opt: 增强参数填充处理器,防止因参数名称与填充名称一致类型不匹配导致转换错误
opt: 优化方法注入,去除SelectPage,SelectMapsPage,SelectByMap,DeleteByMap注入
opt: 减少MappedStatement堆内存占用
opt: 解决PluginUtils重复获取元数据带来的性能消耗
opt: 注入方法去除多余的换行符
opt: 去除SqlRunner持有的sqlSessionFactory变量
opt: 解决Sequence初始化多次问题(自定义情况下可不创建默认主键生成器)
feat: 增加Sequence初始化debug日志
feat: 参数填充器支持多参数填充
feat: BaseMapper新增selectMaps(page, wrapper)与selectList(page, wrapper)方法
feat: 升级mybatis至3.5.13,mybatis-spring至2.1.1
docs: 修正DdlHelper注释错误
