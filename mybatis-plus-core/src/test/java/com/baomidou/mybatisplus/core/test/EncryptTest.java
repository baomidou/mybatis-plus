/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 加密测试
 */
class EncryptTest {

    @Test
    void md5Base64() {
        Assertions.assertEquals("Jgmg8jeuq9EyB1ybYtj1fg==",
            EncryptUtils.md5Base64("犯我中华者虽远必诛"));
    }

    @Test
    void other() {
        System.out.println(TableInfoHelper.checkRelated(true, "order", "'order'"));
        System.out.println(TableInfoHelper.checkRelated(true, "order", "order"));
        System.out.println(TableInfoHelper.checkRelated(true, "orderFile", "'ORDER_FILE'"));
    }

    @Test
    void testTableInfoHelper() {
        TableInfo info = TableInfoHelper.initTableInfo(null, Xx.class);
        System.out.println("----------- AllInsertSqlColumn -----------");
        System.out.println(info.getAllInsertSqlColumnMaybeIf());
        System.out.println("----------- AllInsertSqlProperty -----------");
        System.out.println(info.getAllInsertSqlPropertyMaybeIf(null));
        System.out.println("----------- AllSqlSet -----------");
        System.out.println(info.getAllSqlSet(true, "ew.entity."));
        System.out.println("----------- AllSqlWhere -----------");
        System.out.println(info.getAllSqlWhere(true, true, "ew.entity."));
    }

    @Data
    private static class Xx {
        private Long id;
        @TableField(fill = FieldFill.INSERT)
        private String x1;
        @TableField(fill = FieldFill.INSERT_UPDATE, whereStrategy = FieldStrategy.NOT_EMPTY)
        private String x2;
        @TableField(fill = FieldFill.UPDATE)
        private String x3;
        @TableField(whereStrategy = FieldStrategy.NOT_EMPTY)
        private String x4;
        @TableField(value = "xx5", updateStrategy = FieldStrategy.IGNORED, update = "%s+1")
        private String x5;
        @TableLogic
        private Integer deleted;
    }
}
