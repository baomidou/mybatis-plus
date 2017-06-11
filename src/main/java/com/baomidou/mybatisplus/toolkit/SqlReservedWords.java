/**
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
package com.baomidou.mybatisplus.toolkit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;

/**
 * <p>
 * 数据库保留字转义处理类
 * </p>
 *
 * @author hubin
 * @date 2016-08-18
 */
public class SqlReservedWords {

    public static final Set<String> RESERVED_WORDS;

    static {
        String[] words = {"AUDIT", "VOLUMES", "MINVALUE", "STATIC", "FLOOR", "CATALOG", "YEAR", "TRIGGER_CATALOG", "WLM",
                "DISCONNECT", "PARTITION", "LEFT", " MODE", "SEARCH", "CURRENT_PATH", "DAYOFYEAR", "SIZE", "RESTRICT", "RELEASE",
                "WHERE", "POSTFIX", "SQLWARNING", "UID", "AS", "AT", "SYSID", "DATABASE", "SPECIFIC_NAME", "TIMEZONE_MINUTE",
                "LC_CTYPE", "ATTRIBUTES", "LOCATION", "A", "DOMAIN", "C", "SET", " INCREMENT", "MERGE", "G", "DAYS", "HEADER",
                "K", "CONSTRAINT", "M", "PRECISION", "ROLE", "COLLATION_NAME", "BY", "STRAIGHT_JOIN", "CHARACTER", "INSTEAD",
                "DUAL", "DSNHATTR", "BERNOULLI", " ADD", "CLUSTERED", "CONTINUE", "DIV", "OPERATOR", "SETS", "SECURITY",
                "CURRENT_LC_CTYPE", "TRUNCATE", "CURSOR", "SQL_LOW_PRIORITY_UPDATES", "CONSTRAINT_SCHEMA", "STATEMENT",
                "TABLE_NAME", "NODENUMBER", "DO", "RECOVERY", "LOW_PRIORITY", "ADMIN", "ESCAPED", "CAPTURE", "ALLOW", "VARYING",
                "DISABLE", "STRICT", "SQL_BIG_RESULT", "ABORT", "FOR", "IMPLICIT", "USING", "EXEC", "DEFERRABLE", "RAISERROR",
                "USER_DEFINED_TYPE_NAME", "VALIDATOR", "UNDO", "STATE", "WITHIN", "NCHAR", "ABSOLUTE", "PREORDER", "SCHEMA",
                "SEQUENCE", "ASSIGNMENT", "COLUMNS", "END-EXEC1", "GO", "ROW_NUMBER", "BIT", "INTERSECT", "SYSDATE", "WITH",
                "CLASS_ORIGIN", "ACTION", "ISNULL", "VALIDATE", " COMPRESS", "START", "CHARACTER_LENGTH", "BULK", "EVERY",
                "NULLIF", "CEIL", "FLUSH", "MIN_ROWS", "GRANTED", "IF", "BIT_LENGTH", "PARAMETER", "CHARACTER_SET_SCHEMA",
                "DEFINITION", "NCLOB", "FIELDS", "IN", "DISTINCT", "STYLE", "IS", "MASTER_BIND", "REGR_INTERCEPT", "TERMINATE",
                "FORCE", "FENCED", " IMMEDIATE", "MAP", "READTEXT", "SYSFUN", "EXIT", "DBCC", "OPENROWSET", "COLLATION", "GOTO",
                "RAID0", "MAX", "CASCADE", "DELIMITERS", "TRANSACTION", "SECQTY", "IGNORE", "COMMENT", "OFF", "CHARACTERS",
                "IDENTITYCOL", "USAGE", "UPDATE", "SAVE", "REQUIRE", "DISTINCTROW", "USE", "RETURNS", "LOCATORS",
                "CHARACTERISTICS", "FIRST", "LINENO", "ONLINE", "SELECT", "EXCLUDING", "TABLES", "OUTFILE", "ERASE", "GREATEST",
                "UTC_DATE", "VARCHARACTER", "INVOKER", "STOGROUP", "DEPTH", "CURRENT_USER", "LN", "ARRAY", "ATOMIC", " ALTER",
                "COLUMN_NAME", "COLUMN", "MODE", " COMMENT", "DECIMAL", "RESET", "NEW_TABLE", "ROUTINE_NAME", "COMPRESS",
                "OVERLAY", "READ_WRITE", "COALESCE", "STATUS", " EXCLUSIVE", "UNBOUNDED", "REGR_SLOPE", "CORRESPONDING",
                "TIMESTAMP", "MESSAGE_OCTET_LENGTH", "SQL_LOG_OFF", "RETURNED_LENGTH", "DISALLOW", "PRIVILEGES", "SQL", "READ",
                " IDENTIFIED", "REAL", "LESS", "DIAGNOSTICS", "QUERYNO", "NO", "FLOAT", "COMMAND_FUNCTION_CODE",
                "CURRENT_TIMESTAMP", "TIES", "ROUTINE", "SUBLIST", "FOLLOWING", "ROLLBACK", "MEMBER", "DSSIZE", "DUMP",
                "EXTERNAL", "GROUPING", "OF", "CHANGE", "RECHECK", " NUMBER", "ON", "DBINFO", "OR", "EQUALS", "CREATEROLE",
                "PRIMARY", "SSL", "MATCHED", "DAYOFWEEK", "DYNAMIC_FUNCTION_CODE", "ENCODING", "OPTIMIZATION", "SECOND",
                "UNKNOWN", "HOUR_SECOND", "REFERENCES", "ROWS", "JAVA", "SPATIAL", "INHERIT", "CREATE",
                "PARAMETER_SPECIFIC_SCHEMA", "LEAST", "OLD", "TRIGGER", "BETWEEN", "OBID", "CONVERT", "POSITION", "PROCESS",
                "SQL_SELECT_LIMIT", "NOTHING", "DEALLOCATE", "SUBPAGES", "INNER", "SQL_BIG_SELECTS", "EACH", "OPTIONALLY",
                "SETUSER", "BIGINT", "NOAUDIT", "SUM", "OPTIONS", "MIN", "BITVAR", "VARCHAR", "SQLCA", "KEY", "PROGRAM", "CALL",
                "WAITFOR", "RELOAD", "DELAY_KEY_WRITE", "USER_DEFINED_TYPE_SCHEMA", "RLIKE", "EDITPROC", "GROUP",
                "DATETIME_INTERVAL_PRECISION", "ASSOCIATE", "RESTORE", "OFFSET", "TEMPORARY", "STANDARD", "OPENDATASOURCE",
                "STATISTICS", "COBOL", "SECOND_MICROSECOND", "NULLABLE", "COMMITTED", "DELAYED", "PERCENT", " ONLINE", "DB2SQL",
                "TO", "CONSTRUCTOR", "DB2GNRL", "UNION", "FREEZE", "SCOPE", "CLASS", "VIEW", "LINES", "ASSERTION", "PACKAGE",
                "TRIGGER_NAME", "CONSTRAINTS", "LABEL", "CURRENT_TIME", "DEFERRED", "REPLACE", "KEY_MEMBER", "INTEGER",
                "OVERRIDING", "UNIQUE", " MAXEXTENTS", "TRAILING", "COVAR_SAMP", "FINAL", " NOAUDIT", "FULL", "NAME", "YEARS",
                "ROW_COUNT", "NOCREATEROLE", "CEILING", "LAST", "MAXVALUE", "QUOTE", "TOAST", "LOCALTIME", "CONTAINS", "GENERAL",
                "DELIMITER", "STDIN", "REGEXP", "REGR_COUNT", "NOTIFY", "NEXT", "GLOBAL", "LEAVE", "SHOW", "SHUTDOWN", "VERBOSE",
                "NORMALIZE", "CURRENT_TIMEZONE", "MOD", "EXISTS", "TIME", "MYISAM", "INHERITS", "DATETIME", "HOURS",
                "NOMINVALUE", "BOOL", "ERRLVL", "NESTING", "FALSE", "MINUTES", "SECTION", "NOCHECK", "NOTNULL",
                "PERCENTILE_CONT", "SYMMETRIC", "VALID", "PLAN", "SHARE", "TRAN", "STDDEV_SAMP", "WHEN",
                "TRANSACTIONS_COMMITTED", "BREAK", "LOCAL", "CONSTRAINT_CATALOG", "DICTIONARY", "LOGIN", "CLUSTER", "GRANTS",
                "DAY_MINUTE", "LONGBLOB", "CYCLE", "CAST", "INSTANCE", "VARCHAR2", "FUNCTION", "LEADING", "MODIFIES", "NOWAIT",
                "CASE", "OUT", "OPTIMIZE", "REGR_SXX", "REGR_SXY", "OVERLAPS", "GET", "DENSE_RANK", "PUBLIC", "COUNT", "TREAT",
                "NAMES", " NOCOMPRESS", "IDENTITY_INSERT", "NONCLUSTERED", "LENGTH", "UNSIGNED", "CHAR", "BEGIN", "MAX_ROWS",
                "WRITE", "ORDER", "ISOLATION", "REPLICATION", "SQL_CALC_FOUND_ROWS", "REGR_SYY", "LANCOMPILER", " CLUSTER",
                "CHARACTER_SET_NAME", "SIGNAL", "SUBMULTISET", "COLLATE", "MODIFY", "INSTANTIABLE", "UNCOMMITTED", "RESIGNAL",
                "MORE", "PROC", "REPEATABLE", "COMPLETION", "KEY_TYPE", "KILL", "TRANSFORMS", "VOLATILE", "INPUT", "SUBSTRING",
                "ZONE", "VCAT", "DEREF", "AUXILIARY", "REGR_AVGY", "REGR_AVGX", "TEMPLATE", "INCLUDING", "INSENSITIVE", "BOTH",
                "CHARACTER_SET_CATALOG", "ENABLE", "EXCEPT", "HOSTS", "SCHEMA_NAME", "PREFIX", "SCROLL", "METHOD", "DAY_SECOND",
                "DESTRUCTOR", "OIDS", "INT", "PASCAL", "COLLID", "PART", "ALSO", "CARDINALITY", "ACCESS", "OPENQUERY", "CLOB",
                "COMMIT", "DISPATCH", "STRUCTURE", "DETERMINISTIC", "SAVEPOINT", "UNTIL", "USER", "TEMP", "MEDIUMBLOB", "MOVE",
                "CROSS", "SMALLINT", "UESCAPE", "USER_DEFINED_TYPE_CATALOG", "RESULT", "SQLID", "PATH", "RESULT_SET_LOCATOR",
                "PURGE", "TRIM", "ROWGUIDCOL", "RAW", "RANK", "VAR_POP", "MUMPS", "TRANSLATION", "MINUS", "EXPLAIN",
                "PARAMETER_SPECIFIC_CATALOG", " INITIAL", "MESSAGE_LENGTH", "HOUR_MINUTE", "LISTEN", "WIDTH_BUCKET", "STORAGE",
                "CURRENT_DEFAULT_TRANSFORM_GROUP", "STDOUT", "CUBE", "IMMUTABLE", "REGR_R2", "SQL_LOG_UPDATE", "XOR",
                "FREETEXTTABLE", "ALTER", "MONTHNAME", "FUSION", "DESTROY", "PARAMETER_SPECIFIC_NAME", "TEXTSIZE", "SPACE",
                "UPPER", "ABS", "CREATEUSER", "INTEGRITY", "OCTET_LENGTH", "TINYINT", "INTERVAL", "COLLATION_SCHEMA",
                "CATALOG_NAME", "UNLISTEN", "MASTER_SSL_VERIFY_SERVER_CERT", "POWER", "CONNECTION", "PAD", "REF", "LOCALE",
                "OPERATION", "SIMPLE", "VARBINARY", "VARIABLES", "ADA", "VIRTUAL", " FILE", "SYSTEM", "ADD", "SCOPE_CATALOG",
                "SQLERROR", "CHECKED", "VARIANT", "OLD_TABLE", "INFIX", "TRUSTED", "INDEX", "FOUND", "HOLD", "EXTRACT",
                "OFFSETS", "ATTRIBUTE", "PERCENTILE_DISC", "ITERATE", "CURRENT_SERVER", "CACHE", " LOCK", "CURRENT", " NOWAIT",
                "RETURNED_SQLSTATE", "SYSPROC", "CONNECTION_NAME", "END", "PRESERVE", "LOAD", "TERMINATED", "BINARY", "FORWARD",
                "SOME", "LAST_INSERT_ID", "OUTER", "INFILE", "RENAME", "EXCLUSIVE", "FILTER", "IDENTIFIED", "NORMALIZED",
                " COLUMN", "INITIALLY", "OVER", "CURRENT_ROLE", "GRANT", "OTHERS", "SONAME", "CHAR_LENGTH", "NOMAXVALUE",
                "ROWID", "DEFAULT", "SQRT", "JOIN", "LOCK", "TEXT", "UNNEST", "AVG_ROW_LENGTH", "SESSION_USER", "AGGREGATE",
                "MULTISET", "ELSE", "TRANSACTION_ACTIVE", "LANGUAGE", "PERCENT_RANK", "ENUM", "NATIONAL", "SETOF",
                "RETURNED_CARDINALITY", "SYNONYM", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "SPECIFICTYPE", "TOP", "FORTRAN",
                "DEGREE", "ASYMMETRIC", "GRAPHIC", "ALWAYS", "MEDIUMTEXT", "SYSTEM_USER", "ROUTINE_CATALOG", "CURSOR_NAME",
                "RIGHT", "STABLE", "FILE", "CREATEDB", "DISTRIBUTED", "FILLFACTOR", "FETCH", "NUMERIC", "STARTING", "REVOKE",
                "SQLEXCEPTION", "DYNAMIC", "CHAIN", "CALLED", "INCREMENT", "ELEMENT", "MAXEXTENTS", "ROUTINE_SCHEMA",
                "IO_AFTER_GTIDS", "TRIGGER_SCHEMA", "ALL", "NEW", "THAN", "ALIAS", "HOST", "VALUE", "LOGS", "SERIALIZABLE",
                "X509", "AUTO_INCREMENT", "BACKUP", "MINUTE_MICROSECOND", "ALLOCATE", "HOLDLOCK", "MINUTE", "SCALE", "TINYTEXT",
                "DESCRIBE", "NOCREATEDB", " INTERSECT", " PCTFREE", " PRIOR", "NULL", "TRUE", "PCTFREE", "EXISTING",
                "PARAMETERS", "OBJECT", "TABLESPACE", "UTC_TIME", " LEVEL", "MODULE", "PASSWORD", "EXCLUDE", "SQL_WARNINGS",
                "AND", "SQLCODE", "ROW", "CURRENT_DATE", "MESSAGE_TEXT", "DISK", "RANGE", "VACUUM", "MLSLABEL", "STORED", "HOUR",
                "CONCAT", "APPLICATION", "INITIAL", "ANY", "PLI", "HEAP", " AUDIT", "NATURAL", "NOINHERIT", "STORES", "UNNAMED",
                "KEYS", "RESTART", "READS", "NUMPARTS", "CSV", "IMPLEMENTATION", "ORDERING", "TRANSLATE", "REINDEX", "JAR",
                "EXP", "MATCH", "PRINT", "NOCREATEUSER", "CHECKSUM", "ELSEIF", "MONTH", "ROWCOUNT", "AFTER", "CLOSE", "RRN",
                "MONTHS", "OWNER", "DENY", "END-EXEC", "INCLUDE", "OCTETS", "UPDATETEXT", "PRIOR", "SYSIBM", " DATE",
                "SCRATCHPAD", "NODENAME", "IDENTITY", "ARE", "FULLTEXT", "SOURCE", "CONDITION", "THEN", "PROCEDURAL", "UNLOCK",
                "HIERARCHY", "ORDINALITY", "INTO", "MICROSECONDS", "REPEAT", "MICROSECOND", "EXCEPTION", "INDICATOR", "FREE",
                "RETURNED_OCTET_LENGTH", "NOCOMPRESS", "ASC", "DELETE", "COVAR_POP", "VARIABLE", " INDEX", "PREPARED",
                "GENERATED", "SIMILAR", "LONG", "RESOURCE", "INT1", "INT2", "PROCEDURE", "INT3", "STDDEV_POP", "INT4", "SECONDS",
                "COLLECT", "ANALYZE", "RUN", "UNDER", "INT8", "OPEN", "DERIVED", "NO_WRITE_TO_BINLOG", "REFERENCING", "STRING",
                "PSID", "BREADTH", "STAY", "LOCATOR", "NOCACHE", "LOOP", "HIGH_PRIORITY", "IMMEDIATE", "DESC", "FREETEXT",
                "NUMBER", "AUX", "OUTPUT", "LONGTEXT", "DATABASES", "BOOLEAN", "AVG", "NOT", "PLACING", "INTERSECTION", "LOWER",
                "SPECIFIC", "MINUTE_SECOND", "FLOAT8", "HAVING", "FIELDPROC", "FLOAT4", "SQLSTATE", "RECONFIGURE", "LOCKMAX",
                "BACKWARD", "BUFFERPOOL", "VALIDPROC", " MINUS", "COMMAND_FUNCTION", "DROP", "RETURN", "FOREIGN",
                "PARAMETER_NAME", "TSEQUAL", "SQL_SMALL_RESULT", "RULE", "SERVER_NAME", "DAYOFMONTH", "IO_BEFORE_GTIDS",
                "PARTIAL", "MEDIUMINT", "TRANSACTIONS_ROLLED_BACK", "OPENXML", " DROP", "ESCAPE", "SCOPE_NAME", "ISAM", "LINEAR",
                "PARAMETER_ORDINAL_POSITION", " OFFLINE", "ROWNUM", "DATETIME_INTERVAL_CODE", "DEFINED", "LOCALTIMESTAMP",
                " CONNECT", "ISOBID", "TABLE", "ANALYSE", "DEFINER", "SCOPE_SCHEMA", "COLLATION_CATALOG", "NONE", "PROCESSLIST",
                "TYPE", "USER_DEFINED_TYPE_CODE", "DESCRIPTOR", "PIECESIZE", "OPTION", "WHENEVER", "ENCLOSED", "LEVEL",
                "COUNT_BIG", "ASENSITIVE", "LOCKSIZE", "TINYBLOB", "PREPARE", "CHECK", "WITHOUT", "WORK", "HANDLER", "CUME_DIST",
                "WRITETEXT", "INITIALIZE", "DAY_HOUR", "ILIKE", "CONNECT", "TABLESAMPLE", "INSERT_ID", "MIDDLEINT", "RELATIVE",
                "LARGE", "ACCESSIBLE", "VALUES", "DOUBLE", "ASUTIME", "DEFAULTS", "NOLOGIN", "TIMEZONE_HOUR", "COMPUTE", "COPY",
                "SELF", "SESSION", "NOCYCLE", "DUMMY", "WINDOW", "EXECUTE", "PRECEDING", "PACK_KEYS", "NOORDER", "CHECKPOINT",
                " LONG", "DAY", "AUTHORIZATION", "CCSID", "COLLECTION", "BLOB", "PRIQTY", "RECURSIVE", "ONLY", "FROM",
                "SQL_BIG_TABLES", "LATERAL", "TRANSFORM", "HOUR_MICROSECOND", "SENSITIVE", "SUBCLASS_ORIGIN", "CONVERSION",
                "DAY_MICROSECOND", "SEPARATOR", "OPTIMIZER_COSTS", "NOSUPERUSER", "DATE", "ROLLUP", "TOP_LEVEL_COUNT", "CORR",
                "UNENCRYPTED", "UTC_TIMESTAMP", "LIKE", "ZEROFILL", "DATA", "SUCCESSFUL", "INSERT", "YEAR_MONTH", "OFFLINE",
                "INOUT", "VAR_SAMP", "BROWSE", "SCHEMAS", "CONSTRAINT_NAME", "PARAMETER_MODE", "LIMIT", "LINKTYPE", "NULLS",
                "DEC", "CASCADED", "ENCRYPTED", "CONTAINSTABLE", "DYNAMIC_FUNCTION", "CONDITION_NUMBER", "BEFORE", "DB2GENERAL",
                "DECLARE", "SUPERUSER", "WHILE"};

        RESERVED_WORDS = new HashSet<>(words.length);
        Collections.addAll(RESERVED_WORDS, words);
    }

    /**
     * Utility class - no instances allowed
     */
    private SqlReservedWords() {

    }

    /**
     * <p>
     * 数据库字段转义
     * </p>
     *
     * @param globalConfig
     *            全局配置
     * @param column
     *            数据库字段
     * @return
     */
    public static String convert(GlobalConfiguration globalConfig, String column) {
		return containsWord(column) ? convertQuote(globalConfig, column) : column;
    }

	public static String convertQuote(GlobalConfiguration globalConfig, String column) {
		String identifierQuote = globalConfig.getIdentifierQuote();
        if (null != identifierQuote) {
            return String.format(identifierQuote, column);
		}
		return column;
	}

    /**
     * 判断关键字中是否包含该字段
     *
     * @param word
     * @return
     */
    public static boolean containsWord(String word) {
        return null != word && RESERVED_WORDS.contains(word.toUpperCase());
    }

}
