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
            .addImport("com.baomidou.mybatisplus.core.metadata.IPage")
            .addImport("com.baomidou.mybatisplus.core.toolkit.Assert"));
        // execute
        addStep(i -> i
            .front("result = executeForCursor(sqlSession, args);")
            .interval(8)
            .behind("case FLUSH:")
            .content(Overwrite.Content.builder()
                .code("""
                    if (IPage.class.isAssignableFrom(method.getReturnType())) {
                        result = executeForIPage(sqlSession, args);
                    } else {
                    """)
                .frontDown(1).build())
            .content(Overwrite.Content.builder()
                .code("}")
                .behindUp(3)
                .build())
        );
        addStep(i -> i
            .behind("private Object rowCountResult(int rowCount) {")
            .content(Overwrite.Content.builder()
                .code("""
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
                .behindUp(2).build())
        );
    }
}
