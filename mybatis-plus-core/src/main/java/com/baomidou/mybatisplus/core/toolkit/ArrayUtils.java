/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * ArrayUtils工具类
 * </p>
 *
 * @author Caratacus t1zg
 * @since 2017-03-09
 */
public final class ArrayUtils {

    private ArrayUtils() {
    }

    /**
     * 判断数据是否为空
     *
     * @param array 长度
     * @return 数组对象为null或者长度为 0 时，返回 false
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否不为空
     *
     * @param array 数组
     * @return 数组对象内含有任意对象时返回 true
     * @see ArrayUtils#isEmpty(Object[])
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组分组
     * 大数据量插入时不建议使用 foreach
     * https://stackoverflow.com/questions/32649759/using-foreach-to-do-batch-insert-with-mybatis/40608353
     *
     * 如果真要用 可以考虑对于数组进行分批次插入
     * params: List[101] 50 return: 3[34][34][33]
     * params: List[101] 150 return: 3[50][50][50]
     *
     * @param array 数组
     * @param branchSize 分组大小
     * @return 按照参数平均分组
     */
    public static <T> List<T>[] groupBySize(List<T> array, int branchSize) {
        if(branchSize <= 0) {
            branchSize = 1;
        }

        int size = array.size();
        int len = size % branchSize > 0 ? size / branchSize + 1 : size / branchSize;
        List<T>[] groups = new ArrayList[len];

        for(int i = 0 ; i < array.size(); i++){
            int index = i % len;
            if(groups[index] == null){
                groups[index] = new ArrayList<>();
            }
            groups[index].add(array.get(i));
        }
        return groups;
    }

}
