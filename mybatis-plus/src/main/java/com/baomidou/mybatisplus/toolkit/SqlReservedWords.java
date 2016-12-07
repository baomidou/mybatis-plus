/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.toolkit;

import java.util.HashSet;
import java.util.Set;

import com.baomidou.mybatisplus.enums.DBType;

/**
 * <p>
 * 数据库保留字转义处理类
 * </p>
 * 
 * @author hubin
 * @date 2016-08-18
 */
public class SqlReservedWords {

	private static Set<String> RESERVED_WORDS;

	static {
		String[] words = { "A", //$NON-NLS-1$
				"ABORT", //$NON-NLS-1$
				"ABS", //$NON-NLS-1$
				"ABSOLUTE", //$NON-NLS-1$
				"ACCESS", //$NON-NLS-1$
				"ACTION", //$NON-NLS-1$
				"ADA", //$NON-NLS-1$
				"ADD", // DB2 //$NON-NLS-1$
				"ADMIN", //$NON-NLS-1$
				"AFTER", // DB2 //$NON-NLS-1$
				"AGGREGATE", //$NON-NLS-1$
				"ALIAS", // DB2 //$NON-NLS-1$
				"ALL", // DB2 //$NON-NLS-1$
				"ALLOCATE", // DB2 //$NON-NLS-1$
				"ALLOW", // DB2 //$NON-NLS-1$
				"ALSO", //$NON-NLS-1$
				"ALTER", // DB2 //$NON-NLS-1$
				"ALWAYS", //$NON-NLS-1$
				"ANALYSE", //$NON-NLS-1$
				"ANALYZE", //$NON-NLS-1$
				"AND", // DB2 //$NON-NLS-1$
				"ANY", // DB2 //$NON-NLS-1$
				"APPLICATION", // DB2 //$NON-NLS-1$
				"ARE", //$NON-NLS-1$
				"ARRAY", //$NON-NLS-1$
				"AS", // DB2 //$NON-NLS-1$
				"ASC", //$NON-NLS-1$
				"ASENSITIVE", //$NON-NLS-1$
				"ASSERTION", //$NON-NLS-1$
				"ASSIGNMENT", //$NON-NLS-1$
				"ASSOCIATE", // DB2 //$NON-NLS-1$
				"ASUTIME", // DB2 //$NON-NLS-1$
				"ASYMMETRIC", //$NON-NLS-1$
				"AT", //$NON-NLS-1$
				"ATOMIC", //$NON-NLS-1$
				"ATTRIBUTE", //$NON-NLS-1$
				"ATTRIBUTES", //$NON-NLS-1$
				"AUDIT", // DB2 //$NON-NLS-1$
				"AUTHORIZATION", // DB2 //$NON-NLS-1$
				"AUTO_INCREMENT", //$NON-NLS-1$
				"AUX", // DB2 //$NON-NLS-1$
				"AUXILIARY", // DB2 //$NON-NLS-1$
				"AVG", //$NON-NLS-1$
				"AVG_ROW_LENGTH", //$NON-NLS-1$
				"BACKUP", //$NON-NLS-1$
				"BACKWARD", //$NON-NLS-1$
				"BEFORE", // DB2 //$NON-NLS-1$
				"BEGIN", // DB2 //$NON-NLS-1$
				"BERNOULLI", //$NON-NLS-1$
				"BETWEEN", // DB2 //$NON-NLS-1$
				"BIGINT", //$NON-NLS-1$
				"BINARY", // DB2 //$NON-NLS-1$
				"BIT", //$NON-NLS-1$
				"BIT_LENGTH", //$NON-NLS-1$
				"BITVAR", //$NON-NLS-1$
				"BLOB", //$NON-NLS-1$
				"BOOL", //$NON-NLS-1$
				"BOOLEAN", //$NON-NLS-1$
				"BOTH", //$NON-NLS-1$
				"BREADTH", //$NON-NLS-1$
				"BREAK", //$NON-NLS-1$
				"BROWSE", //$NON-NLS-1$
				"BUFFERPOOL", // DB2 //$NON-NLS-1$
				"BULK", //$NON-NLS-1$
				"BY", // DB2 //$NON-NLS-1$
				"C", //$NON-NLS-1$
				"CACHE", // DB2 //$NON-NLS-1$
				"CALL", // DB2 //$NON-NLS-1$
				"CALLED", // DB2 //$NON-NLS-1$
				"CAPTURE", // DB2 //$NON-NLS-1$
				"CARDINALITY", // DB2 //$NON-NLS-1$
				"CASCADE", //$NON-NLS-1$
				"CASCADED", // DB2 //$NON-NLS-1$
				"CASE", // DB2 //$NON-NLS-1$
				"CAST", // DB2 //$NON-NLS-1$
				"CATALOG", //$NON-NLS-1$
				"CATALOG_NAME", //$NON-NLS-1$
				"CCSID", // DB2 //$NON-NLS-1$
				"CEIL", //$NON-NLS-1$
				"CEILING", //$NON-NLS-1$
				"CHAIN", //$NON-NLS-1$
				"CHANGE", //$NON-NLS-1$
				"CHAR", // DB2 //$NON-NLS-1$
				"CHAR_LENGTH", //$NON-NLS-1$
				"CHARACTER", // DB2 //$NON-NLS-1$
				"CHARACTER_LENGTH", //$NON-NLS-1$
				"CHARACTER_SET_CATALOG", //$NON-NLS-1$
				"CHARACTER_SET_NAME", //$NON-NLS-1$
				"CHARACTER_SET_SCHEMA", //$NON-NLS-1$
				"CHARACTERISTICS", //$NON-NLS-1$
				"CHARACTERS", //$NON-NLS-1$
				"CHECK", // DB2 //$NON-NLS-1$
				"CHECKED", //$NON-NLS-1$
				"CHECKPOINT", //$NON-NLS-1$
				"CHECKSUM", //$NON-NLS-1$
				"CLASS", //$NON-NLS-1$
				"CLASS_ORIGIN", //$NON-NLS-1$
				"CLOB", //$NON-NLS-1$
				"CLOSE", // DB2 //$NON-NLS-1$
				"CLUSTER", // DB2 //$NON-NLS-1$
				"CLUSTERED", //$NON-NLS-1$
				"COALESCE", //$NON-NLS-1$
				"COBOL", //$NON-NLS-1$
				"COLLATE", //$NON-NLS-1$
				"COLLATION", //$NON-NLS-1$
				"COLLATION_CATALOG", //$NON-NLS-1$
				"COLLATION_NAME", //$NON-NLS-1$
				"COLLATION_SCHEMA", //$NON-NLS-1$
				"COLLECT", //$NON-NLS-1$
				"COLLECTION", // DB2 //$NON-NLS-1$
				"COLLID", // DB2 //$NON-NLS-1$
				"COLUMN", // DB2 //$NON-NLS-1$
				"COLUMN_NAME", //$NON-NLS-1$
				"COLUMNS", //$NON-NLS-1$
				"COMMAND_FUNCTION", //$NON-NLS-1$
				"COMMAND_FUNCTION_CODE", //$NON-NLS-1$
				"COMMENT", // DB2 //$NON-NLS-1$
				"COMMIT", // DB2 //$NON-NLS-1$
				"COMMITTED", //$NON-NLS-1$
				"COMPLETION", //$NON-NLS-1$
				"COMPRESS", //$NON-NLS-1$
				"COMPUTE", //$NON-NLS-1$
				"CONCAT", // DB2 //$NON-NLS-1$
				"CONDITION", // DB2 //$NON-NLS-1$
				"CONDITION_NUMBER", //$NON-NLS-1$
				"CONNECT", // DB2 //$NON-NLS-1$
				"CONNECTION", // DB2 //$NON-NLS-1$
				"CONNECTION_NAME", //$NON-NLS-1$
				"CONSTRAINT", // DB2 //$NON-NLS-1$
				"CONSTRAINT_CATALOG", //$NON-NLS-1$
				"CONSTRAINT_NAME", //$NON-NLS-1$
				"CONSTRAINT_SCHEMA", //$NON-NLS-1$
				"CONSTRAINTS", //$NON-NLS-1$
				"CONSTRUCTOR", //$NON-NLS-1$
				"CONTAINS", // DB2 //$NON-NLS-1$
				"CONTAINSTABLE", //$NON-NLS-1$
				"CONTINUE", // DB2 //$NON-NLS-1$
				"CONVERSION", //$NON-NLS-1$
				"CONVERT", //$NON-NLS-1$
				"COPY", //$NON-NLS-1$
				"CORR", //$NON-NLS-1$
				"CORRESPONDING", //$NON-NLS-1$
				"COUNT", // DB2 //$NON-NLS-1$
				"COUNT_BIG", // DB2 //$NON-NLS-1$
				"COVAR_POP", //$NON-NLS-1$
				"COVAR_SAMP", //$NON-NLS-1$
				"CREATE", // DB2 //$NON-NLS-1$
				"CREATEDB", //$NON-NLS-1$
				"CREATEROLE", //$NON-NLS-1$
				"CREATEUSER", //$NON-NLS-1$
				"CROSS", // DB2 //$NON-NLS-1$
				"CSV", //$NON-NLS-1$
				"CUBE", //$NON-NLS-1$
				"CUME_DIST", //$NON-NLS-1$
				"CURRENT", // DB2 //$NON-NLS-1$
				"CURRENT_DATE", // DB2 //$NON-NLS-1$
				"CURRENT_DEFAULT_TRANSFORM_GROUP", //$NON-NLS-1$
				"CURRENT_LC_CTYPE", // DB2 //$NON-NLS-1$
				"CURRENT_PATH", // DB2 //$NON-NLS-1$
				"CURRENT_ROLE", //$NON-NLS-1$
				"CURRENT_SERVER", // DB2 //$NON-NLS-1$
				"CURRENT_TIME", // DB2 //$NON-NLS-1$
				"CURRENT_TIMESTAMP", // DB2 //$NON-NLS-1$
				"CURRENT_TIMEZONE", // DB2 //$NON-NLS-1$
				"CURRENT_TRANSFORM_GROUP_FOR_TYPE", //$NON-NLS-1$
				"CURRENT_USER", // DB2 //$NON-NLS-1$
				"CURSOR", // DB2 //$NON-NLS-1$
				"CURSOR_NAME", //$NON-NLS-1$
				"CYCLE", // DB2 //$NON-NLS-1$
				"DATA", // DB2 //$NON-NLS-1$
				"DATABASE", // DB2 //$NON-NLS-1$
				"DATABASES", //$NON-NLS-1$
				"DATE", //$NON-NLS-1$
				"DATETIME", //$NON-NLS-1$
				"DATETIME_INTERVAL_CODE", //$NON-NLS-1$
				"DATETIME_INTERVAL_PRECISION", //$NON-NLS-1$
				"DAY", // DB2 //$NON-NLS-1$
				"DAY_HOUR", //$NON-NLS-1$
				"DAY_MICROSECOND", //$NON-NLS-1$
				"DAY_MINUTE", //$NON-NLS-1$
				"DAY_SECOND", //$NON-NLS-1$
				"DAYOFMONTH", //$NON-NLS-1$
				"DAYOFWEEK", //$NON-NLS-1$
				"DAYOFYEAR", //$NON-NLS-1$
				"DAYS", // DB2 //$NON-NLS-1$
				"DB2GENERAL", // DB2 //$NON-NLS-1$
				"DB2GNRL", // DB2 //$NON-NLS-1$
				"DB2SQL", // DB2 //$NON-NLS-1$
				"DBCC", //$NON-NLS-1$
				"DBINFO", // DB2 //$NON-NLS-1$
				"DEALLOCATE", //$NON-NLS-1$
				"DEC", //$NON-NLS-1$
				"DECIMAL", //$NON-NLS-1$
				"DECLARE", // DB2 //$NON-NLS-1$
				"DEFAULT", // DB2 //$NON-NLS-1$
				"DEFAULTS", // DB2 //$NON-NLS-1$
				"DEFERRABLE", //$NON-NLS-1$
				"DEFERRED", //$NON-NLS-1$
				"DEFINED", //$NON-NLS-1$
				"DEFINER", //$NON-NLS-1$
				"DEFINITION", // DB2 //$NON-NLS-1$
				"DEGREE", //$NON-NLS-1$
				"DELAY_KEY_WRITE", //$NON-NLS-1$
				"DELAYED", //$NON-NLS-1$
				"DELETE", // DB2 //$NON-NLS-1$
				"DELIMITER", //$NON-NLS-1$
				"DELIMITERS", //$NON-NLS-1$
				"DENSE_RANK", //$NON-NLS-1$
				"DENY", //$NON-NLS-1$
				"DEPTH", //$NON-NLS-1$
				"DEREF", //$NON-NLS-1$
				"DERIVED", //$NON-NLS-1$
				"DESC", //$NON-NLS-1$
				"DESCRIBE", //$NON-NLS-1$
				"DESCRIPTOR", // DB2 //$NON-NLS-1$
				"DESTROY", //$NON-NLS-1$
				"DESTRUCTOR", //$NON-NLS-1$
				"DETERMINISTIC", // DB2 //$NON-NLS-1$
				"DIAGNOSTICS", //$NON-NLS-1$
				"DICTIONARY", //$NON-NLS-1$
				"DISABLE", //$NON-NLS-1$
				"DISALLOW", // DB2 //$NON-NLS-1$
				"DISCONNECT", // DB2 //$NON-NLS-1$
				"DISK", //$NON-NLS-1$
				"DISPATCH", //$NON-NLS-1$
				"DISTINCT", // DB2 //$NON-NLS-1$
				"DISTINCTROW", //$NON-NLS-1$
				"DISTRIBUTED", //$NON-NLS-1$
				"DIV", //$NON-NLS-1$
				"DO", // DB2 //$NON-NLS-1$
				"DOMAIN", //$NON-NLS-1$
				"DOUBLE", // DB2 //$NON-NLS-1$
				"DROP", // DB2 //$NON-NLS-1$
				"DSNHATTR", // DB2 //$NON-NLS-1$
				"DSSIZE", // DB2 //$NON-NLS-1$
				"DUAL", //$NON-NLS-1$
				"DUMMY", //$NON-NLS-1$
				"DUMP", //$NON-NLS-1$
				"DYNAMIC", // DB2 //$NON-NLS-1$
				"DYNAMIC_FUNCTION", //$NON-NLS-1$
				"DYNAMIC_FUNCTION_CODE", //$NON-NLS-1$
				"EACH", // DB2 //$NON-NLS-1$
				"EDITPROC", // DB2 //$NON-NLS-1$
				"ELEMENT", //$NON-NLS-1$
				"ELSE", // DB2 //$NON-NLS-1$
				"ELSEIF", // DB2 //$NON-NLS-1$
				"ENABLE", //$NON-NLS-1$
				"ENCLOSED", //$NON-NLS-1$
				"ENCODING", // DB2 //$NON-NLS-1$
				"ENCRYPTED", //$NON-NLS-1$
				"END", // DB2 //$NON-NLS-1$
				"END-EXEC", // DB2 //$NON-NLS-1$
				"END-EXEC1", // DB2 //$NON-NLS-1$
				"ENUM", //$NON-NLS-1$
				"EQUALS", //$NON-NLS-1$
				"ERASE", // DB2 //$NON-NLS-1$
				"ERRLVL", //$NON-NLS-1$
				"ESCAPE", // DB2 //$NON-NLS-1$
				"ESCAPED", //$NON-NLS-1$
				"EVERY", //$NON-NLS-1$
				"EXCEPT", // DB2 //$NON-NLS-1$
				"EXCEPTION", // DB2 //$NON-NLS-1$
				"EXCLUDE", //$NON-NLS-1$
				"EXCLUDING", // DB2 //$NON-NLS-1$
				"EXCLUSIVE", //$NON-NLS-1$
				"EXEC", //$NON-NLS-1$
				"EXECUTE", // DB2 //$NON-NLS-1$
				"EXISTING", //$NON-NLS-1$
				"EXISTS", // DB2 //$NON-NLS-1$
				"EXIT", // DB2 //$NON-NLS-1$
				"EXP", //$NON-NLS-1$
				"EXPLAIN", //$NON-NLS-1$
				"EXTERNAL", // DB2 //$NON-NLS-1$
				"EXTRACT", //$NON-NLS-1$
				"FALSE", //$NON-NLS-1$
				"FENCED", // DB2 //$NON-NLS-1$
				"FETCH", // DB2 //$NON-NLS-1$
				"FIELDPROC", // DB2 //$NON-NLS-1$
				"FIELDS", //$NON-NLS-1$
				"FILE", // DB2 //$NON-NLS-1$
				"FILLFACTOR", //$NON-NLS-1$
				"FILTER", //$NON-NLS-1$
				"FINAL", // DB2 //$NON-NLS-1$
				"FIRST", //$NON-NLS-1$
				"FLOAT", //$NON-NLS-1$
				"FLOAT4", //$NON-NLS-1$
				"FLOAT8", //$NON-NLS-1$
				"FLOOR", //$NON-NLS-1$
				"FLUSH", //$NON-NLS-1$
				"FOLLOWING", //$NON-NLS-1$
				"FOR", // DB2 //$NON-NLS-1$
				"FORCE", //$NON-NLS-1$
				"FOREIGN", // DB2 //$NON-NLS-1$
				"FORTRAN", //$NON-NLS-1$
				"FORWARD", //$NON-NLS-1$
				"FOUND", //$NON-NLS-1$
				"FREE", // DB2 //$NON-NLS-1$
				"FREETEXT", //$NON-NLS-1$
				"FREETEXTTABLE", //$NON-NLS-1$
				"FREEZE", //$NON-NLS-1$
				"FROM", // DB2 //$NON-NLS-1$
				"FULL", // DB2 //$NON-NLS-1$
				"FULLTEXT", //$NON-NLS-1$
				"FUNCTION", // DB2 //$NON-NLS-1$
				"FUSION", //$NON-NLS-1$
				"G", //$NON-NLS-1$
				"GENERAL", // DB2 //$NON-NLS-1$
				"GENERATED", // DB2 //$NON-NLS-1$
				"GET", // DB2 //$NON-NLS-1$
				"GLOBAL", // DB2 //$NON-NLS-1$
				"GO", // DB2 //$NON-NLS-1$
				"GOTO", // DB2 //$NON-NLS-1$
				"GRANT", // DB2 //$NON-NLS-1$
				"GRANTED", //$NON-NLS-1$
				"GRANTS", //$NON-NLS-1$
				"GRAPHIC", // DB2 //$NON-NLS-1$
				"GREATEST", //$NON-NLS-1$
				"GROUP", // DB2 //$NON-NLS-1$
				"GROUPING", //$NON-NLS-1$
				"HANDLER", // DB2 //$NON-NLS-1$
				"HAVING", // DB2 //$NON-NLS-1$
				"HEADER", //$NON-NLS-1$
				"HEAP", //$NON-NLS-1$
				"HIERARCHY", //$NON-NLS-1$
				"HIGH_PRIORITY", //$NON-NLS-1$
				"HOLD", // DB2 //$NON-NLS-1$
				"HOLDLOCK", //$NON-NLS-1$
				"HOST", //$NON-NLS-1$
				"HOSTS", //$NON-NLS-1$
				"HOUR", // DB2 //$NON-NLS-1$
				"HOUR_MICROSECOND", //$NON-NLS-1$
				"HOUR_MINUTE", //$NON-NLS-1$
				"HOUR_SECOND", //$NON-NLS-1$
				"HOURS", // DB2 //$NON-NLS-1$
				"IDENTIFIED", //$NON-NLS-1$
				"IDENTITY", // DB2 //$NON-NLS-1$
				"IDENTITY_INSERT", //$NON-NLS-1$
				"IDENTITYCOL", //$NON-NLS-1$
				"IF", // DB2 //$NON-NLS-1$
				"IGNORE", //$NON-NLS-1$
				"ILIKE", //$NON-NLS-1$
				"IMMEDIATE", // DB2 //$NON-NLS-1$
				"IMMUTABLE", //$NON-NLS-1$
				"IMPLEMENTATION", //$NON-NLS-1$
				"IMPLICIT", //$NON-NLS-1$
				"IN", // DB2 //$NON-NLS-1$
				"INCLUDE", //$NON-NLS-1$
				"INCLUDING", // DB2 //$NON-NLS-1$
				"INCREMENT", // DB2 //$NON-NLS-1$
				"INDEX", // DB2 //$NON-NLS-1$
				"INDICATOR", // DB2 //$NON-NLS-1$
				"INFILE", //$NON-NLS-1$
				"INFIX", //$NON-NLS-1$
				"INHERIT", // DB2 //$NON-NLS-1$
				"INHERITS", //$NON-NLS-1$
				"INITIAL", //$NON-NLS-1$
				"INITIALIZE", //$NON-NLS-1$
				"INITIALLY", //$NON-NLS-1$
				"INNER", // DB2 //$NON-NLS-1$
				"INOUT", // DB2 //$NON-NLS-1$
				"INPUT", //$NON-NLS-1$
				"INSENSITIVE", // DB2 //$NON-NLS-1$
				"INSERT", // DB2 //$NON-NLS-1$
				"INSERT_ID", //$NON-NLS-1$
				"INSTANCE", //$NON-NLS-1$
				"INSTANTIABLE", //$NON-NLS-1$
				"INSTEAD", //$NON-NLS-1$
				"INT", //$NON-NLS-1$
				"INT1", //$NON-NLS-1$
				"INT2", //$NON-NLS-1$
				"INT3", //$NON-NLS-1$
				"INT4", //$NON-NLS-1$
				"INT8", //$NON-NLS-1$
				"INTEGER", //$NON-NLS-1$
				"INTEGRITY", // DB2 //$NON-NLS-1$
				"INTERSECT", //$NON-NLS-1$
				"INTERSECTION", //$NON-NLS-1$
				"INTERVAL", //$NON-NLS-1$
				"INTO", // DB2 //$NON-NLS-1$
				"INVOKER", //$NON-NLS-1$
				"IS", // DB2 //$NON-NLS-1$
				"ISAM", //$NON-NLS-1$
				"ISNULL", //$NON-NLS-1$
				"ISOBID", // DB2 //$NON-NLS-1$
				"ISOLATION", // DB2 //$NON-NLS-1$
				"ITERATE", // DB2 //$NON-NLS-1$
				"JAR", // DB2 //$NON-NLS-1$
				"JAVA", // DB2 //$NON-NLS-1$
				"JOIN", // DB2 //$NON-NLS-1$
				"K", //$NON-NLS-1$
				"KEY", // DB2 //$NON-NLS-1$
				"KEY_MEMBER", //$NON-NLS-1$
				"KEY_TYPE", //$NON-NLS-1$
				"KEYS", //$NON-NLS-1$
				"KILL", //$NON-NLS-1$
				"LABEL", // DB2 //$NON-NLS-1$
				"LANCOMPILER", //$NON-NLS-1$
				"LANGUAGE", // DB2 //$NON-NLS-1$
				"LARGE", //$NON-NLS-1$
				"LAST", //$NON-NLS-1$
				"LAST_INSERT_ID", //$NON-NLS-1$
				"LATERAL", //$NON-NLS-1$
				"LC_CTYPE", // DB2 //$NON-NLS-1$
				"LEADING", //$NON-NLS-1$
				"LEAST", //$NON-NLS-1$
				"LEAVE", // DB2 //$NON-NLS-1$
				"LEFT", // DB2 //$NON-NLS-1$
				"LENGTH", //$NON-NLS-1$
				"LESS", //$NON-NLS-1$
				"LEVEL", //$NON-NLS-1$
				"LIKE", // DB2 //$NON-NLS-1$
				"LIMIT", //$NON-NLS-1$
				"LINENO", //$NON-NLS-1$
				"LINES", //$NON-NLS-1$
				"LINKTYPE", // DB2 //$NON-NLS-1$
				"LISTEN", //$NON-NLS-1$
				"LN", //$NON-NLS-1$
				"LOAD", //$NON-NLS-1$
				"LOCAL", // DB2 //$NON-NLS-1$
				"LOCALE", // DB2 //$NON-NLS-1$
				"LOCALTIME", //$NON-NLS-1$
				"LOCALTIMESTAMP", //$NON-NLS-1$
				"LOCATION", //$NON-NLS-1$
				"LOCATOR", // DB2 //$NON-NLS-1$
				"LOCATORS", // DB2 //$NON-NLS-1$
				"LOCK", // DB2 //$NON-NLS-1$
				"LOCKMAX", // DB2 //$NON-NLS-1$
				"LOCKSIZE", // DB2 //$NON-NLS-1$
				"LOGIN", //$NON-NLS-1$
				"LOGS", //$NON-NLS-1$
				"LONG", // DB2 //$NON-NLS-1$
				"LONGBLOB", //$NON-NLS-1$
				"LONGTEXT", //$NON-NLS-1$
				"LOOP", // DB2 //$NON-NLS-1$
				"LOW_PRIORITY", //$NON-NLS-1$
				"LOWER", //$NON-NLS-1$
				"M", //$NON-NLS-1$
				"MAP", //$NON-NLS-1$
				"MATCH", //$NON-NLS-1$
				"MATCHED", //$NON-NLS-1$
				"MAX", //$NON-NLS-1$
				"MAX_ROWS", //$NON-NLS-1$
				"MAXEXTENTS", //$NON-NLS-1$
				"MAXVALUE", // DB2 //$NON-NLS-1$
				"MEDIUMBLOB", //$NON-NLS-1$
				"MEDIUMINT", //$NON-NLS-1$
				"MEDIUMTEXT", //$NON-NLS-1$
				"MEMBER", //$NON-NLS-1$
				"MERGE", //$NON-NLS-1$
				"MESSAGE_LENGTH", //$NON-NLS-1$
				"MESSAGE_OCTET_LENGTH", //$NON-NLS-1$
				"MESSAGE_TEXT", //$NON-NLS-1$
				"METHOD", //$NON-NLS-1$
				"MICROSECOND", // DB2 //$NON-NLS-1$
				"MICROSECONDS", // DB2 //$NON-NLS-1$
				"MIDDLEINT", //$NON-NLS-1$
				"MIN", //$NON-NLS-1$
				"MIN_ROWS", //$NON-NLS-1$
				"MINUS", //$NON-NLS-1$
				"MINUTE", // DB2 //$NON-NLS-1$
				"MINUTE_MICROSECOND", //$NON-NLS-1$
				"MINUTE_SECOND", //$NON-NLS-1$
				"MINUTES", // DB2 //$NON-NLS-1$
				"MINVALUE", // DB2 //$NON-NLS-1$
				"MLSLABEL", //$NON-NLS-1$
				"MOD", //$NON-NLS-1$
				"MODE", // DB2 //$NON-NLS-1$
				"MODIFIES", // DB2 //$NON-NLS-1$
				"MODIFY", //$NON-NLS-1$
				"MODULE", //$NON-NLS-1$
				"MONTH", // DB2 //$NON-NLS-1$
				"MONTHNAME", //$NON-NLS-1$
				"MONTHS", // DB2 //$NON-NLS-1$
				"MORE", //$NON-NLS-1$
				"MOVE", //$NON-NLS-1$
				"MULTISET", //$NON-NLS-1$
				"MUMPS", //$NON-NLS-1$
				"MYISAM", //$NON-NLS-1$
				"NAME", //$NON-NLS-1$
				"NAMES", //$NON-NLS-1$
				"NATIONAL", //$NON-NLS-1$
				"NATURAL", //$NON-NLS-1$
				"NCHAR", //$NON-NLS-1$
				"NCLOB", //$NON-NLS-1$
				"NESTING", //$NON-NLS-1$
				"NEW", // DB2 //$NON-NLS-1$
				"NEW_TABLE", // DB2 //$NON-NLS-1$
				"NEXT", //$NON-NLS-1$
				"NO", // DB2 //$NON-NLS-1$
				"NO_WRITE_TO_BINLOG", //$NON-NLS-1$
				"NOAUDIT", //$NON-NLS-1$
				"NOCACHE", // DB2 //$NON-NLS-1$
				"NOCHECK", //$NON-NLS-1$
				"NOCOMPRESS", //$NON-NLS-1$
				"NOCREATEDB", //$NON-NLS-1$
				"NOCREATEROLE", //$NON-NLS-1$
				"NOCREATEUSER", //$NON-NLS-1$
				"NOCYCLE", // DB2 //$NON-NLS-1$
				"NODENAME", // DB2 //$NON-NLS-1$
				"NODENUMBER", // DB2 //$NON-NLS-1$
				"NOINHERIT", //$NON-NLS-1$
				"NOLOGIN", //$NON-NLS-1$
				"NOMAXVALUE", // DB2 //$NON-NLS-1$
				"NOMINVALUE", // DB2 //$NON-NLS-1$
				"NONCLUSTERED", //$NON-NLS-1$
				"NONE", //$NON-NLS-1$
				"NOORDER", // DB2 //$NON-NLS-1$
				"NORMALIZE", //$NON-NLS-1$
				"NORMALIZED", //$NON-NLS-1$
				"NOSUPERUSER", //$NON-NLS-1$
				"NOT", // DB2 //$NON-NLS-1$
				"NOTHING", //$NON-NLS-1$
				"NOTIFY", //$NON-NLS-1$
				"NOTNULL", //$NON-NLS-1$
				"NOWAIT", //$NON-NLS-1$
				"NULL", // DB2 //$NON-NLS-1$
				"NULLABLE", //$NON-NLS-1$
				"NULLIF", //$NON-NLS-1$
				"NULLS", // DB2 //$NON-NLS-1$
				"NUMBER", //$NON-NLS-1$
				"NUMERIC", //$NON-NLS-1$
				"NUMPARTS", // DB2 //$NON-NLS-1$
				"OBID", // DB2 //$NON-NLS-1$
				"OBJECT", //$NON-NLS-1$
				"OCTET_LENGTH", //$NON-NLS-1$
				"OCTETS", //$NON-NLS-1$
				"OF", // DB2 //$NON-NLS-1$
				"OFF", //$NON-NLS-1$
				"OFFLINE", //$NON-NLS-1$
				"OFFSET", //$NON-NLS-1$
				"OFFSETS", //$NON-NLS-1$
				"OIDS", //$NON-NLS-1$
				"OLD", // DB2 //$NON-NLS-1$
				"OLD_TABLE", // DB2 //$NON-NLS-1$
				"ON", // DB2 //$NON-NLS-1$
				"ONLINE", //$NON-NLS-1$
				"ONLY", //$NON-NLS-1$
				"OPEN", // DB2 //$NON-NLS-1$
				"OPENDATASOURCE", //$NON-NLS-1$
				"OPENQUERY", //$NON-NLS-1$
				"OPENROWSET", //$NON-NLS-1$
				"OPENXML", //$NON-NLS-1$
				"OPERATION", //$NON-NLS-1$
				"OPERATOR", //$NON-NLS-1$
				"OPTIMIZATION", // DB2 //$NON-NLS-1$
				"OPTIMIZE", // DB2 //$NON-NLS-1$
				"OPTION", // DB2 //$NON-NLS-1$
				"OPTIONALLY", //$NON-NLS-1$
				"OPTIONS", //$NON-NLS-1$
				"OR", // DB2 //$NON-NLS-1$
				"ORDER", // DB2 //$NON-NLS-1$
				"ORDERING", //$NON-NLS-1$
				"ORDINALITY", //$NON-NLS-1$
				"OTHERS", //$NON-NLS-1$
				"OUT", // DB2 //$NON-NLS-1$
				"OUTER", // DB2 //$NON-NLS-1$
				"OUTFILE", //$NON-NLS-1$
				"OUTPUT", //$NON-NLS-1$
				"OVER", //$NON-NLS-1$
				"OVERLAPS", //$NON-NLS-1$
				"OVERLAY", //$NON-NLS-1$
				"OVERRIDING", // DB2 //$NON-NLS-1$
				"OWNER", //$NON-NLS-1$
				"PACK_KEYS", //$NON-NLS-1$
				"PACKAGE", // DB2 //$NON-NLS-1$
				"PAD", //$NON-NLS-1$
				"PARAMETER", // DB2 //$NON-NLS-1$
				"PARAMETER_MODE", //$NON-NLS-1$
				"PARAMETER_NAME", //$NON-NLS-1$
				"PARAMETER_ORDINAL_POSITION", //$NON-NLS-1$
				"PARAMETER_SPECIFIC_CATALOG", //$NON-NLS-1$
				"PARAMETER_SPECIFIC_NAME", //$NON-NLS-1$
				"PARAMETER_SPECIFIC_SCHEMA", //$NON-NLS-1$
				"PARAMETERS", //$NON-NLS-1$
				"PART", // DB2 //$NON-NLS-1$
				"PARTIAL", //$NON-NLS-1$
				"PARTITION", // DB2 //$NON-NLS-1$
				"PASCAL", //$NON-NLS-1$
				"PASSWORD", //$NON-NLS-1$
				"PATH", // DB2 //$NON-NLS-1$
				"PCTFREE", //$NON-NLS-1$
				"PERCENT", //$NON-NLS-1$
				"PERCENT_RANK", //$NON-NLS-1$
				"PERCENTILE_CONT", //$NON-NLS-1$
				"PERCENTILE_DISC", //$NON-NLS-1$
				"PIECESIZE", // DB2 //$NON-NLS-1$
				"PLACING", //$NON-NLS-1$
				"PLAN", // DB2 //$NON-NLS-1$
				"PLI", //$NON-NLS-1$
				"POSITION", // DB2 //$NON-NLS-1$
				"POSTFIX", //$NON-NLS-1$
				"POWER", //$NON-NLS-1$
				"PRECEDING", //$NON-NLS-1$
				"PRECISION", // DB2 //$NON-NLS-1$
				"PREFIX", //$NON-NLS-1$
				"PREORDER", //$NON-NLS-1$
				"PREPARE", // DB2 //$NON-NLS-1$
				"PREPARED", //$NON-NLS-1$
				"PRESERVE", //$NON-NLS-1$
				"PRIMARY", // DB2 //$NON-NLS-1$
				"PRINT", //$NON-NLS-1$
				"PRIOR", //$NON-NLS-1$
				"PRIQTY", // DB2 //$NON-NLS-1$
				"PRIVILEGES", // DB2 //$NON-NLS-1$
				"PROC", //$NON-NLS-1$
				"PROCEDURAL", //$NON-NLS-1$
				"PROCEDURE", // DB2 //$NON-NLS-1$
				"PROCESS", //$NON-NLS-1$
				"PROCESSLIST", //$NON-NLS-1$
				"PROGRAM", // DB2 //$NON-NLS-1$
				"PSID", // DB2 //$NON-NLS-1$
				"PUBLIC", //$NON-NLS-1$
				"PURGE", //$NON-NLS-1$
				"QUERYNO", // DB2 //$NON-NLS-1$
				"QUOTE", //$NON-NLS-1$
				"RAID0", //$NON-NLS-1$
				"RAISERROR", //$NON-NLS-1$
				"RANGE", //$NON-NLS-1$
				"RANK", //$NON-NLS-1$
				"RAW", //$NON-NLS-1$
				"READ", // DB2 //$NON-NLS-1$
				"READS", // DB2 //$NON-NLS-1$
				"READTEXT", //$NON-NLS-1$
				"REAL", //$NON-NLS-1$
				"RECHECK", //$NON-NLS-1$
				"RECONFIGURE", //$NON-NLS-1$
				"RECOVERY", // DB2 //$NON-NLS-1$
				"RECURSIVE", //$NON-NLS-1$
				"REF", //$NON-NLS-1$
				"REFERENCES", // DB2 //$NON-NLS-1$
				"REFERENCING", // DB2 //$NON-NLS-1$
				"REGEXP", //$NON-NLS-1$
				"REGR_AVGX", //$NON-NLS-1$
				"REGR_AVGY", //$NON-NLS-1$
				"REGR_COUNT", //$NON-NLS-1$
				"REGR_INTERCEPT", //$NON-NLS-1$
				"REGR_R2", //$NON-NLS-1$
				"REGR_SLOPE", //$NON-NLS-1$
				"REGR_SXX", //$NON-NLS-1$
				"REGR_SXY", //$NON-NLS-1$
				"REGR_SYY", //$NON-NLS-1$
				"REINDEX", //$NON-NLS-1$
				"RELATIVE", //$NON-NLS-1$
				"RELEASE", // DB2 //$NON-NLS-1$
				"RELOAD", //$NON-NLS-1$
				"RENAME", // DB2 //$NON-NLS-1$
				"REPEAT", // DB2 //$NON-NLS-1$
				"REPEATABLE", //$NON-NLS-1$
				"REPLACE", //$NON-NLS-1$
				"REPLICATION", //$NON-NLS-1$
				"REQUIRE", //$NON-NLS-1$
				"RESET", // DB2 //$NON-NLS-1$
				"RESIGNAL", // DB2 //$NON-NLS-1$
				"RESOURCE", //$NON-NLS-1$
				"RESTART", // DB2 //$NON-NLS-1$
				"RESTORE", //$NON-NLS-1$
				"RESTRICT", // DB2 //$NON-NLS-1$
				"RESULT", // DB2 //$NON-NLS-1$
				"RESULT_SET_LOCATOR", // DB2 //$NON-NLS-1$
				"RETURN", // DB2 //$NON-NLS-1$
				"RETURNED_CARDINALITY", //$NON-NLS-1$
				"RETURNED_LENGTH", //$NON-NLS-1$
				"RETURNED_OCTET_LENGTH", //$NON-NLS-1$
				"RETURNED_SQLSTATE", //$NON-NLS-1$
				"RETURNS", // DB2 //$NON-NLS-1$
				"REVOKE", // DB2 //$NON-NLS-1$
				"RIGHT", // DB2 //$NON-NLS-1$
				"RLIKE", //$NON-NLS-1$
				"ROLE", //$NON-NLS-1$
				"ROLLBACK", // DB2 //$NON-NLS-1$
				"ROLLUP", //$NON-NLS-1$
				"ROUTINE", // DB2 //$NON-NLS-1$
				"ROUTINE_CATALOG", //$NON-NLS-1$
				"ROUTINE_NAME", //$NON-NLS-1$
				"ROUTINE_SCHEMA", //$NON-NLS-1$
				"ROW", // DB2 //$NON-NLS-1$
				"ROW_COUNT", //$NON-NLS-1$
				"ROW_NUMBER", //$NON-NLS-1$
				"ROWCOUNT", //$NON-NLS-1$
				"ROWGUIDCOL", //$NON-NLS-1$
				"ROWID", //$NON-NLS-1$
				"ROWNUM", //$NON-NLS-1$
				"ROWS", // DB2 //$NON-NLS-1$
				"RRN", // DB2 //$NON-NLS-1$
				"RULE", //$NON-NLS-1$
				"RUN", // DB2 //$NON-NLS-1$
				"SAVE", //$NON-NLS-1$
				"SAVEPOINT", // DB2 //$NON-NLS-1$
				"SCALE", //$NON-NLS-1$
				"SCHEMA", // DB2 //$NON-NLS-1$
				"SCHEMA_NAME", //$NON-NLS-1$
				"SCHEMAS", //$NON-NLS-1$
				"SCOPE", //$NON-NLS-1$
				"SCOPE_CATALOG", //$NON-NLS-1$
				"SCOPE_NAME", //$NON-NLS-1$
				"SCOPE_SCHEMA", //$NON-NLS-1$
				"SCRATCHPAD", // DB2 //$NON-NLS-1$
				"SCROLL", //$NON-NLS-1$
				"SEARCH", //$NON-NLS-1$
				"SECOND", // DB2 //$NON-NLS-1$
				"SECOND_MICROSECOND", //$NON-NLS-1$
				"SECONDS", // DB2 //$NON-NLS-1$
				"SECQTY", // DB2 //$NON-NLS-1$
				"SECTION", //$NON-NLS-1$
				"SECURITY", // DB2 //$NON-NLS-1$
				"SELECT", // DB2 //$NON-NLS-1$
				"SELF", //$NON-NLS-1$
				"SENSITIVE", // DB2 //$NON-NLS-1$
				"SEPARATOR", //$NON-NLS-1$
				"SEQUENCE", //$NON-NLS-1$
				"SERIALIZABLE", //$NON-NLS-1$
				"SERVER_NAME", //$NON-NLS-1$
				"SESSION", //$NON-NLS-1$
				"SESSION_USER", //$NON-NLS-1$
				"SET", // DB2 //$NON-NLS-1$
				"SETOF", //$NON-NLS-1$
				"SETS", //$NON-NLS-1$
				"SETUSER", //$NON-NLS-1$
				"SHARE", //$NON-NLS-1$
				"SHOW", //$NON-NLS-1$
				"SHUTDOWN", //$NON-NLS-1$
				"SIGNAL", // DB2 //$NON-NLS-1$
				"SIMILAR", //$NON-NLS-1$
				"SIMPLE", // DB2 //$NON-NLS-1$
				"SIZE", //$NON-NLS-1$
				"SMALLINT", //$NON-NLS-1$
				"SOME", // DB2 //$NON-NLS-1$
				"SONAME", //$NON-NLS-1$
				"SOURCE", // DB2 //$NON-NLS-1$
				"SPACE", //$NON-NLS-1$
				"SPATIAL", //$NON-NLS-1$
				"SPECIFIC", // DB2 //$NON-NLS-1$
				"SPECIFIC_NAME", //$NON-NLS-1$
				"SPECIFICTYPE", //$NON-NLS-1$
				"SQL", // DB2 //$NON-NLS-1$
				"SQL_BIG_RESULT", //$NON-NLS-1$
				"SQL_BIG_SELECTS", //$NON-NLS-1$
				"SQL_BIG_TABLES", //$NON-NLS-1$
				"SQL_CALC_FOUND_ROWS", //$NON-NLS-1$
				"SQL_LOG_OFF", //$NON-NLS-1$
				"SQL_LOG_UPDATE", //$NON-NLS-1$
				"SQL_LOW_PRIORITY_UPDATES", //$NON-NLS-1$
				"SQL_SELECT_LIMIT", //$NON-NLS-1$
				"SQL_SMALL_RESULT", //$NON-NLS-1$
				"SQL_WARNINGS", //$NON-NLS-1$
				"SQLCA", //$NON-NLS-1$
				"SQLCODE", //$NON-NLS-1$
				"SQLERROR", //$NON-NLS-1$
				"SQLEXCEPTION", //$NON-NLS-1$
				"SQLID", // DB2 //$NON-NLS-1$
				"SQLSTATE", //$NON-NLS-1$
				"SQLWARNING", //$NON-NLS-1$
				"SQRT", //$NON-NLS-1$
				"SSL", //$NON-NLS-1$
				"STABLE", //$NON-NLS-1$
				"STANDARD", // DB2 //$NON-NLS-1$
				"START", // DB2 //$NON-NLS-1$
				"STARTING", //$NON-NLS-1$
				"STATE", //$NON-NLS-1$
				"STATEMENT", //$NON-NLS-1$
				"STATIC", // DB2 //$NON-NLS-1$
				"STATISTICS", //$NON-NLS-1$
				"STATUS", //$NON-NLS-1$
				"STAY", // DB2 //$NON-NLS-1$
				"STDDEV_POP", //$NON-NLS-1$
				"STDDEV_SAMP", //$NON-NLS-1$
				"STDIN", //$NON-NLS-1$
				"STDOUT", //$NON-NLS-1$
				"STOGROUP", // DB2 //$NON-NLS-1$
				"STORAGE", //$NON-NLS-1$
				"STORES", // DB2 //$NON-NLS-1$
				"STRAIGHT_JOIN", //$NON-NLS-1$
				"STRICT", //$NON-NLS-1$
				"STRING", //$NON-NLS-1$
				"STRUCTURE", //$NON-NLS-1$
				"STYLE", // DB2 //$NON-NLS-1$
				"SUBCLASS_ORIGIN", //$NON-NLS-1$
				"SUBLIST", //$NON-NLS-1$
				"SUBMULTISET", //$NON-NLS-1$
				"SUBPAGES", // DB2 //$NON-NLS-1$
				"SUBSTRING", // DB2 //$NON-NLS-1$
				"SUCCESSFUL", //$NON-NLS-1$
				"SUM", //$NON-NLS-1$
				"SUPERUSER", //$NON-NLS-1$
				"SYMMETRIC", //$NON-NLS-1$
				"SYNONYM", // DB2 //$NON-NLS-1$
				"SYSDATE", //$NON-NLS-1$
				"SYSFUN", // DB2 //$NON-NLS-1$
				"SYSIBM", // DB2 //$NON-NLS-1$
				"SYSID", //$NON-NLS-1$
				"SYSPROC", // DB2 //$NON-NLS-1$
				"SYSTEM", // DB2 //$NON-NLS-1$
				"SYSTEM_USER", //$NON-NLS-1$
				"TABLE", // DB2 //$NON-NLS-1$
				"TABLE_NAME", //$NON-NLS-1$
				"TABLES", //$NON-NLS-1$
				"TABLESAMPLE", //$NON-NLS-1$
				"TABLESPACE", // DB2 //$NON-NLS-1$
				"TEMP", //$NON-NLS-1$
				"TEMPLATE", //$NON-NLS-1$
				"TEMPORARY", //$NON-NLS-1$
				"TERMINATE", //$NON-NLS-1$
				"TERMINATED", //$NON-NLS-1$
				"TEXT", //$NON-NLS-1$
				"TEXTSIZE", //$NON-NLS-1$
				"THAN", //$NON-NLS-1$
				"THEN", // DB2 //$NON-NLS-1$
				"TIES", //$NON-NLS-1$
				"TIME", //$NON-NLS-1$
				"TIMESTAMP", //$NON-NLS-1$
				"TIMEZONE_HOUR", //$NON-NLS-1$
				"TIMEZONE_MINUTE", //$NON-NLS-1$
				"TINYBLOB", //$NON-NLS-1$
				"TINYINT", //$NON-NLS-1$
				"TINYTEXT", //$NON-NLS-1$
				"TO", // DB2 //$NON-NLS-1$
				"TOAST", //$NON-NLS-1$
				"TOP", //$NON-NLS-1$
				"TOP_LEVEL_COUNT", //$NON-NLS-1$
				"TRAILING", //$NON-NLS-1$
				"TRAN", //$NON-NLS-1$
				"TRANSACTION", // DB2 //$NON-NLS-1$
				"TRANSACTION_ACTIVE", //$NON-NLS-1$
				"TRANSACTIONS_COMMITTED", //$NON-NLS-1$
				"TRANSACTIONS_ROLLED_BACK", //$NON-NLS-1$
				"TRANSFORM", //$NON-NLS-1$
				"TRANSFORMS", //$NON-NLS-1$
				"TRANSLATE", //$NON-NLS-1$
				"TRANSLATION", //$NON-NLS-1$
				"TREAT", //$NON-NLS-1$
				"TRIGGER", // DB2 //$NON-NLS-1$
				"TRIGGER_CATALOG", //$NON-NLS-1$
				"TRIGGER_NAME", //$NON-NLS-1$
				"TRIGGER_SCHEMA", //$NON-NLS-1$
				"TRIM", // DB2 //$NON-NLS-1$
				"TRUE", //$NON-NLS-1$
				"TRUNCATE", //$NON-NLS-1$
				"TRUSTED", //$NON-NLS-1$
				"TSEQUAL", //$NON-NLS-1$
				"TYPE", // DB2 //$NON-NLS-1$
				"UESCAPE", //$NON-NLS-1$
				"UID", //$NON-NLS-1$
				"UNBOUNDED", //$NON-NLS-1$
				"UNCOMMITTED", //$NON-NLS-1$
				"UNDER", //$NON-NLS-1$
				"UNDO", // DB2 //$NON-NLS-1$
				"UNENCRYPTED", //$NON-NLS-1$
				"UNION", // DB2 //$NON-NLS-1$
				"UNIQUE", // DB2 //$NON-NLS-1$
				"UNKNOWN", //$NON-NLS-1$
				"UNLISTEN", //$NON-NLS-1$
				"UNLOCK", //$NON-NLS-1$
				"UNNAMED", //$NON-NLS-1$
				"UNNEST", //$NON-NLS-1$
				"UNSIGNED", //$NON-NLS-1$
				"UNTIL", // DB2 //$NON-NLS-1$
				"UPDATE", // DB2 //$NON-NLS-1$
				"UPDATETEXT", //$NON-NLS-1$
				"UPPER", //$NON-NLS-1$
				"USAGE", // DB2 //$NON-NLS-1$
				"USE", //$NON-NLS-1$
				"USER", // DB2 //$NON-NLS-1$
				"USER_DEFINED_TYPE_CATALOG", //$NON-NLS-1$
				"USER_DEFINED_TYPE_CODE", //$NON-NLS-1$
				"USER_DEFINED_TYPE_NAME", //$NON-NLS-1$
				"USER_DEFINED_TYPE_SCHEMA", //$NON-NLS-1$
				"USING", // DB2 //$NON-NLS-1$
				"UTC_DATE", //$NON-NLS-1$
				"UTC_TIME", //$NON-NLS-1$
				"UTC_TIMESTAMP", //$NON-NLS-1$
				"VACUUM", //$NON-NLS-1$
				"VALID", //$NON-NLS-1$
				"VALIDATE", //$NON-NLS-1$
				"VALIDATOR", //$NON-NLS-1$
				"VALIDPROC", // DB2 //$NON-NLS-1$
				"VALUE", //$NON-NLS-1$
				"VALUES", // DB2 //$NON-NLS-1$
				"VAR_POP", //$NON-NLS-1$
				"VAR_SAMP", //$NON-NLS-1$
				"VARBINARY", //$NON-NLS-1$
				"VARCHAR", //$NON-NLS-1$
				"VARCHAR2", //$NON-NLS-1$
				"VARCHARACTER", //$NON-NLS-1$
				"VARIABLE", // DB2 //$NON-NLS-1$
				"VARIABLES", //$NON-NLS-1$
				"VARIANT", // DB2 //$NON-NLS-1$
				"VARYING", //$NON-NLS-1$
				"VCAT", // DB2 //$NON-NLS-1$
				"VERBOSE", //$NON-NLS-1$
				"VIEW", // DB2 //$NON-NLS-1$
				"VOLATILE", //$NON-NLS-1$
				"VOLUMES", // DB2 //$NON-NLS-1$
				"WAITFOR", //$NON-NLS-1$
				"WHEN", // DB2 //$NON-NLS-1$
				"WHENEVER", //$NON-NLS-1$
				"WHERE", // DB2 //$NON-NLS-1$
				"WHILE", // DB2 //$NON-NLS-1$
				"WIDTH_BUCKET", //$NON-NLS-1$
				"WINDOW", //$NON-NLS-1$
				"WITH", // DB2 //$NON-NLS-1$
				"WITHIN", //$NON-NLS-1$
				"WITHOUT", //$NON-NLS-1$
				"WLM", // DB2 //$NON-NLS-1$
				"WORK", //$NON-NLS-1$
				"WRITE", // DB2 //$NON-NLS-1$
				"WRITETEXT", //$NON-NLS-1$
				"X509", //$NON-NLS-1$
				"XOR", //$NON-NLS-1$
				"YEAR", // DB2 //$NON-NLS-1$
				"YEAR_MONTH", //$NON-NLS-1$
				"YEARS", // DB2 //$NON-NLS-1$
				"ZEROFILL", //$NON-NLS-1$
				"ZONE" //$NON-NLS-1$
		};

		RESERVED_WORDS = new HashSet<String>(words.length);

		for (String word : words) {
			RESERVED_WORDS.add(word);
		}
	}

	/**
	 * <p>
	 * 数据库字段转义
	 * </p>
	 *
	 * @param dbType
	 *            数据库类型
	 * @param column
	 *            数据库字段
	 * @return
	 */
	public static String convert(DBType dbType, String column) {
		if (dbType == DBType.MYSQL && containsWord(column)) {
			return String.format("`%s`", column);
		}
		return column;
	}

	public static boolean containsWord(String word) {
		boolean rc = false;
		if (null != word) {
			rc = RESERVED_WORDS.contains(word.toUpperCase());
		}
		return rc;
	}

	/**
	 * Utility class - no instances allowed
	 */
	private SqlReservedWords() {

	}
}
