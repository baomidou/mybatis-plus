/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.toolkit.StringUtils;

import java.text.MessageFormat;

/**
 * <p>
 * Entity 对象封装操作类
 * </p>
 *
 * @author hubin
 * @Date 2016-03-15
 */
public class EntityWrapper<T> {

    /**
     * 定义T-SQL SELECT中的WHERE关键字
     */
    private final String WHERE_WORD = " WHERE ";
    /**
     * 定义T-SQL SELECT中的AND关键字
     */
    private final String AND_WORD = " AND ";
    /**
     * 定义T-SQL SELECT中的OR关键字
     */
    private final String OR_WORD = " OR ";
    /**
     * 定义T-SQL SELECT中的GROUP BY关键字
     */
    private final String GROUPBY_WORD = " GROUP BY ";
    /**
     * 定义T-SQL SELECT中的HAVING关键字
     */
    private final String HAVING_WORD = " HAVING ";
    /**
     * 定义T-SQL SELECT中的ORDER BY关键字
     */
    private final String ORDERBY_WORD = " ORDER BY ";
    /**
     * 定义T-SQL SELECT中的ORDER BY语句中排序的 DESC关键字
     */
    private final String DESC_WORD = " DESC ";
    /**
     * 定义T-SQL SELECT中的ORDER BY语句中排序的 ASC关键字
     */
    private final String ASC_WORD = " ASC ";

    /**
     * 数据库表映射实体类
     */
    private T entity = null;

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    private String sqlSelect = null;

    /**
     * 查询条件
     */
    protected StringBuffer queryFilter = new StringBuffer();

    public EntityWrapper() {
        // to do nothing
    }

    public EntityWrapper(T entity) {
        this.entity = entity;
    }

    public EntityWrapper(T entity, String sqlSelect) {
        this.entity = entity;
        this.sqlSelect = sqlSelect;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public String getSqlSelect() {
        if (StringUtils.isEmpty(sqlSelect)) {
            return null;
        }
        return stripSqlInjection(sqlSelect);
    }

    public void setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
    }

    /**
     * SQL 片段
     */
    public String getSqlSegment() {
        /*
         * 无条件
		 */
        String tempQuery = queryFilter.toString().trim();
        if (StringUtils.isEmpty(tempQuery)) {
            return null;
        }

        // 使用防SQL注入处理后返回
        return stripSqlInjection(queryFilter.toString());
    }

    /**
     * <p>SQL中WHERE关键字跟的条件语句</p>
     * <p>eg: ew.where("name='zhangsan'").and("id={0}",22).and("password is not null")</p>
     *
     * @param sqlWhere where语句
     * @param params   参数集
     * @return
     */
    public EntityWrapper<T> where(String sqlWhere, Object... params) {
        addFilter(WHERE_WORD, sqlWhere, params);
        return this;
    }

    /**
     * <p>SQL中AND关键字跟的条件语句</p>
     * <p>eg: ew.where("name='zhangsan'").and("id={0}",22).and("password is not null")</p>
     *
     * @param sqlAnd and连接串
     * @param params 参数集
     * @return
     */
    public EntityWrapper<T> and(String sqlAnd, Object... params) {
        addFilter(AND_WORD, sqlAnd, params);
        return this;
    }

    /**
     * <p>与and方法的区别是 可根据需要判断是否添加该条件</p>
     *
     * @param need   是否需要使用该and条件
     * @param sqlAnd and条件语句
     * @param params 参数集
     * @return
     */
    public EntityWrapper<T> andIfNeed(boolean need, String sqlAnd, Object... params) {
        addFilterIfNeed(need, AND_WORD, sqlAnd, params);
        return this;
    }

    /**
     * <p>SQL中AND关键字跟的条件语句</p>
     * <p>eg: ew.where("name='zhangsan'").or("password is not null")</p>
     *
     * @param sqlOr  or条件语句
     * @param params 参数集
     * @return
     */
    public EntityWrapper<T> or(String sqlOr, Object... params) {
        addFilter(OR_WORD, sqlOr, params);
        return this;
    }

    /**
     * <p>与or方法的区别是 可根据需要判断是否添加该条件</p>
     *
     * @param need   是否需要使用OR条件
     * @param sqlOr  OR条件语句
     * @param params 参数集
     * @return
     */
    public EntityWrapper<T> orIfNeed(boolean need, String sqlOr, Object... params) {
        addFilterIfNeed(need, OR_WORD, sqlOr, params);
        return this;
    }

    /**
     * <p>SQL中groupBy关键字跟的条件语句</p>
     * <p>eg: ew.where("name='zhangsan'").and("id={0}",22).and("password is not null")
     * .groupBy("id,name")
     * </p>
     *
     * @param sqlGroupBy sql中的Group by语句，无需输入Group By关键字
     * @return this
     */
    public EntityWrapper<T> groupBy(String sqlGroupBy) {
        addFilter(GROUPBY_WORD, sqlGroupBy);
        return this;
    }

    /**
     * <p>SQL中having关键字跟的条件语句</p>
     * <p>eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null")
     * </p>
     *
     * @param sqlHaving having关键字后面跟随的语句
     * @param params    参数集
     * @return EntityWrapper
     */
    public EntityWrapper<T> having(String sqlHaving, Object... params) {
        addFilter(HAVING_WORD, sqlHaving, params);
        return this;
    }

    /**
     * <p>SQL中orderby关键字跟的条件语句</p>
     * <p>
     * eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null")
     * .orderBy("id,name")
     * </p>
     *
     * @param sqlOrderBy sql中的order by语句，无需输入Order By关键字
     * @return this
     */
    public EntityWrapper<T> orderBy(String sqlOrderBy) {
        addFilter(ORDERBY_WORD, sqlOrderBy);
        return this;
    }

    /**
     * <p>SQL中orderby关键字跟的条件语句，可根据变更动态排序</p>
     *
     * @param sqlOrderBy
     * @param isAsc
     * @return
     */
    public EntityWrapper<T> orderBy(String sqlOrderBy, boolean isAsc) {
        addFilter(ORDERBY_WORD, sqlOrderBy);
        if (isAsc) {
            queryFilter.append(ASC_WORD);
        } else {
            queryFilter.append(DESC_WORD);
        }
        return this;
    }


    /**
     * <p>
     * SQL注入内容剥离
     * </p>
     *
     * @param value 待处理内容
     * @return
     */
    protected String stripSqlInjection(String value) {
        return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }

    /**
     * <p>
     * 添加查询条件
     * </p>
     * <p>
     * 例如： ew.addFilter("name={0}", "'123'") <br>
     * 输出：name='123'
     * </p>
     *
     * @param filter SQL 片段内容
     * @param params 格式参数
     * @return
     */
    private EntityWrapper<T> addFilter(String keyWord, String filter, Object... params) {
        if (StringUtils.isEmpty(filter)) {
            return this;
        }
        queryFilter.append(keyWord);
        if (null != params && params.length >= 1) {
            queryFilter.append(MessageFormat.format(filter, params));
        } else {
            queryFilter.append(filter);
        }
        return this;
    }

    /**
     * <p>
     * 添加查询条件
     * </p>
     * <p>
     * 例如： ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id") <br>
     * 输出：name='123'
     * </p>
     *
     * @param keyWord    SQL关键字
     * @param willAppend 判断条件 true 输出 SQL 片段，false 不输出
     * @param filter     SQL 片段
     * @param params     格式参数
     * @return
     */
    private EntityWrapper<T> addFilterIfNeed(boolean willAppend, String keyWord, String filter, Object... params) {
        if (willAppend) {
            addFilter(keyWord, filter, params);
        }
        return this;
    }

}
