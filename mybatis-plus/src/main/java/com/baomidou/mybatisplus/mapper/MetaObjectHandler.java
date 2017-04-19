/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.mapper;

import org.apache.ibatis.reflection.MetaObject;

/**
 * <p>
 * 元对象字段填充控制器抽象类，实现公共字段自动写入
 * </p>
 *
 * @author hubin
 * @Date 2016-08-28
 */
public abstract class MetaObjectHandler {

    /**
     * <p>
     * 插入元对象字段填充
     * </p>
     *
     * @param metaObject 元对象
     */
    public abstract void insertFill(MetaObject metaObject);

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     * Created with IntelliJ IDEA.
     * Author:  Wu Yujie
     * Email:  coffee377@dingtalk.com
     * Time:  2017/04/16 15:03
     *
     * @param metaObject 元对象
     */
    public abstract void updateFill(MetaObject metaObject);

    /**
     * 开启插入填充
     */
    public boolean openInsertFill() {
        return true;
    }

    /**
     * 开启更新填充
     */
    public boolean openUpdateFill() {
        return true;
    }

}
