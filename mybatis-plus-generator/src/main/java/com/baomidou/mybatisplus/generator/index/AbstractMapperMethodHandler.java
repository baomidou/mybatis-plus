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
package com.baomidou.mybatisplus.generator.index;

import com.baomidou.mybatisplus.generator.IGenerateMapperMethodHandler;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.jdbc.DatabaseMetaDataWrapper.Index;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nieqiurong
 * @since 3.5.10
 */
public abstract class AbstractMapperMethodHandler implements IGenerateMapperMethodHandler {

    protected Map<String, List<Index>> getMappedIndexes(TableInfo tableInfo) {
        Map<String, List<Index>> indexes = tableInfo.getIndexList().stream()
                .collect(Collectors.groupingBy(Index::getName));
        // 复合索引必须保留全部键列，否则按部分条件生成的更新和删除方法会影响其他记录。
        indexes.values().removeIf(
                parts -> parts.stream()
                        .anyMatch(part -> !tableInfo.getTableFieldMap().containsKey(part.getColumnName())));
        return indexes;
    }

    /**
     * 生成Java方法(default) <br>
     * <pre>
     * default ${returnValue} ${methodName}(${args}) {
     *    return ${returnBody};
     * }
     * <pre/>
     * Example:
     * <pre>
     * default UserInfo selectByCardNo(String cardNo) {
     *    return selectOne(Wrappers.<UserInfo>query().eq(TUserInfo.CARD_NO, cardNo));
     * }
     * </pre>
     */
    public String buildMethod(String methodName, String args, String returnValue, String returnBody) {
        return "default" + " " + returnValue + " " + methodName + "(" + args + ")" + " " + "{" + "\n"
                + "        return " + returnBody + ";" + "\n" + "    }\n";
    }

    /**
     * 构建Kotlin方法 <br>
     * <pre>
     * fun ${methodName}(${args}) :${returnValue} {
     *    return ${returnBody};
     * }
     * </pre> <br>
     * Example: <br>
     * <pre>
     * fun selectByCardNo(cardNo: String) :UserInfo? {
     *    return selectOne(Wrappers.query<UserInfo>().eq(UserInfo.CARD_NO, cardNo));
     * }
     * </pre>
     *
     * @param methodName 方法名
     * @param args 参数列表
     * @param returnValue 返回值
     * @param returnBody 返回体
     * @return 方法
     */
    public String buildKotlinMethod(String methodName, String args, String returnValue, String returnBody) {
        return "fun " + methodName + "(" + args + ")" + " :" + returnValue + " {" + "\n" + "        return "
                + returnBody + ";" + "\n" + "    }\n";
    }

    /**
     * 判断当前索引名称是否为主键索引 (索引名为PRIMARY或PRIMARY开头的为主键索引)
     *
     * @param indexName 索引名称
     * @return 是否为主键索引
     * @since 3.5.12
     */
    public boolean isPrimaryKey(String indexName) {
        return indexName.toUpperCase().startsWith(ConstVal.PRIMARY);
    }

}
