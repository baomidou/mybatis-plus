/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.override.SelectReturnTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.apache.ibatis.binding.MapperMethod;

import java.util.List;
import java.util.Map;

/**
 * IPage 返回类型的默认处理器。
 * <p>与 {@link PaginationInnerInterceptor} 配套使用——只有配置了分页插件、执行了 count 查询，
 * {@link IPage#getTotal()} 才有意义。</p>
 *
 * @author shanhy
 * @since 3.5.18
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class IPageReturnTypeHandler implements SelectReturnTypeHandler {

    @Override
    public Class<?> getType() {
        return IPage.class;
    }

    @Override
    public SelectMethod selectMethod() {
        return SelectMethod.SELECT_LIST;
    }

    @Override
    public Object transform(Object queryResult, Object[] args,
                            MapperMethod.MethodSignature method,
                            Map<String, Object> executeSharedData) {
        IPage result = null;
        for (Object arg : args) {
            if (arg instanceof IPage) {
                result = (IPage) arg;
                break;
            }
        }
        Assert.notNull(result, "can't found IPage for args!");
        result.setRecords((List) queryResult);
        Long total = (Long) executeSharedData.get(PaginationInnerInterceptor.SHARED_KEY_PAGE_TOTAL);
        if (total != null) {
            result.setTotal(total);
        }
        return result;
    }
}
