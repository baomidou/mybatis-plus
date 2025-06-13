package com.baomidou.mybatisplus.core.handlers;


import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 複數個 {@link MetaObjectHandler}
 *
 * @author Matt Ho
 * @since 2025-06-12
 */
public class CompositeMetaObjectHandler implements MetaObjectHandler {

    private final Collection<MetaObjectHandler> handlers;

    public CompositeMetaObjectHandler(Collection<MetaObjectHandler> handlers) {
        Assert.notNull(handlers, "handlers cannot be null");
        this.handlers = new ArrayList<>(handlers);
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        handlers.forEach(handler -> handler.insertFill(metaObject));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        handlers.forEach(handler -> handler.updateFill(metaObject));
    }
}
