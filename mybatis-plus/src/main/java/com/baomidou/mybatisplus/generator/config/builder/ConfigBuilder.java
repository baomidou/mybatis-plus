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
package com.baomidou.mybatisplus.generator.config.builder;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.config.rules.QuerySQL;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * 配置汇总 传递给文件生成工具
 *
 * @author YangHu, tangguo, hubin
 * @since 2016-08-30
 */
public class ConfigBuilder {

	/**
	 * SQL连接
	 */
	private Connection connection;
	/**
	 * SQL语句类型
	 */
	private QuerySQL querySQL;
	private String superEntityClass;
	private String superMapperClass;
	/**
	 * service超类定义
	 */
	private String superServiceClass;
	private String superServiceImplClass;
	private String superControllerClass;
	/**
	 * 数据库表信息
	 */
	private List<TableInfo> tableInfoList;

	/**
	 * 包配置详情
	 */
	private Map<String, String> packageInfo;
	/**
	 * 路径配置信息
	 */
	private Map<String, String> pathInfo;

	/**
	 * 模板路径配置信息
	 */
	private TemplateConfig template;

	/**
	 * 策略配置
	 */
	private StrategyConfig strategyConfig;

	/**
	 * 全局配置信息
	 */
	private GlobalConfig globalConfig;

	/**
	 * 在构造器中处理配置
	 *
	 * @param packageConfig
	 *            包配置
	 * @param dataSourceConfig
	 *            数据源配置
	 * @param strategyConfig
	 *            表配置
	 * @param template
	 *            模板配置
	 * @param globalConfig
	 *            全局配置
	 */
	public ConfigBuilder(PackageConfig packageConfig, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig,
			TemplateConfig template, GlobalConfig globalConfig) {
		// 全局配置
		if (null == globalConfig) {
			this.globalConfig = new GlobalConfig();
		} else {
			this.globalConfig = globalConfig;
		}
		// 包配置
		if (null == packageConfig) {
			handlerPackage(this.globalConfig.getOutputDir(), new PackageConfig());
		} else {
			handlerPackage(this.globalConfig.getOutputDir(), packageConfig);
		}
		handlerDataSource(dataSourceConfig);
		// 策略配置
		if (null == strategyConfig) {
			this.strategyConfig = new StrategyConfig();
		} else {
			this.strategyConfig = strategyConfig;
		}
		handlerStrategy(this.strategyConfig);
		// 模板配置
		if (null == template) {
			this.template = new TemplateConfig();
		} else {
			this.template = template;
		}
	}

	// ************************ 曝露方法 BEGIN*****************************

	/**
	 * 所有包配置信息
	 *
	 * @return 包配置
	 */
	public Map<String, String> getPackageInfo() {
		return packageInfo;
	}

	/**
	 * 所有路径配置
	 *
	 * @return 路径配置
	 */
	public Map<String, String> getPathInfo() {
		return pathInfo;
	}

	public String getSuperEntityClass() {
		return superEntityClass;
	}

	public String getSuperMapperClass() {
		return superMapperClass;
	}

	/**
	 * 获取超类定义
	 *
	 * @return 完整超类名称
	 */
	public String getSuperServiceClass() {
		return superServiceClass;
	}

	public String getSuperServiceImplClass() {
		return superServiceImplClass;
	}

	public String getSuperControllerClass() {
		return superControllerClass;
	}

	/**
	 * 表信息
	 *
	 * @return 所有表信息
	 */
	public List<TableInfo> getTableInfoList() {
		return tableInfoList;
	}

	/**
	 * 模板路径配置信息
	 *
	 * @return 所以模板路径配置信息
	 */
	public TemplateConfig getTemplate() {
		return template == null ? new TemplateConfig() : template;
	}

	// ****************************** 曝露方法 END**********************************

	/**
	 * 处理包配置
	 *
	 * @param config
	 *            PackageConfig
	 */
	private void handlerPackage(String outputDir, PackageConfig config) {
		packageInfo = new HashMap<String, String>();
		packageInfo.put(ConstVal.MODULENAME, config.getModuleName());
		packageInfo.put(ConstVal.ENTITY, joinPackage(config.getParent(), config.getEntity()));
		packageInfo.put(ConstVal.MAPPER, joinPackage(config.getParent(), config.getMapper()));
		packageInfo.put(ConstVal.XML, joinPackage(config.getParent(), config.getXml()));
		packageInfo.put(ConstVal.SERIVCE, joinPackage(config.getParent(), config.getService()));
		packageInfo.put(ConstVal.SERVICEIMPL, joinPackage(config.getParent(), config.getServiceImpl()));
		packageInfo.put(ConstVal.CONTROLLER, joinPackage(config.getParent(), config.getController()));

		pathInfo = new HashMap<String, String>();
		pathInfo.put(ConstVal.ENTITY_PATH, joinPath(outputDir, packageInfo.get(ConstVal.ENTITY)));
		pathInfo.put(ConstVal.MAPPER_PATH, joinPath(outputDir, packageInfo.get(ConstVal.MAPPER)));
		pathInfo.put(ConstVal.XML_PATH, joinPath(outputDir, packageInfo.get(ConstVal.XML)));
		pathInfo.put(ConstVal.SERIVCE_PATH, joinPath(outputDir, packageInfo.get(ConstVal.SERIVCE)));
		pathInfo.put(ConstVal.SERVICEIMPL_PATH, joinPath(outputDir, packageInfo.get(ConstVal.SERVICEIMPL)));
		pathInfo.put(ConstVal.CONTROLLER_PATH, joinPath(outputDir, packageInfo.get(ConstVal.CONTROLLER)));
	}

	/**
	 * 处理数据源配置
	 *
	 * @param config
	 *            DataSourceConfig
	 */
	private void handlerDataSource(DataSourceConfig config) {
		connection = config.getConn();
		querySQL = getQuerySQL(config.getDbType());
	}

	/**
	 * 处理数据库表 加载数据库表、列、注释相关数据集
	 *
	 * @param config
	 *            StrategyConfig
	 */
	private void handlerStrategy(StrategyConfig config) {
		processTypes(config);
		tableInfoList = getTablesInfo(config);
	}

	/**
	 * 处理superClassName,IdClassType,IdStrategy配置
	 *
	 * @param config
	 *            策略配置
	 */
	private void processTypes(StrategyConfig config) {
		if (StringUtils.isEmpty(config.getSuperServiceClass())) {
			superServiceClass = ConstVal.SUPERD_SERVICE_CLASS;
		} else {
			superServiceClass = config.getSuperServiceClass();
		}
		if (StringUtils.isEmpty(config.getSuperServiceImplClass())) {
			superServiceImplClass = ConstVal.SUPERD_SERVICEIMPL_CLASS;
		} else {
			superServiceImplClass = config.getSuperServiceImplClass();
		}
		if (StringUtils.isEmpty(config.getSuperMapperClass())) {
			superMapperClass = ConstVal.SUPERD_MAPPER_CLASS;
		} else {
			superMapperClass = config.getSuperMapperClass();
		}
		superEntityClass = config.getSuperEntityClass();
		superControllerClass = config.getSuperControllerClass();
	}

	/**
	 * 处理表对应的类名称
	 *
	 * @param tableList
	 *            表名称
	 * @param strategy
	 *            命名策略
	 * @param tablePrefix
	 * @return 补充完整信息后的表
	 */
	private List<TableInfo> processTable(List<TableInfo> tableList, NamingStrategy strategy, String tablePrefix) {
		for (TableInfo tableInfo : tableList) {
			tableInfo.setEntityName(NamingStrategy.capitalFirst(processName(tableInfo.getName(), strategy, tablePrefix)));
			if (StringUtils.isNotEmpty(globalConfig.getMapperName())) {
				tableInfo.setMapperName(String.format(globalConfig.getMapperName(), tableInfo.getEntityName()));
			} else {
				tableInfo.setMapperName(tableInfo.getEntityName() + ConstVal.MAPPER);
			}
			if (StringUtils.isNotEmpty(globalConfig.getXmlName())) {
				tableInfo.setXmlName(String.format(globalConfig.getXmlName(), tableInfo.getEntityName()));
			} else {
				tableInfo.setXmlName(tableInfo.getEntityName() + ConstVal.MAPPER);
			}
			if (StringUtils.isNotEmpty(globalConfig.getServiceName())) {
				tableInfo.setServiceName(String.format(globalConfig.getServiceName(), tableInfo.getEntityName()));
			} else {
				tableInfo.setServiceName("I" + tableInfo.getEntityName() + ConstVal.SERIVCE);
			}
			if (StringUtils.isNotEmpty(globalConfig.getServiceImplName())) {
				tableInfo.setServiceImplName(String.format(globalConfig.getServiceImplName(), tableInfo.getEntityName()));
			} else {
				tableInfo.setServiceImplName(tableInfo.getEntityName() + ConstVal.SERVICEIMPL);
			}
			if (StringUtils.isNotEmpty(globalConfig.getControllerName())) {
				tableInfo.setControllerName(String.format(globalConfig.getControllerName(), tableInfo.getEntityName()));
			} else {
				tableInfo.setControllerName(tableInfo.getEntityName() + ConstVal.CONTROLLER);
			}
		}
		return tableList;
	}

	/**
	 * 获取所有的数据库表信息
	 *
	 * @return 表信息
	 */
	private List<TableInfo> getTablesInfo(StrategyConfig config) {
		boolean isInclude = (null != config.getInclude() && config.getInclude().length > 0);
		boolean isExclude = (null != config.getExclude() && config.getExclude().length > 0);
		if (isInclude && isExclude) {
			throw new RuntimeException("<strategy> 标签中 <include> 与 <exclude> 只能配置一项！");
		}
		List<TableInfo> tableList = new ArrayList<TableInfo>();
		Set<String> notExistTables = new HashSet<String>();
		NamingStrategy strategy = config.getNaming();
		NamingStrategy fieldStrategy = config.getFieldNaming();
		PreparedStatement pstate = null;
		try {
			pstate = connection.prepareStatement(querySQL.getTableCommentsSql());
			ResultSet results = pstate.executeQuery();
			while (results.next()) {
				String tableName = results.getString(querySQL.getTableName());
				if (StringUtils.isNotEmpty(tableName)) {
					String tableComment = results.getString(querySQL.getTableComment());
					TableInfo tableInfo = new TableInfo();
					if (isInclude) {
						for (String includeTab : config.getInclude()) {
							if (includeTab.equalsIgnoreCase(tableName)) {
								tableInfo.setName(tableName);
								tableInfo.setComment(tableComment);
							} else {
								notExistTables.add(includeTab);
							}
						}
					} else if (isExclude) {
						for (String excludeTab : config.getExclude()) {
							if (!excludeTab.equalsIgnoreCase(tableName)) {
								tableInfo.setName(tableName);
								tableInfo.setComment(tableComment);
							} else {
								notExistTables.add(excludeTab);
							}
						}
					} else {
						tableInfo.setName(tableName);
						tableInfo.setComment(tableComment);
					}
					if (StringUtils.isNotEmpty(tableInfo.getName())) {
						List<TableField> fieldList = getListFields(tableInfo.getName(), fieldStrategy);
						tableInfo.setFields(fieldList);
						tableList.add(tableInfo);
					}
				} else {
					System.err.println("当前数据库为空！！！");
				}
			}
			// 将不存在或排除的表移除
			for (String table : notExistTables) {
				tableList.removeIf(tableInfo -> tableInfo.getName().equals(table));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 释放资源
			try {
				if (pstate != null) {
					pstate.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return processTable(tableList, strategy, config.getTablePrefix());
	}

	/**
	 * 将字段信息与表信息关联
	 *
	 * @param tableName
	 *            表名称
	 * @param strategy
	 *            命名策略
	 * @return 表信息
	 */
	private List<TableField> getListFields(String tableName, NamingStrategy strategy) throws SQLException {
		boolean havedId = false;

		PreparedStatement pstate = connection.prepareStatement(String.format(querySQL.getTableFieldsSql(), tableName));
		ResultSet results = pstate.executeQuery();

		List<TableField> fieldList = new ArrayList<TableField>();
		while (results.next()) {
			TableField field = new TableField();
			String key = results.getString(querySQL.getFieldKey());
			// 避免多重主键设置，目前只取第一个找到ID，并放到list中的索引为0的位置
			boolean isId = StringUtils.isNotEmpty(key) && key.toUpperCase().equals("PRI");
			// 处理ID
			if (isId && !havedId) {
				field.setKeyFlag(true);
				havedId = true;
			} else {
				field.setKeyFlag(false);
			}
			// 处理其它信息
			field.setName(results.getString(querySQL.getFieldName()));
			if (strategyConfig.includeSuperEntityColumns(field.getName())) {
				// 跳过公共字段
				continue;
			}
			field.setType(results.getString(querySQL.getFieldType()));
			field.setPropertyName(processName(field.getName(), strategy));
			field.setPropertyType(processFiledType(field.getType()));
			field.setComment(results.getString(querySQL.getFieldComment()));
			fieldList.add(field);
		}
		return fieldList;
	}

	/**
	 * 连接路径字符串
	 *
	 * @param parentDir
	 *            路径常量字符串
	 * @param packageName
	 *            包名
	 * @return 连接后的路径
	 */
	private String joinPath(String parentDir, String packageName) {
		if (StringUtils.isEmpty(parentDir)) {
			parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
		}
		if (!StringUtils.endsWith(parentDir, File.separator)) {
			parentDir += File.separator;
		}
		packageName = packageName.replaceAll("\\.", "\\" + File.separator);
		return parentDir + packageName;
	}

	/**
	 * 连接父子包名
	 *
	 * @param parent
	 *            父包名
	 * @param subPackage
	 *            子包名
	 * @return 连接后的包名
	 */
	private String joinPackage(String parent, String subPackage) {
		if (StringUtils.isEmpty(parent)) {
			return subPackage;
		}
		return parent + "." + subPackage;
	}

	/**
	 * 处理字段类型
	 *
	 * @return 转换成JAVA包装类型
	 */
	private String processFiledType(String type) {
		if (QuerySQL.MYSQL == querySQL) {
			return processMySqlType(type);
		} else if (QuerySQL.ORACLE == querySQL) {
			return processOracleType(type);
		} else if (QuerySQL.SQL_SERVER == querySQL) {
			return processSQLServerType(type);
		} else if (QuerySQL.POSTGRE_SQL == querySQL) {
			return processPostgreSQL(type);
		}
		return null;
	}

	/**
	 * 处理字段名称
	 *
	 * @return 根据策略返回处理后的名称
	 */
	private String processName(String name, NamingStrategy strategy) {
		return processName(name, strategy, null);
	}

	/**
	 * 处理字段名称
	 *
	 * @param name
	 * @param strategy
	 * @param tablePrefix
	 * @return 根据策略返回处理后的名称
	 */
	private String processName(String name, NamingStrategy strategy, String tablePrefix) {
		String propertyName = "";
		if (strategy == NamingStrategy.remove_prefix_and_camel) {
			propertyName = NamingStrategy.removePrefixAndCamel(name, tablePrefix);
		} else if (strategy == NamingStrategy.underline_to_camel) {
			propertyName = NamingStrategy.underlineToCamel(name);
		} else if (strategy == NamingStrategy.remove_prefix) {
			propertyName = NamingStrategy.removePrefix(name, tablePrefix);
		} else {
			propertyName = name;
		}
		return propertyName;
	}

	/**
	 * <p>
	 * Mysql 字段类型转换
	 * </p>
	 *
	 * @param type
	 *            字段类型
	 * @return JAVA类型
	 */
	private String processMySqlType(String type) {
		String t = type.toLowerCase();
		if (t.contains("char") || t.contains("text")) {
			return "String";
		} else if (t.contains("bigint")) {
			return "Long";
		} else if (t.contains("int")) {
			return "Integer";
		} else if (t.contains("date") || t.contains("time") || t.contains("year")) {
			return "java.util.Date";
		} else if (t.contains("text")) {
			return "String";
		} else if (t.contains("bit")) {
			return "Boolean";
		} else if (t.contains("decimal")) {
			return "java.math.BigDecimal";
		} else if (t.contains("blob")) {
			return "byte[]";
		} else if (t.contains("float")) {
			return "Float";
		} else if (t.contains("double")) {
			return "Double";
		} else if (t.contains("json") || t.contains("enum")) {
			return "String";
		}
		return "String";
	}

	/**
	 * <p>
	 * Oracle 字段类型转换
	 * </p>
	 *
	 * @param type
	 *            字段类型
	 * @return JAVA类型
	 */
	private String processOracleType(String type) {
		String t = type.toUpperCase();
		if (t.contains("CHAR")) {
			return "String";
		} else if (t.contains("DATE") || t.contains("TIMESTAMP")) {
			return "java.util.Date";
		} else if (t.contains("NUMBER")) {
			if (t.matches("NUMBER\\(+\\d{1}+\\)")) {
				return "Integer";
			} else if (t.matches("NUMBER\\(+\\d{2}+\\)")) {
				return "Long";
			}
			return "Double";
		} else if (t.contains("FLOAT")) {
			return "Float";
		} else if (t.contains("BLOB")) {
			return "Object";
		} else if (t.contains("RAW")) {
			return "byte[]";
		}
		return "String";
	}

	/**
	 * <p>
	 * SQLServer 字段类型转换
	 * </p>
	 *
	 * @param type
	 * @return
	 */
	private String processSQLServerType(String type) {
		String t = type.toLowerCase();
		if (t.contains("char") || t.contains("text") || t.contains("xml")) {
			return "String";
		} else if (t.contains("bigint")) {
			return "Long";
		} else if (t.contains("int")) {
			return "Integer";
		} else if (t.contains("date") || t.contains("time")) {
			return "java.util.Date";
		} else if (t.contains("text")) {
			return "String";
		} else if (t.contains("bit")) {
			return "Boolean";
		}else if (t.contains("decimal")||t.contains("numeric")){
			return "Double";
		}else if (t.contains("money")) {
			return "java.math.BigDecimal";
		} else if (t.contains("binary")||t.contains("image")) {
			return "byte[]";
		} else if (t.contains("float")||t.contains("real")) {
			return "Float";
		}
		return "String";
	}


	/**
	 * <p>
	 * PostgreSQL 字段类型转换
	 * </p>
	 *
	 * @param type
	 *            字段类型
	 * @return JAVA类型
	 */
	private String processPostgreSQL(String type) {
		String t = type.toLowerCase();
		if (t.contains("char") || t.contains("text")) {
			return "String";
		} else if (t.contains("bigint")) {
			return "Long";
		} else if (t.contains("int")) {
			return "Integer";
		} else if (t.contains("date") || t.contains("time") || t.contains("year")) {
			return "java.util.Date";
		} else if (t.contains("text")) {
			return "String";
		} else if (t.contains("bit")) {
			return "Boolean";
		} else if (t.contains("decimal")) {
			return "java.math.BigDecimal";
		} else if (t.contains("blob")) {
			return "byte[]";
		} else if (t.contains("float")) {
			return "Float";
		} else if (t.contains("double")) {
			return "Double";
		} else if (t.contains("json") || t.contains("enum")) {
			return "String";
		}
		return "String";
	}

	/**
	 * 获取当前的SQL类型
	 *
	 * @return DB类型
	 */
	private QuerySQL getQuerySQL(DbType dbType) {
		for (QuerySQL qs : QuerySQL.values()) {
			if (qs.getDbType().equals(dbType.getValue())) {
				return qs;
			}
		}
		return QuerySQL.MYSQL;
	}

	public StrategyConfig getStrategyConfig() {
		return strategyConfig;
	}

	public void setStrategyConfig(StrategyConfig strategyConfig) {
		this.strategyConfig = strategyConfig;
	}

	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}

}
