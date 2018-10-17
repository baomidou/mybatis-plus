package com.baomidou.mybatisplus.core.toolkit.support.lambda;

import java.io.Serializable;

/**
 * 一个序列化标志接口，起着接口占位符的作用，主要用于传递可序列化的 lambda 表达式
 * 实现该接口可用于快速的实现 lambda 序列化
 *
 * @author HCL
 * Create at 2018/10/17
 */
public interface SerializableLambda extends Serializable {
}
