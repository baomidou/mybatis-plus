package com.baomidou.mybatisplus.core.toolkit.condition;

/**
 * 谨慎的传递泛型
 * 主要的条件拼接都是这货干活
 * <p>
 * Create by HCL at 2018/05/29
 */
public abstract class AbstractCondition<This, CLASS, COLUMN>
    implements ICondition<This, CLASS, COLUMN> {

    private StringBuilder sb = new StringBuilder();

    @Override
    public This eq(COLUMN column, Object value) {
        sb.append(getColumn(column)).append(" = ").append(value);
        return typedSelf();
    }

    protected abstract String getColumn(COLUMN column);

    @SuppressWarnings("unchecked")
    protected This typedSelf() {
        return (This) this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
