/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.plugins;

import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * EntityWrapper ew.and("test_date>{0} and test_date<{1}", Date date1, Date
 * date2):<BR>
 * sql segment will be replaced like: test_date>#{MPGENVAL1} and
 * test_date<#{MPGENVAL2},<BR>
 * the parameter MPGENVAL1,MPGENVAL2 should be included in parameter map.<BR>
 * </p>
 *
 * @author yuxiaobin
 * @Date 2017-3-9
 */
@Intercepts({
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,
				ResultHandler.class }),
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }), })
public class WrapperInterceptor implements Interceptor {

	// #200
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		if (args[1] instanceof Map) {
			Map<String, Object> mp = (Map<String, Object>) args[1];
            Collection<Object> values = mp.values();
            Iterator<Object> iterator = values.iterator();
            while (iterator.hasNext()){
                Object obj = iterator.next();
                if (obj instanceof Wrapper) {
                    Wrapper ew = (Wrapper) obj;
                    mp.putAll(ew.getParamNameValuePairs());
                }
            }
		}
		return invocation.proceed();

	}

	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	public void setProperties(Properties prop) {
	}

}
