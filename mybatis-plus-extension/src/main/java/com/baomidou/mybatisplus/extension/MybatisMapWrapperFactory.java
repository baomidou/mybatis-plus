/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension;

import com.baomidou.mybatisplus.extension.handlers.MybatisMapWrapper;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.Map;

/**
 * 开启返回map结果集的下划线转驼峰
 *
 * <p> Map 的 key 下划线转驼峰 </p>
 * <p>configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());</p>
 *
 * @author yuxiaobin
 * @since 2017-12-19
 */
public class MybatisMapWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        return object instanceof Map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new MybatisMapWrapper(metaObject, (Map<String, Object>) object);
    }
}
