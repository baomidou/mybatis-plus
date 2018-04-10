/*
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
package com.baomidou.mybatisplus.core.injector;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.core.injector.methods.Delete;
import com.baomidou.mybatisplus.core.injector.methods.DeleteBatchByIds;
import com.baomidou.mybatisplus.core.injector.methods.DeleteById;
import com.baomidou.mybatisplus.core.injector.methods.DeleteByMap;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.injector.methods.InsertAllColumn;
import com.baomidou.mybatisplus.core.injector.methods.SelectBatchByIds;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.core.injector.methods.SelectByMap;
import com.baomidou.mybatisplus.core.injector.methods.SelectCount;
import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.baomidou.mybatisplus.core.injector.methods.SelectMaps;
import com.baomidou.mybatisplus.core.injector.methods.SelectMapsPage;
import com.baomidou.mybatisplus.core.injector.methods.SelectObjs;
import com.baomidou.mybatisplus.core.injector.methods.SelectOne;
import com.baomidou.mybatisplus.core.injector.methods.SelectPage;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.baomidou.mybatisplus.core.injector.methods.UpdateAllColumnById;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;


/**
 * <p>
 * SQL 默认注入器
 * </p>
 *
 * @author hubin
 * @since 2018-04-10
 */
public class DefaultSqlInjector extends AbstractSqlInjector {


    @Override
    public List<AbstractMethod> getMethodList() {
        List<AbstractMethod> methodList = new ArrayList<>();
        methodList.add(new Insert());
        methodList.add(new InsertAllColumn());
        methodList.add(new Delete());
        methodList.add(new DeleteByMap());
        methodList.add(new DeleteById());
        methodList.add(new DeleteBatchByIds());
        methodList.add(new Update());
        methodList.add(new UpdateById());
        methodList.add(new UpdateAllColumnById());
        methodList.add(new SelectById());
        methodList.add(new SelectBatchByIds());
        methodList.add(new SelectByMap());
        methodList.add(new SelectOne());
        methodList.add(new SelectCount());
        methodList.add(new SelectMaps());
        methodList.add(new SelectMapsPage());
        methodList.add(new SelectObjs());
        methodList.add(new SelectList());
        methodList.add(new SelectPage());
        return methodList;
    }


    @Override
    public void injectSqlRunner(Configuration configuration) {
        new SqlRunnerInjector().inject(configuration);
    }
}
