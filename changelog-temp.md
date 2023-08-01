fix: 修复在选择springdoc文档注释时entity描述异常
fix: 在主键的`IdType`为`AUTO`的情况下,`Table#getAllInsertSqlColumnMaybeIf("xx.")`所生成sql错误问题
perf: `wrapper#apply`支持配置`mapping`比如`column={0,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler}`
