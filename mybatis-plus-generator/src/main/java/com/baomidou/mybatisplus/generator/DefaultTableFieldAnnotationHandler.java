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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.model.AnnotationAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认字段注解处理器
 *
 * @author nieqiurong
 * @since 3.5.10
 */
public class DefaultTableFieldAnnotationHandler implements ITableFieldAnnotationHandler {

    @Override
    public List<AnnotationAttributes> handle(TableInfo tableInfo, TableField tableField) {
        List<AnnotationAttributes> annotationAttributesList = new ArrayList<>();
        GlobalConfig globalConfig = tableInfo.getGlobalConfig();
        Entity entity = tableField.getEntity();
        String comment = tableField.getComment();
        if (StringUtils.isNotBlank(comment)) {
            if (globalConfig.isSpringdoc()) {
                //@Schema(description = "${field.comment}")
                String displayName = String.format("@Schema(description = \"%s\")", comment);
                annotationAttributesList.add(new AnnotationAttributes(displayName, "io.swagger.v3.oas.annotations.media.Schema"));
            } else if (globalConfig.isSwagger()) {
                String displayName = String.format("@ApiModelProperty(\"%s\")", comment);
                annotationAttributesList.add(new AnnotationAttributes(displayName, "io.swagger.annotations.ApiModelProperty"));
            }
        }
        if (tableField.isKeyFlag()) {
            IdType idType = entity.getIdType();
            if (tableField.isKeyIdentityFlag()) {
                //@TableId(value = "${field.annotationColumnName}", type = IdType.AUTO)
                String displayName = String.format("@TableId(value = \"%s\", type = IdType.AUTO)", tableField.getAnnotationColumnName());
                annotationAttributesList.add(new AnnotationAttributes(displayName, TableId.class.getName()));
            } else if (idType != null) {
                //@TableId(value = "${field.annotationColumnName}", type = IdType.${idType})
                String displayName = String.format("@TableId(value = \"%s\", type = IdType.%s)", tableField.getAnnotationColumnName(), idType);
                annotationAttributesList.add(new AnnotationAttributes(displayName, TableId.class.getName()));
            } else if (tableField.isConvert()) {
                //@TableId("${field.annotationColumnName}")
                String displayName = String.format("@TableId(\"%s\")", tableField.getAnnotationColumnName());
                annotationAttributesList.add(new AnnotationAttributes(displayName, TableId.class.getName()));
            }
        } else {
            String fill = tableField.getFill();
            if (StringUtils.isNotBlank(fill)) {
                if (tableField.isConvert()) {
                    //@TableField(value = "${field.annotationColumnName}", fill = FieldFill.${field.fill})
                    String displayName = String.format("@TableField(value = \"%s\", fill = FieldFill.%s)", tableField.getAnnotationColumnName(), fill);
                    annotationAttributesList.add(new AnnotationAttributes(displayName, com.baomidou.mybatisplus.annotation.TableField.class.getName()));
                } else {
                    //@TableField(fill = FieldFill.${field.fill})
                    String displayName = String.format("@TableField(fill = FieldFill.%s)", fill);
                    annotationAttributesList.add(new AnnotationAttributes(displayName, com.baomidou.mybatisplus.annotation.TableField.class.getName()));
                }
            } else {
                if (tableField.isConvert()) {
                    //@TableField("${field.annotationColumnName}")
                    String displayName = String.format("@TableField(\"%s\")", tableField.getAnnotationColumnName());
                    annotationAttributesList.add(new AnnotationAttributes(displayName, com.baomidou.mybatisplus.annotation.TableField.class.getName()));
                }
            }
            if (tableField.isVersionField()) {
                // @Version
                annotationAttributesList.add(new AnnotationAttributes(Version.class));
            }
            if (tableField.isLogicDeleteField()) {
                //@TableLogic
                annotationAttributesList.add(new AnnotationAttributes(TableLogic.class));
            }
        }
        return annotationAttributesList;
    }

}
