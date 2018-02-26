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
package com.baomidou.mybatisplus.extension.plugins.tenant;


import com.baomidou.mybatisplus.core.parser.AbstractJsqlParser;

import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;

/**
 * <p>
 * 租户 SQL 解析（ Schema 表级 ）
 * </p>
 *
 * @author hubin
 * @since 2017-09-01
 */
public class TenantSchemaSqlParser extends AbstractJsqlParser {

    private TenantSchemaHandler tenantSchemaHandler;

    @Override
    public void processInsert(Insert insert) {

    }

    @Override
    public void processDelete(Delete delete) {

    }

    @Override
    public void processUpdate(Update update) {

    }

    @Override
    public void processSelectBody(SelectBody selectBody) {

    }

    public TenantSchemaHandler getTenantSchemaHandler() {
        return tenantSchemaHandler;
    }

    public void setTenantSchemaHandler(TenantSchemaHandler tenantSchemaHandler) {
        this.tenantSchemaHandler = tenantSchemaHandler;
    }
}
