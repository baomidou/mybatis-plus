package com.baomidou.mybatisplus.extension.plugins.inner;

import static com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInterceptor.convertDoubleQuotes;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;

import lombok.Data;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;

/**
 * <p>
 * 数据变动记录插件
 * 默认会生成一条log，格式：
 * ----------------------INSERT LOG------------------------------
 * {
 *   "tableName": "h2user",
 *   "operation": "insert",
 *   "recordStatus": "true",
 *   "changedData": [
 *     {
 *       "LAST_UPDATED_DT": "null->2022-08-22 18:49:16.512",
 *       "TEST_ID": "null->1561666810058739714",
 *       "AGE": "null->THREE"
 *     }
 *   ],
 *   "cost(ms)": 0
 * }
 * </p>
 * <p>
 *  * ----------------------UPDATE LOG------------------------------
 *
 * {
 *   "tableName": "h2user",
 *   "operation": "update",
 *   "recordStatus": "true",
 *   "changedData": [
 *     {
 *       "TEST_ID": "102",
 *       "AGE": "2->THREE",
 *       "FIRSTNAME": "DOU.HAO->{\"json\":\"abc\"}",
 *       "LAST_UPDATED_DT": "null->2022-08-22 18:49:16.512"
 *     }
 *   ],
 *   "cost(ms)": 0
 * }
 * </p>
 *
 * @author yuxiaobin
 * @date 2022-8-21
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DataChangeRecorderInterceptor implements InnerInterceptor {

    @SuppressWarnings("unused")
    public static final String IGNORED_TABLE_COLUMN_PROPERTIES = "ignoredTableColumns";

    private final Logger logger = LoggerFactory.getLogger("DataChangeRecorder");

    private final Map<String, Set<String>> ignoredTableColumns = new ConcurrentHashMap<>();
    private final Set<String> ignoreAllColumns = new HashSet<>();//全部表的这些字段名，INSERT/UPDATE都忽略，delete暂时保留

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        final BoundSql boundSql = mpSh.boundSql();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            OperationResult operationResult;
            long startTs = System.currentTimeMillis();
            try {
                Statement statement = CCJSqlParserUtil.parse(mpBs.sql());
                if (statement instanceof Insert) {
                    operationResult = processInsert((Insert) statement, mpSh.boundSql());
                } else if (statement instanceof Update) {
                    operationResult = processUpdate((Update) statement, ms, boundSql, connection);
                } else if (statement instanceof Delete) {
                    operationResult = processDelete((Delete) statement, ms, boundSql, connection);
                } else {
                    logger.info("other operation sql={}", mpBs.sql());
                    return;
                }
            } catch (Exception e) {
                logger.error("Unexpected error for mappedStatement={}, sql={}", ms.getId(), mpBs.sql(), e);
                return;
            }
            long costThis = System.currentTimeMillis() - startTs;
            if (operationResult != null) {
                operationResult.setCost(costThis);
                dealOperationResult(operationResult);
            }
        }
    }

    /**
     * 判断哪些SQL需要处理
     * 默认INSERT/UPDATE/DELETE语句
     *
     * @param sql
     * @return
     */
    protected boolean allowProcess(String sql) {
        String sqlTrim = sql.trim().toUpperCase();
        return sqlTrim.startsWith("INSERT") || sqlTrim.startsWith("UPDATE") || sqlTrim.startsWith("DELETE");
    }

    /**
     * 处理数据更新结果，默认打印
     *
     * @param operationResult
     */
    protected void dealOperationResult(OperationResult operationResult) {
        logger.info("{}", operationResult);
    }

    public OperationResult processInsert(Insert insertStmt, BoundSql boundSql) {
        OperationResult result = new OperationResult();
        result.setOperation("insert");
        result.setTableName(insertStmt.getTable().getName());
        result.setRecordStatus(true);
        result.buildDataStr(compareAndGetUpdatedColumnDatas(result.getTableName(), boundSql, insertStmt, null));
        return result;
    }

    public OperationResult processUpdate(Update updateStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        Expression where = updateStmt.getWhere();
        Select selectStmt = new Select();
        PlainSelect selectBody = new PlainSelect();
        Table table = updateStmt.getTable();
        final Set<String> ignoredColumns = ignoredTableColumns.get(table.getName().toUpperCase());
        if (ignoredColumns != null) {
            if (ignoredColumns.stream().anyMatch("*"::equals)) {
                OperationResult result = new OperationResult();
                result.setOperation("update");
                result.setTableName(table.getName() + ":*");
                result.setRecordStatus(false);
                return result;
            }
        }
        selectBody.setFromItem(table);
        List<Column> updateColumns = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateColumns.addAll(updateSet.getColumns());
        }
        Columns2SelectItemsResult buildColumns2SelectItems = buildColumns2SelectItems(table.getName(), updateColumns);
        selectBody.setSelectItems(buildColumns2SelectItems.getSelectItems());
        selectBody.setWhere(where);
        selectStmt.setSelectBody(selectBody);

        BoundSql boundSql4Select = new BoundSql(mappedStatement.getConfiguration(), selectStmt.toString(),
            prepareParameterMapping4Select(boundSql.getParameterMappings(), updateStmt),
            boundSql.getParameterObject());
        MetaObject metaObject = SystemMetaObject.forObject(boundSql);
        Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("additionalParameters");
        if (additionalParameters != null && !additionalParameters.isEmpty()) {
            for (Map.Entry<String, Object> ety : additionalParameters.entrySet()) {
                boundSql4Select.setAdditionalParameter(ety.getKey(), ety.getValue());
            }
        }
        OriginalDataObj originalData = buildOriginalObjectData(selectStmt, buildColumns2SelectItems.getPk(), mappedStatement, boundSql4Select, connection);
        OperationResult result = new OperationResult();
        result.setOperation("update");
        result.setTableName(table.getName());
        result.setRecordStatus(true);
        result.buildDataStr(compareAndGetUpdatedColumnDatas(result.getTableName(), boundSql, updateStmt, originalData));
        return result;
    }

    /**
     * 将update SET部分的jdbc参数去除
     *
     * @param originalMappingList 这里只会包含JdbcParameter参数
     * @param updateStmt
     * @return
     */
    private List<ParameterMapping> prepareParameterMapping4Select(List<ParameterMapping> originalMappingList, Update updateStmt) {
        List<Expression> updateValueExpressions = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateValueExpressions.addAll(updateSet.getExpressions());
        }
        int removeParamCount = 0;
        for (Expression expression : updateValueExpressions) {
            if (expression instanceof JdbcParameter) {
                ++removeParamCount;
            }
        }
        return originalMappingList.subList(removeParamCount, originalMappingList.size());
    }

    /**
     * @param updateSql
     * @param originalDataObj
     * @return
     */
    private List<DataChangedRecord> compareAndGetUpdatedColumnDatas(String tableName, BoundSql updateSql, Statement statement, OriginalDataObj originalDataObj) {
        Map<String, Object> columnNameValMap = new HashMap<>(updateSql.getParameterMappings().size());
        List<Column> selectItemsFromUpdateSql = new ArrayList<>();
        if (statement instanceof Update) {
            Update updateStmt = (Update) statement;
            for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
                selectItemsFromUpdateSql.addAll(updateSet.getColumns());
                final List<Expression> updateList = updateSet.getExpressions();
                for (int i = 0; i < updateList.size(); ++i) {
                    Expression updateExps = updateList.get(i);
                    if (!(updateExps instanceof JdbcParameter)) {
                        columnNameValMap.put(updateSet.getColumns().get(i).getColumnName().toUpperCase(), updateExps.toString());
                    }
                }
            }
        } else if (statement instanceof Insert) {
            selectItemsFromUpdateSql.addAll(((Insert) statement).getColumns());
        }
        Map<String, String> relatedColumnsUpperCaseWithoutUnderline = new HashMap<>(selectItemsFromUpdateSql.size(), 1);
        for (Column item : selectItemsFromUpdateSql) {
            //FIRSTNAME: FIRST_NAME/FIRST-NAME/FIRST$NAME/FIRST.NAME
            relatedColumnsUpperCaseWithoutUnderline.put(item.toString().replaceAll("[._\\-$]", "").toUpperCase(), item.toString().toUpperCase());
        }
        MetaObject metaObject = SystemMetaObject.forObject(updateSql.getParameterObject());

        for (ParameterMapping parameterMapping : updateSql.getParameterMappings()) {
            String propertyName = parameterMapping.getProperty();
            if (propertyName.startsWith("ew.paramNameValuePairs")) {
                continue;
            }
            String[] arr = propertyName.split("\\.");
            String propertyNameTrim = arr[arr.length - 1].replace("_", "").toUpperCase();
            if (relatedColumnsUpperCaseWithoutUnderline.containsKey(propertyNameTrim)) {
                columnNameValMap.put(relatedColumnsUpperCaseWithoutUnderline.get(propertyNameTrim), metaObject.getValue(propertyName));
            }
        }

        final Set<String> ignoredColumns = ignoredTableColumns.get(tableName.toUpperCase());
        if (originalDataObj == null || originalDataObj.isEmpty()) {
            DataChangedRecord oneRecord = new DataChangedRecord();
            List<DataColumnChangeResult> updateColumns = new ArrayList<>(columnNameValMap.size());
            for (Map.Entry<String, Object> ety : columnNameValMap.entrySet()) {
                String columnName = ety.getKey();
                if ((ignoredColumns == null || !ignoredColumns.contains(columnName)) && !ignoreAllColumns.contains(columnName)) {
                    updateColumns.add(DataColumnChangeResult.constrcutByUpdateVal(columnName, ety.getValue()));
                }
            }
            oneRecord.setUpdatedColumns(updateColumns);
            return Collections.singletonList(oneRecord);
        }
        List<DataChangedRecord> originalDataList = originalDataObj.getOriginalDataObj();
        List<DataChangedRecord> updateDataList = new ArrayList<>(originalDataList.size());
        for (DataChangedRecord originalData : originalDataList) {
            if (originalData.hasUpdate(columnNameValMap, ignoredColumns, ignoreAllColumns)) {
                updateDataList.add(originalData);
            }
        }
        return updateDataList;
    }


    private Map<String, Object> buildParameterObjectMap(BoundSql boundSql) {
        MetaObject metaObject = SystemMetaObject.forObject(boundSql.getParameterObject());
        Map<String, Object> propertyValMap = new HashMap<>(boundSql.getParameterMappings().size());
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String propertyName = parameterMapping.getProperty();
            if (propertyName.startsWith("ew.paramNameValuePairs")) {
                continue;
            }
            Object propertyValue = metaObject.getValue(propertyName);
            propertyValMap.put(propertyName, propertyValue);
        }
        return propertyValMap;

    }


    private String buildOriginalData(Select selectStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {
            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder sb = new StringBuilder("[");
            while (resultSet.next()) {
                sb.append("{");
                for (int i = 1; i <= columnCount; ++i) {
                    sb.append("\"").append(metaData.getColumnName(i)).append("\":\"");
                    Object res = resultSet.getObject(i);
                    if (res instanceof Clob) {
                        sb.append(DataColumnChangeResult.convertClob((Clob) res));
                    } else {
                        sb.append(res);
                    }
                    sb.append("\",");
                }
                sb.replace(sb.length() - 1, sb.length(), "}");
            }
            sb.append("]");
            resultSet.close();
            return sb.toString();
        } catch (Exception e) {
            logger.error("try to get record tobe deleted for selectStmt={}", selectStmt, e);
            return "failed to get original data";
        }
    }

    private OriginalDataObj buildOriginalObjectData(Select selectStmt, Column pk, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {

            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            List<DataChangedRecord> originalObjectDatas = new LinkedList<>();
            while (resultSet.next()) {
                originalObjectDatas.add(prepareOriginalDataObj(resultSet, pk));
            }
            OriginalDataObj result = new OriginalDataObj();
            result.setOriginalDataObj(originalObjectDatas);
            resultSet.close();
            return result;
        } catch (Exception e) {
            logger.error("try to get record tobe updated for selectStmt={}", selectStmt, e);
            return new OriginalDataObj();
        }
    }

    /**
     * get records : include related column with original data in DB
     *
     * @param resultSet
     * @param pk
     * @return
     * @throws SQLException
     */
    private DataChangedRecord prepareOriginalDataObj(ResultSet resultSet, Column pk) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<DataColumnChangeResult> originalColumnDatas = new LinkedList<>();
        DataColumnChangeResult pkval = null;
        for (int i = 1; i <= columnCount; ++i) {
            String columnName = metaData.getColumnName(i).toUpperCase();
            DataColumnChangeResult col = DataColumnChangeResult.constrcutByOriginalVal(columnName, resultSet.getObject(i));
            if (pk != null && columnName.equalsIgnoreCase(pk.getColumnName())) {
                pkval = col;
            } else {
                originalColumnDatas.add(col);
            }
        }
        DataChangedRecord changedRecord = new DataChangedRecord();
        changedRecord.setOriginalColumnDatas(originalColumnDatas);
        if (pkval != null) {
            changedRecord.setPkColumnName(pkval.getColumnName());
            changedRecord.setPkColumnVal(pkval.getOriginalValue());
        }
        return changedRecord;
    }

    private Columns2SelectItemsResult buildColumns2SelectItems(String tableName, List<Column> columns) {
        if (columns == null || columns.isEmpty()) {
            return Columns2SelectItemsResult.build(Collections.singletonList(new AllColumns()), 0);
        }
        List<SelectItem> selectItems = new ArrayList<>(columns.size());
        for (Column column : columns) {
            selectItems.add(new SelectExpressionItem(column));
        }
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            if (tableName.equalsIgnoreCase(tableInfo.getTableName())) {
                Column pk = new Column(tableInfo.getKeyColumn());
                selectItems.add(new SelectExpressionItem(pk));
                Columns2SelectItemsResult result = Columns2SelectItemsResult.build(selectItems, 1);
                result.setPk(pk);
                return result;
            }
        }
        return Columns2SelectItemsResult.build(selectItems, 0);
    }

    private String buildParameterObject(BoundSql boundSql) {
        Object paramObj = boundSql.getParameterObject();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (paramObj instanceof Map) {
            Map<String, Object> paramMap = (Map<String, Object>) paramObj;
            int index = 1;
            boolean hasParamIndex = false;
            String key;
            while (paramMap.containsKey((key = "param" + index))) {
                Object paramIndex = paramMap.get(key);
                sb.append("\"").append(key).append("\"").append(":").append("\"").append(paramIndex).append("\"").append(",");
                hasParamIndex = true;
                ++index;
            }
            if (hasParamIndex) {
                sb.delete(sb.length() - 1, sb.length());
                sb.append("}");
                return sb.toString();
            }
            for (Map.Entry<String, Object> ety : paramMap.entrySet()) {
                sb.append("\"").append(ety.getKey()).append("\"").append(":").append("\"").append(ety.getValue()).append("\"").append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            sb.append("}");
            return sb.toString();
        }
        sb.append("param:").append(paramObj);
        sb.append("}");
        return sb.toString();
    }

    public OperationResult processDelete(Delete deleteStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        Table table = deleteStmt.getTable();
        Expression where = deleteStmt.getWhere();
        Select selectStmt = new Select();
        PlainSelect selectBody = new PlainSelect();
        selectBody.setFromItem(table);
        selectBody.setSelectItems(Collections.singletonList(new AllColumns()));
        selectBody.setWhere(where);
        selectStmt.setSelectBody(selectBody);
        String originalData = buildOriginalData(selectStmt, mappedStatement, boundSql, connection);
        OperationResult result = new OperationResult();
        result.setOperation("delete");
        result.setTableName(table.getName());
        result.setRecordStatus(originalData.startsWith("["));
        result.setChangedData(originalData);
        return result;
    }

    /**
     * ignoredColumns = TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2; TABLE3.*; *.COLUMN1,COLUMN2
     * 多个表用分号分隔
     * TABLE_NAME1.COLUMN1,COLUMN2 : 表示忽略这个表的这2个字段
     * TABLE3.*: 表示忽略这张表的INSERT/UPDATE，delete暂时还保留
     * *.COLUMN1,COLUMN2:表示所有表的这个2个字段名都忽略
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {

        String ignoredTableColumns = properties.getProperty("ignoredTableColumns");
        if (ignoredTableColumns == null || ignoredTableColumns.trim().isEmpty()) {
            return;
        }
        String[] array = ignoredTableColumns.split(";");
        for (String table : array) {
            int index = table.indexOf(".");
            if (index == -1) {
                logger.warn("invalid data={} for ignoredColumns, format should be TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2;", table);
                continue;
            }
            String tableName = table.substring(0, index).trim().toUpperCase();
            String[] columnArray = table.substring(index + 1).split(",");
            Set<String> columnSet = new HashSet<>(columnArray.length);
            for (String column : columnArray) {
                column = column.trim().toUpperCase();
                if (column.isEmpty()) {
                    continue;
                }
                columnSet.add(column);
            }
            if ("*".equals(tableName)) {
                ignoreAllColumns.addAll(columnSet);
            } else {
                this.ignoredTableColumns.put(tableName, columnSet);
            }
        }
    }

    public static String convertDoubleQuotes(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString().replace("\"", "\\\"");
    }

    @Data
    public static class OperationResult {

        private String operation;
        private boolean recordStatus;
        private String tableName;
        private String changedData;
        /**
         * cost for this plugin, ms
         */
        private long cost;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public boolean isRecordStatus() {
            return recordStatus;
        }

        public void setRecordStatus(boolean recordStatus) {
            this.recordStatus = recordStatus;
        }

        public void buildDataStr(List<DataChangedRecord> records) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (DataChangedRecord r : records) {
                sb.append(r.generateUpdatedDataStr()).append(",");
            }
            if (sb.length() == 1) {
                sb.append("]");
                changedData = sb.toString();
                return;
            }
            sb.replace(sb.length() - 1, sb.length(), "]");
            changedData = sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"").append("tableName").append("\"").append(":").append("\"").append(tableName).append("\"").append(",");
            sb.append("\"").append("operation").append("\"").append(":").append("\"").append(operation).append("\"").append(",");
            sb.append("\"").append("recordStatus").append("\"").append(":").append("\"").append(recordStatus).append("\"").append(",");
            sb.append("\"").append("changedData").append("\"").append(":").append(changedData).append(",");
            sb.append("\"").append("cost(ms)").append("\"").append(":").append(cost);
            sb.append("}");
            return sb.toString();
        }

    }

}


@Data
class Columns2SelectItemsResult {

    private Column pk;
    /**
     * all column with additional columns: ID, etc.
     */
    private List<SelectItem> selectItems;
    /**
     * newly added column count from meta data.
     */
    private int additionalItemCount;

    public static Columns2SelectItemsResult build(List<SelectItem> selectItems, int additionalItemCount) {
        Columns2SelectItemsResult result = new Columns2SelectItemsResult();
        result.setSelectItems(selectItems);
        result.setAdditionalItemCount(additionalItemCount);
        return result;
    }

}

@Data
class OriginalDataObj {

    private List<DataChangedRecord> originalDataObj;

    public boolean isEmpty() {
        return originalDataObj == null || originalDataObj.isEmpty();
    }

}

@Data
class DataColumnChangeResult {

    private String columnName;
    private Object originalValue;
    private Object updateValue;

    @SuppressWarnings("rawtypes")
    public boolean isDataChanged(Object updateValue) {
        if (!Objects.equals(originalValue, updateValue)) {
            if (updateValue instanceof Number && originalValue instanceof Number) {
                BigDecimal update = new BigDecimal(updateValue.toString());
                BigDecimal original = new BigDecimal(originalValue.toString());
                return update.compareTo(original) != 0;
            }
            if (updateValue instanceof Date && originalValue instanceof Date) {
                Date update = (Date) updateValue;
                Date original = (Date) originalValue;
                return update.compareTo(original) != 0;
            }
            if (originalValue instanceof Clob) {
                String originalStr = convertClob((Clob) originalValue);
                setOriginalValue(originalStr);
                return !originalStr.equals(updateValue);
            }
            return true;
        }
        if (originalValue instanceof Comparable) {
            Comparable original = (Comparable) originalValue;
            Comparable update = (Comparable) updateValue;
            return original.compareTo(update) != 0;
        }
        return false;
    }

    public static String convertClob(Clob clobObj) {
        try {
            return clobObj.getSubString(0, (int) clobObj.length());
        } catch (Exception e) {
            try (Reader is = clobObj.getCharacterStream()) {
                char[] chars = new char[64];
                int readChars;
                StringBuilder sb = new StringBuilder();
                while ((readChars = is.read(chars)) != -1) {
                    sb.append(chars, 0, readChars);
                }
                return sb.toString();
            } catch (Exception e2) {
                //ignored
                return "unknown clobObj";
            }
        }
    }

    public static DataColumnChangeResult constrcutByUpdateVal(String columnName, Object updateValue) {
        DataColumnChangeResult res = new DataColumnChangeResult();
        res.setColumnName(columnName);
        res.setUpdateValue(updateValue);
        return res;
    }

    public static DataColumnChangeResult constrcutByOriginalVal(String columnName, Object originalValue) {
        DataColumnChangeResult res = new DataColumnChangeResult();
        res.setColumnName(columnName);
        res.setOriginalValue(originalValue);
        return res;
    }

    public String generateDataStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(columnName).append("\"").append(":").append("\"").append(convertDoubleQuotes(originalValue)).append("->").append(convertDoubleQuotes(updateValue)).append("\"").append(",");
        return sb.toString();
    }
}

@Data
class DataChangedRecord {
    private String pkColumnName;
    private Object pkColumnVal;
    private List<DataColumnChangeResult> originalColumnDatas;
    private List<DataColumnChangeResult> updatedColumns;

    public boolean hasUpdate(Map<String, Object> columnNameValMap, Set<String> ignoredColumns, Set<String> ignoreAllColumns) {
        if (originalColumnDatas == null) {
            return true;
        }
        boolean hasUpdate = false;
        updatedColumns = new ArrayList<>(originalColumnDatas.size());
        for (DataColumnChangeResult originalColumn : originalColumnDatas) {
            final String columnName = originalColumn.getColumnName().toUpperCase();
            if (ignoredColumns != null && ignoredColumns.contains(columnName) || ignoreAllColumns.contains(columnName)) {
                continue;
            }
            Object updatedValue = columnNameValMap.get(columnName);
            if (originalColumn.isDataChanged(updatedValue)) {
                hasUpdate = true;
                originalColumn.setUpdateValue(updatedValue);
                updatedColumns.add(originalColumn);
            }
        }
        return hasUpdate;
    }

    public String generateUpdatedDataStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (pkColumnName != null) {
            sb.append("\"").append(pkColumnName).append("\"").append(":").append("\"").append(convertDoubleQuotes(pkColumnVal)).append("\"").append(",");
        }
        for (DataColumnChangeResult update : updatedColumns) {
            sb.append(update.generateDataStr());
        }
        sb.replace(sb.length() - 1, sb.length(), "}");
        return sb.toString();
    }


}
