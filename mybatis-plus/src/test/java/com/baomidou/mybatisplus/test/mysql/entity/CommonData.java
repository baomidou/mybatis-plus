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
package com.baomidou.mybatisplus.test.mysql.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.test.mysql.enums.TestEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

/**
 * 包含功能:
 * 1.自动填充        验证无误
 * 2.乐观锁          验证无误
 * 3.多租户          验证无误
 * 4.枚举            验证无误
 *
 * @author meimie
 * @since 2018/6/7
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CommonData extends Model<CommonData> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5651603508613034190L;

    private Long id;
    @TableField(jdbcType = JdbcType.INTEGER)
    private Integer testInt;
    @TableField
    private String testStr;
    @TableField(value = "c_time", fill = FieldFill.INSERT)
    private LocalDateTime createDatetime;
    @TableField(value = "u_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateDatetime;
    private TestEnum testEnum;

    @Version
    private Integer version;
//    /**
//     * 多租户
//     * 不用配置实体字段,但是数据库需要该字段
//     */
//    private Long tenantId;

}
