package com.baomidou.mybatisplus.core.test.lambda1;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * @author ming
 * @Date 2018/5/14
 */
public class Condition1 extends AbstractWrapper1<Condition1, String, String> {

    @Override
    public String getEntity() {
        throw new MybatisPlusException("ERROR: you can't do it!");
    }

    @Override
    public Condition1 setEntity(String s) {
        throw new MybatisPlusException("ERROR: you can't do it!");
    }

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
