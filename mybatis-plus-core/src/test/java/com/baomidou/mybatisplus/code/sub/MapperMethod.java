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
            .front(new Overwrite.Point("import org.apache.ibatis.session.SqlSession;"))
            .addImport("com.baomidou.mybatisplus.core.metadata.IPage")
            .addImport("com.baomidou.mybatisplus.core.toolkit.Assert"));
        // execute
        addStep(i -> i
            .front(new Overwrite.Point(84, "result = executeForCursor(sqlSession, args);"))
            .behind(new Overwrite.Point(93, "case FLUSH:"))
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
            .front(new Overwrite.Point(106, "private Object rowCountResult(int rowCount) {"))
            .behind(new Overwrite.Point(123, "private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {"))
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
                .behindUp(0).build())
        );
    }
}
