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
package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.ddl.DdlHelper;
import com.baomidou.mybatisplus.extension.ddl.DdlScriptErrorHandler;
import com.baomidou.mybatisplus.extension.ddl.IDdl;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;
import java.util.function.Consumer;

/**
 * DDL 启动应用后执行
 *
 * @author hubin
 * @since 2021-06-22
 */
@Slf4j
public class DdlApplicationRunner implements ApplicationRunner {

    /**
     * 处理器列表
     */
    private final List<IDdl> ddlList;

    /**
     * 是否自动提交 (默认自动提交)
     *
     * @since 3.5.11
     */
    @Setter
    private boolean autoCommit = true;

    /**
     * 执行脚本错误处理器 (默认打印错误日志继续执行下一个文件)
     *
     * @since 3.5.11
     */
    @Setter
    private DdlScriptErrorHandler ddlScriptErrorHandler = DdlScriptErrorHandler.PrintlnLogErrorHandler.INSTANCE;

    /**
     * 自定义 ScriptRunner 函数
     *
     * @since 3.5.11
     */
    @Setter
    private Consumer<ScriptRunner> scriptRunnerConsumer;

    /**
     * 是否抛出异常
     * <p>
     * 注意这里是控制{@link #ddlList}循环处理时是否抛出异常
     * </p>
     * <p>
     * 当设置为false时,会遍历处理完所有处理器
     * </p>
     * <p>
     * 当设置为true时,在遍历处理时遇到异常会抛出异常中断下一个处理器处理
     * </p>
     *
     * @since 3.5.11 保持兼容性,默认不抛出
     */
    @Setter
    private boolean throwException = false;

    public DdlApplicationRunner(List<IDdl> ddlList) {
        this.ddlList = ddlList;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (CollectionUtils.isNotEmpty(ddlList)) {
            log.debug("  ...  DDL start create  ...  ");
            ddlList.forEach(ddl -> ddl.runScript(dataSource -> {
                String ddlClassName = AopUtils.getTargetClass(ddl).getName();
                if (CollectionUtils.isEmpty(ddl.getSqlFiles())) {
                    log.warn("{}, sql files is empty", ddlClassName);
                    return;
                }
                log.info("{}, run sql files {}", ddlClassName, ddl.getSqlFiles());
                try {
                    DdlHelper.runScript(
                            ddl.getDdlGenerator(), dataSource, ddl.getSqlFiles(), this.scriptRunnerConsumer,
                            this.autoCommit, this.ddlScriptErrorHandler);
                } catch (Exception e) {
                    log.error("{}, run sql file error: ", ddlClassName, e);
                    if (throwException) {
                        throw new RuntimeException(e);
                    }
                }
            }));
            log.debug("  ...  DDL end create  ...  ");
        }
    }

}
