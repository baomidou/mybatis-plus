package com.baomidou.mybatisplus.extension.plugins.handler.sharding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zengzhihong
 * @since 2021-01-14
 */
@AllArgsConstructor
@Getter
@Setter
public class ShardingNode<T, C> {

    private T node;

    private List<C> list;
}
