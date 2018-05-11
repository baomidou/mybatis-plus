package com.baomidou.mybatisplus.core.test.lambda1;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

/**
 * @author ming
 * @Date 2018/5/11
 */
public class LambdaWrapper1<T> extends AbstractWrapper1<LambdaWrapper1<T>, T,SerializedFunction<T, ?>> {

    @Override
    String getColumn(SerializedFunction<T, ?> tProperty) {
        //todo 能执行?
        SerializedLambda resolve = LambdaUtils.resolve(tProperty);
        return resolve.getImplMethodName();
    }

    @Override
    public String getSqlSegment() {
        /*
         * 无条件
         */
        String sqlWhere = sql.toString();
        if (StringUtils.isEmpty(sqlWhere)) {
            return null;
        }

        /*
         * 根据当前实体判断是否需要将WHERE替换成 AND 增加实体不为空但所有属性为空的情况
         */
        return isWhere != null ? (isWhere ? sqlWhere : sqlWhere.replaceFirst("WHERE", AND_OR)) : sqlWhere.replaceFirst("WHERE", AND_OR);
    }
}
