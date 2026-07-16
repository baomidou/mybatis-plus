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
            .addImport("com.baomidou.mybatisplus.core.override.SelectReturnTypeHandler")
            .addImport("com.baomidou.mybatisplus.core.override.SelectReturnTypeHandlerRegistry"));
        // execute: 在 else 分支中使用 SelectReturnTypeHandler 责任链
        addStep(i -> i
            .front("result = executeForCursor(sqlSession, args);")
            .interval(8)
            .behind("case FLUSH:")
            .content(Overwrite.Content.builder()
                .code("""
                    } else {
                        Object param = method.convertArgsToSqlCommandParam(args);
                        SelectReturnTypeHandler handler = SelectReturnTypeHandlerRegistry.findHandler(method.getReturnType());
                        if (handler != null) {
                            Object rawResult;
                            if (handler.selectMethod() == SelectReturnTypeHandler.SelectMethod.SELECT_LIST) {
                                rawResult = sqlSession.selectList(command.getName(), param);
                            } else {
                                rawResult = sqlSession.selectOne(command.getName(), param);
                            }
                            result = handler.transform(rawResult, args, method);
                    """)
                .frontDown(1).build())
            .content(Overwrite.Content.builder()
                .code("""
                        } else {
                    """)
                .behindUp(3)
                .build())
        );
    }
}
