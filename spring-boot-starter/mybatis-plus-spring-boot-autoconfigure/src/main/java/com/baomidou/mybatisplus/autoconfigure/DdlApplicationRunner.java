/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.ddl.DdlHelper;
import com.baomidou.mybatisplus.extension.ddl.IDdl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

/**
 * DDL 启动应用后执行
 *
 * @author hubin
 * @since 2021-06-22
 */
@Slf4j
public class DdlApplicationRunner implements ApplicationRunner {

    private final List<IDdl> ddlList;

    public DdlApplicationRunner(List<IDdl> ddlList) {
        this.ddlList = ddlList;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (CollectionUtils.isNotEmpty(ddlList)) {
            log.debug("  ...  DDL start create  ...  ");
            ddlList.forEach(ddl -> ddl.runScript(dataSource -> DdlHelper.runScript(ddl.getDdlGenerator(),
                dataSource, ddl.getSqlFiles(), true)));
            log.debug("  ...  DDL end create  ...  ");
        }
    }

}
