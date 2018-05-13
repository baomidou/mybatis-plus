package com.baomidou.mybatisplus.core.test.lambda1;

/**
 * @author ming
 * @Date 2018/5/11
 * 获取entity
 */
public interface Wrapper1<T> extends SqlSegment1 {

    T getEntity();
}
