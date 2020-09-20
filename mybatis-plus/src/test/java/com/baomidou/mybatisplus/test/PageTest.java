package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.sf.cglib.beans.BeanCopier;
import org.apache.ibatis.reflection.property.PropertyCopier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.util.Collections;

/**
 * @author nieqiurong 2020/3/20.
 */
class PageTest {
  
    @Test
    void testCopy(){
        Page page1 = new Page(2, 10, 100, false);
        page1.setOptimizeCountSql(false);
        page1.hitCount(true);
        page1.setOrders(Collections.singletonList(OrderItem.asc("test")));
        
        Page page2 = new Page();
        PropertyCopier.copyBeanProperties(Page.class, page1, page2);
        Assertions.assertEquals(page1.getCurrent(), page2.getCurrent());
        Assertions.assertEquals(page1.getTotal(), page2.getTotal());
        Assertions.assertEquals(page1.getSize(), page2.getSize());
        Assertions.assertEquals(page1.optimizeCountSql(), page2.optimizeCountSql());
        Assertions.assertEquals(page1.isSearchCount(), page2.isSearchCount());
        Assertions.assertEquals(page1.isHitCount(), page2.isHitCount());
        Assertions.assertEquals(page1.getOrders().size(), page2.getOrders().size());

        Page page3 = new Page();
        BeanUtils.copyProperties(page1,page3);
        Assertions.assertEquals(page1.getCurrent(), page3.getCurrent());
        Assertions.assertEquals(page1.getTotal(), page3.getTotal());
        Assertions.assertEquals(page1.getSize(), page3.getSize());
        Assertions.assertEquals(page1.optimizeCountSql(), page3.optimizeCountSql());
        Assertions.assertEquals(page1.isSearchCount(), page3.isSearchCount());
        Assertions.assertEquals(page1.isHitCount(), page3.isHitCount());
        Assertions.assertEquals(page1.getOrders().size(), page3.getOrders().size());
    
        Page page4 = new Page();
        BeanCopier.create(page1.getClass(), page4.getClass(), false).copy(page1, page4, null);
        //链式的set方法会导致属性BeanCopier的拷贝方法失败  https://github.com/cglib/cglib/issues/108.
        Assertions.assertTrue(page4.optimizeCountSql());
        Assertions.assertTrue(page4.isSearchCount());
        Assertions.assertEquals(1, page4.getCurrent());
        Assertions.assertEquals(1, page4.orders().size());
        Assertions.assertEquals(10, page4.getSize());
    }
    
}
