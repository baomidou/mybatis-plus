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
package com.baomidou.mybatisplus.extension.plugins;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 由于开发人员水平参差不齐，即使订了开发规范很多人也不遵守
 * <p>SQL是影响系统性能最重要的因素，所以拦截掉垃圾SQL语句</p>
 * <br>
 * <p>拦截SQL类型的场景</p>
 * <p>1.必须使用到索引，包含left jion连接字段，符合索引最左原则</p>
 * <p>必须使用索引好处，</p>
 * <p>1.1 如果因为动态SQL，bug导致update的where条件没有带上，全表更新上万条数据</p>
 * <p>1.2 如果检查到使用了索引，SQL性能基本不会太差</p>
 * <br>
 * <p>2.SQL尽量单表执行，有查询left jion的语句，必须在注释里面允许该SQL运行，否则会被拦截，有left jion的语句，如果不能拆成单表执行的SQL，请leader商量在做</p>
 * <p>https://gaoxianglong.github.io/shark</p>
 * <p>SQL尽量单表执行的好处</p>
 * <p>2.1 查询条件简单、易于开理解和维护；</p>
 * <p>2.2 扩展性极强；（可为分库分表做准备）</p>
 * <p>2.3 缓存利用率高；</p>
 * <p>2.在字段上使用函数</p>
 * <br>
 * <p>3.where条件为空</p>
 * <p>4.where条件使用了 !=</p>
 * <p>5.where条件使用了 not 关键字</p>
 * <p>6.where条件使用了 or 关键字</p>
 * <p>7.where条件使用了 使用子查询</p>
 * @author willenfoo
 * @date 2018-03-22
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class IllegalSQLInterceptor implements Interceptor {

    /**
     * 缓存验证结果，提高性能
     */
    private static final Set<String> cacheValidResult = new HashSet<>();

    private static final Log logger = LogFactory.getLog(IllegalSQLInterceptor.class);

    /**
     * 缓存表的索引信息
     */
    private static final Map<String, List<IndexInfo>> indexInfoMap = new ConcurrentHashMap<>();

    /**
     * 验证expression对象是不是 or、not等等
     *
     * @param expression ignore
     */
    private static void validExpression(Expression expression) {
        //where条件使用了 or 关键字
        if (expression instanceof OrExpression) {
            OrExpression orExpression = (OrExpression) expression;
            throw new MybatisPlusException("非法SQL，where条件中不能使用【or】关键字，错误or信息：" + orExpression.toString());
        } else if (expression instanceof NotEqualsTo) {
            NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
            throw new MybatisPlusException("非法SQL，where条件中不能使用【!=】关键字，错误!=信息：" + notEqualsTo.toString());
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            // TODO 升级 jsqlparser 后待实现
//            if (binaryExpression.isNot()) {
//                throw new MybatisPlusException("非法SQL，where条件中不能使用【not】关键字，错误not信息：" + binaryExpression.toString());
//            }
            if (binaryExpression.getLeftExpression() instanceof Function) {
                Function function = (Function) binaryExpression.getLeftExpression();
                throw new MybatisPlusException("非法SQL，where条件中不能使用数据库函数，错误函数信息：" + function.toString());
            }
            if (binaryExpression.getRightExpression() instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) binaryExpression.getRightExpression();
                throw new MybatisPlusException("非法SQL，where条件中不能使用子查询，错误子查询SQL信息：" + subSelect.toString());
            }
        } else if (expression instanceof InExpression) {
            InExpression inExpression = (InExpression) expression;
            if (inExpression.getRightItemsList() instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) inExpression.getRightItemsList();
                throw new MybatisPlusException("非法SQL，where条件中不能使用子查询，错误子查询SQL信息：" + subSelect.toString());
            }
        }

    }

    /**
     * 如果SQL用了 left Join，验证是否有or、not等等，并且验证是否使用了索引
     *
     * @param joins ignore
     * @param table ignore
     * @param connection ignore
     */
    private static void validJoins(List<Join> joins, Table table, Connection connection) {
        //允许执行join，验证jion是否使用索引等等
        if (joins != null) {
            for (Join join : joins) {
                Table rightTable = (Table) join.getRightItem();
                Expression expression = join.getOnExpression();
                validWhere(expression, table, rightTable, connection);
            }
        }
    }

    /**
     * 检查是否使用索引
     *
     * @param table ignore
     * @param columnName ignore
     * @param connection ignore
     */
    private static void validUseIndex(Table table, String columnName, Connection connection) {
        //是否使用索引
        boolean useIndexFlag = false;

        String tableInfo = table.getName();
        //表存在的索引
        String dbName = null;
        String tableName;
        String[] tableArray = tableInfo.split("\\.");
        if (tableArray.length == 1) {
            tableName = tableArray[0];
        } else {
            dbName = tableArray[0];
            tableName = tableArray[1];
        }
        List<IndexInfo> indexInfos = getIndexInfos(dbName, tableName, connection);
        for (IndexInfo indexInfo : indexInfos) {
            if (Objects.equals(columnName, indexInfo.getColumnName())) {
                useIndexFlag = true;
                break;
            }
        }
        if (!useIndexFlag) {
            throw new MybatisPlusException("非法SQL，SQL未使用到索引, table:" + table + ", columnName:" + columnName);
        }
    }

    /**
     * 验证where条件的字段，是否有not、or等等，并且where的第一个字段，必须使用索引
     *
     * @param expression ignore
     * @param table ignore
     * @param connection ignore
     */
    private static void validWhere(Expression expression, Table table, Connection connection) {
        validWhere(expression, table, null, connection);
    }

    /**
     * 验证where条件的字段，是否有not、or等等，并且where的第一个字段，必须使用索引
     *
     * @param expression ignore
     * @param table ignore
     * @param joinTable ignore
     * @param connection ignore
     */
    private static void validWhere(Expression expression, Table table, Table joinTable, Connection connection) {
        validExpression(expression);
        if (expression instanceof BinaryExpression) {
            //获得左边表达式
            Expression leftExpression = ((BinaryExpression) expression).getLeftExpression();
            validExpression(leftExpression);

            //如果左边表达式为Column对象，则直接获得列名
            if (leftExpression instanceof Column) {
                Expression rightExpression = ((BinaryExpression) expression).getRightExpression();
                if (joinTable != null && rightExpression instanceof Column) {
                    if (Objects.equals(((Column) rightExpression).getTable().getName(), table.getAlias().getName())) {
                        validUseIndex(table, ((Column) rightExpression).getColumnName(), connection);
                        validUseIndex(joinTable, ((Column) leftExpression).getColumnName(), connection);
                    } else {
                        validUseIndex(joinTable, ((Column) rightExpression).getColumnName(), connection);
                        validUseIndex(table, ((Column) leftExpression).getColumnName(), connection);
                    }
                } else {
                    //获得列名
                    validUseIndex(table, ((Column) leftExpression).getColumnName(), connection);
                }
            }
            //如果BinaryExpression，进行迭代
            else if (leftExpression instanceof BinaryExpression) {
                validWhere(leftExpression, table, joinTable, connection);
            }

            //获得右边表达式，并分解
            Expression rightExpression = ((BinaryExpression) expression).getRightExpression();
            validExpression(rightExpression);
        }
    }

    /**
     * 得到表的索引信息
     *
     * @param dbName ignore
     * @param tableName ignore
     * @param conn ignore
     * @return ignore
     */
    public static List<IndexInfo> getIndexInfos(String dbName, String tableName, Connection conn) {
        return getIndexInfos(null, dbName, tableName, conn);
    }

    /**
     * 得到表的索引信息
     *
     * @param key ignore
     * @param dbName ignore
     * @param tableName ignore
     * @param conn ignore
     * @return ignore
     */
    public static List<IndexInfo> getIndexInfos(String key, String dbName, String tableName, Connection conn) {
        List<IndexInfo> indexInfos = null;
        if (StringUtils.isNotEmpty(key)) {
            indexInfos = indexInfoMap.get(key);
        }
        if (indexInfos == null || indexInfos.isEmpty()) {
            ResultSet rs;
            try {
                DatabaseMetaData metadata = conn.getMetaData();
                rs = metadata.getIndexInfo(dbName, dbName, tableName, false, true);
                indexInfos = new ArrayList<>();
                while (rs.next()) {
                    //索引中的列序列号等于1，才有效
                    if (Objects.equals(rs.getString(8), "1")) {
                        IllegalSQLInterceptor.IndexInfo indexInfo = new IllegalSQLInterceptor.IndexInfo();
                        indexInfo.setDbName(rs.getString(1));
                        indexInfo.setTableName(rs.getString(3));
                        indexInfo.setColumnName(rs.getString(9));
                        indexInfos.add(indexInfo);
                    }
                }
                if (StringUtils.isNotEmpty(key)) {
                    indexInfoMap.put(key, indexInfos);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return indexInfos;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // 如果是insert操作， 或者 @SqlParser(filter = true) 跳过该方法解析 ， 不进行验证
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType()) || SqlParserHelper.getSqlParserInfo(metaObject)) {
            return invocation.proceed();
        }
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String originalSql = boundSql.getSql();
        logger.debug("检查SQL是否合规，SQL:" + originalSql);
        String md5Base64 = EncryptUtils.md5Base64(originalSql);
        if (cacheValidResult.contains(md5Base64)) {
            logger.debug("该SQL已验证，无需再次验证，，SQL:" + originalSql);
            return invocation.proceed();
        }
        Connection connection = (Connection) invocation.getArgs()[0];
        Statement statement = CCJSqlParserUtil.parse(originalSql);
        Expression where = null;
        Table table = null;
        List<Join> joins = null;
        if (statement instanceof Select) {
            PlainSelect plainSelect = (PlainSelect) ((Select) statement).getSelectBody();
            where = plainSelect.getWhere();
            table = (Table) plainSelect.getFromItem();
            joins = plainSelect.getJoins();
        } else if (statement instanceof Update) {
            Update update = (Update) statement;
            where = update.getWhere();
            table = update.getTables().get(0);
            joins = update.getJoins();
        } else if (statement instanceof Delete) {
            Delete delete = (Delete) statement;
            where = delete.getWhere();
            table = delete.getTable();
            joins = delete.getJoins();
        }
        //where条件不能为空
        if (where == null) {
            throw new MybatisPlusException("非法SQL，必须要有where条件");
        }
        validWhere(where, table, connection);
        validJoins(joins, table, connection);
        //缓存验证结果
        cacheValidResult.add(md5Base64);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {

    }

    /**
     * 索引对象
     */
    @Data
    private static class IndexInfo {

        private String dbName;

        private String tableName;

        private String columnName;
    }
}
