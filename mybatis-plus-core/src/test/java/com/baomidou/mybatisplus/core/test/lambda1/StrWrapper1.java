package com.baomidou.mybatisplus.core.test.lambda1;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * @author ming
 * @Date 2018/5/11
 */
public class StrWrapper1<T> extends AbstractWrapper1<StrWrapper1<T>, T, String> {

    @Override
    String getColumn(String s) {
        return s;
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
