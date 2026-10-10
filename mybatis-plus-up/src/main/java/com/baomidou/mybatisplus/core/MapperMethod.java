package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.binding.MapperMethod}
 *
 * @author miemie
 * @since 2025/9/1
 */
public class MapperMethod extends OverwriteFile {

    public MapperMethod() {
        setOverwriteClass("com.baomidou.mybatisplus.core.override.MybatisMapperMethod2");
        setImports("""
            import com.baomidou.mybatisplus.core.metadata.IPage;
            import com.baomidou.mybatisplus.core.toolkit.Assert;
            import com.baomidou.mybatisplus.core.conditions.Wrapper;
            import java.lang.annotation.Annotation;
            import org.apache.ibatis.binding.MapperMethod;
            """);
        addStep(i -> i.source("ParamMap").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        addStep(i -> i.source("SqlCommand").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        addStep(i -> i.source("MethodSignature").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        addStep(i -> i.source("""
            private final SqlCommand command;
            private final MethodSignature method;
            """).target("""
            private final MapperMethod.SqlCommand command;
            private final MapperMethod.MethodSignature method;
            private final Map<Integer, String> wrapperParamsAliasNameMap;
            """).operate(Overwrite.Operate.COVERAGE));
        addStep(i -> i.source("""
                this.command = new SqlCommand(config, mapperInterface, method);
                this.method = new MethodSignature(config, mapperInterface, method);
                """).target("""
                this.wrapperParamsAliasNameMap = this.getWrapperParamsAliasNameMap(method);
                this.command = new MapperMethod.SqlCommand(config, mapperInterface, method);
                this.method = new MapperMethod.MethodSignature(config, mapperInterface, method);
                """).operate(Overwrite.Operate.COVERAGE));
        addStep(i -> i.source("""
                Object param = method.convertArgsToSqlCommandParam(args);
                result = sqlSession.selectOne(command.getName(), param);
                if (method.returnsOptional() && (result == null || !method.getReturnType().equals(result.getClass()))) {
                    result = Optional.ofNullable(result);
                }
                """).target("""
                if (IPage.class.isAssignableFrom(method.getReturnType())) {
                    result = executeForIPage(sqlSession, args);
                } else {
                    Object param = method.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectOne(command.getName(), param);
                    if (method.returnsOptional()
                        && (result == null || !method.getReturnType().equals(result.getClass()))) {
                        result = Optional.ofNullable(result);
                    }
                }
                """).operate(Overwrite.Operate.COVERAGE));
        addStep(i -> i.source("method.convertArgsToSqlCommandParam(args);")
                .target("this.convertArgsToSqlCommandParam(args);")
                .operate(Overwrite.Operate.REPLACE));
        addStep(i -> i.source("""
            @SuppressWarnings("all")
            private <E> Object executeForIPage(SqlSession sqlSession, Object[] args) {
                IPage<E> result = null;
                for (Object arg : args) {
                    if (arg instanceof IPage) {
                        result = (IPage<E>) arg;
                        break;
                    }
                }
                Assert.notNull(result, "can't found IPage for args!");
                Object param = this.convertArgsToSqlCommandParam(args);
                List<E> list = sqlSession.selectList(command.getName(), param);
                result.setRecords(list);
                return result;
            }
            """).operate(Overwrite.Operate.APPEND_METHOD));
        addStep(i -> i.source("""
            private Map<Integer, String> getWrapperParamsAliasNameMap(Method method) {
                Annotation[][] paramAnnotations = method.getParameterAnnotations();
                Class<?>[] parameterTypes = method.getParameterTypes();
                int paramCount = method.getParameterCount();
                final Map<Integer, String> map = new HashMap<>();
                // get names from @Param annotations
                for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
                    for (Annotation annotation : paramAnnotations[paramIndex]) {
                        Class<?> parameterType = parameterTypes[paramIndex];
                        if (annotation instanceof Param && Wrapper.class.isAssignableFrom(parameterType)) {
                            map.put(paramIndex, ((Param) annotation).value());
                            break;
                        }
                    }
                }
                return map.isEmpty() ? null : Collections.unmodifiableMap(map);
            }
            """).operate(Overwrite.Operate.APPEND_METHOD));
        addStep(i -> i.source("""
            private Object convertArgsToSqlCommandParam(Object[] args) {
                if (args == null) {
                    return null;
                }
                if (null != wrapperParamsAliasNameMap) {
                    for (Map.Entry<Integer, String> entry : wrapperParamsAliasNameMap.entrySet()) {
                        Object arg = args[entry.getKey()];
                        if (arg instanceof AbstractWrapper) {
                            AbstractWrapper<?, ?, ?> wrapper = (AbstractWrapper<?, ?, ?>) arg;
                            String paramAlias = entry.getValue();
                            if (!paramAlias.equals(wrapper.getParamAlias())) {
                                wrapper.setParamAlias(paramAlias);
                            }
                        }
                    }
                }
                return method.convertArgsToSqlCommandParam(args);
            }
            """).operate(Overwrite.Operate.APPEND_METHOD));
    }
}
