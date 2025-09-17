package com.baomidou.mybatisplus.code.sub;

import com.baomidou.mybatisplus.code.Overwrite;
import com.baomidou.mybatisplus.code.OverwriteFile;

/**
 * {@link org.apache.ibatis.binding.MapperMethod}
 *
 * @author miemie
 * @since 2025/9/1
 */
public class MapperMethod extends OverwriteFile {

    public MapperMethod() {
        // import
        addStep(i -> i
            .source("import org.apache.ibatis.session.SqlSession;")
            .target("""
                import com.baomidou.mybatisplus.core.metadata.IPage;
                import com.baomidou.mybatisplus.core.toolkit.Assert;
                """));
        // execute
        addStep(i -> i
            .source("""
                Object param = method.convertArgsToSqlCommandParam(args);
                result = sqlSession.selectOne(command.getName(), param);
                if (method.returnsOptional() && (result == null || !method.getReturnType().equals(result.getClass()))) {
                    result = Optional.ofNullable(result);
                }
                """)
            .target("""
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
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                 private Object rowCountResult(int rowCount) {
                  final Object result;
                  if (method.returnsVoid()) {
                    result = null;
                  } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
                    result = rowCount;
                  } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
                    result = (long) rowCount;
                  } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
                    result = rowCount > 0;
                  } else {
                    throw new BindingException(
                        "Mapper method '" + command.getName() + "' has an unsupported return type: " + method.getReturnType());
                  }
                  return result;
                }
                """)
            .target("""
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
                    Object param = method.convertArgsToSqlCommandParam(args);
                    List<E> list = sqlSession.selectList(command.getName(), param);
                    result.setRecords(list);
                    return result;
                }
                """)
        );
    }
}
