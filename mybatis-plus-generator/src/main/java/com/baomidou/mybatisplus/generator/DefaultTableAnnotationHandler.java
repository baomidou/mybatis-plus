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
package com.baomidou.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 表注解处理器
 *
 * @author nieqiurong
 * @since 3.5.10
 */
public class DefaultTableAnnotationHandler implements ITableAnnotationHandler {

    @Override
    public List<ClassAnnotationAttributes> handle(TableInfo tableInfo, Entity entity) {
        List<ClassAnnotationAttributes> annotationAttributesList = new ArrayList<>();
        GlobalConfig globalConfig = tableInfo.getGlobalConfig();
        String comment = tableInfo.getComment();
        if (StringUtils.isBlank(comment)) {
            comment = StringPool.EMPTY;
        }
        boolean kotlin = globalConfig.isKotlin();
        if (!kotlin) {
            // 原先kt模板没有处理这些,作为兼容项
            if (entity.isChain() && entity.isLombok()) {
                annotationAttributesList.add(new ClassAnnotationAttributes("@Accessors(chain = true)", "lombok.experimental.Accessors"));
            }
            if (entity.isLombok()) {
                if (entity.isDefaultLombok()) {
                    // 原先lombok默认只有这两个
                    annotationAttributesList.add(new ClassAnnotationAttributes("@Getter", "lombok.Getter"));
                    annotationAttributesList.add(new ClassAnnotationAttributes("@Setter", "lombok.Setter"));
                    if (entity.isToString()) {
                        annotationAttributesList.add(new ClassAnnotationAttributes("@ToString", "lombok.ToString"));
                    }
                }
            }
        }
        if (tableInfo.isConvert()) {
            String schemaName = tableInfo.getSchemaName();
            if (StringUtils.isBlank(schemaName)) {
                schemaName = StringPool.EMPTY;
            } else {
                schemaName = schemaName + StringPool.DOT;
            }
            //@TableName("${schemaName}${table.name}")
            String displayName = String.format("@TableName(\"%s%s\")", schemaName, tableInfo.getName());
            annotationAttributesList.add(new ClassAnnotationAttributes(TableName.class, displayName));
        }
        if (globalConfig.isSwagger()) {
            //@ApiModel(value = "${entity}对象", description = "${table.comment!}")
            String displayName = String.format("@ApiModel(value = \"%s对象\", description = \"%s\")", tableInfo.getEntityName(), comment);
            annotationAttributesList.add(new ClassAnnotationAttributes(
                displayName, "io.swagger.annotations.ApiModel", "io.swagger.annotations.ApiModelProperty"));
        }
        if (globalConfig.isSpringdoc()) {
            //@Schema(name = "${entity}", description = "${table.comment!}")
            String displayName = String.format("@Schema(name = \"%s\", description = \"%s\")", tableInfo.getEntityName(), comment);
            annotationAttributesList.add(new ClassAnnotationAttributes(displayName, "io.swagger.v3.oas.annotations.media.Schema"));
        }
        return annotationAttributesList;
    }

}
