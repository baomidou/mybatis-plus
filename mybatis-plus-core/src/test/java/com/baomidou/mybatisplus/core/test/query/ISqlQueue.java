package com.baomidou.mybatisplus.core.test.query;


public interface ISqlQueue<T> {

   void enqueue(T element);

}
