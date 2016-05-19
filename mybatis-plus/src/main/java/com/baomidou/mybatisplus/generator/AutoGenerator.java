/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * <p>
 * 映射文件自动生成类
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class AutoGenerator {

	private ConfigGenerator config;

	public ConfigGenerator getConfig() {
		return config;
	}

	public void setConfig(ConfigGenerator config) {
		this.config = config;
	}

	public AutoGenerator() {

	}

	public AutoGenerator(ConfigGenerator config) {
		this.config = config;
	}

	private static String PATH_ENTITY = null;
	private static String PATH_MAPPER = null;
	private static String PATH_SERVICE = null;
	private static String PATH_SERVICE_IMPL = null;
	private static String PATH_XML = null;
	
	/**
	 * run 执行
	 */
	public static void run(ConfigGenerator config) {
		if (config == null) {
			throw new MybatisPlusException(" ConfigGenerator is null. ");
		} else if ( config.getIdType() == null ) {
			throw new MybatisPlusException("ConfigGenerator IdType is null");
		}

		/**
		 * 检查文件夹是否存在
		 */
		File gf = new File(config.getSaveDir());
		if (!gf.exists()) {
			gf.mkdirs();
		}
		
		/**
		 * 目录初始化
		 */
		PATH_ENTITY = getFilePath(gf.getPath(), "entity");
		PATH_MAPPER = getFilePath(gf.getPath(), "mapper");
		PATH_XML = getFilePath(gf.getPath(), "xml");
		PATH_SERVICE = getFilePath(gf.getPath(), "service");
		PATH_SERVICE_IMPL = getFilePath(gf.getPath(), "serviceImpl");

		/**
		 * 开启生成映射关系
		 */
		new AutoGenerator(config).generate();

		/**
		 * 自动打开生成文件的目录
		 * <p>
		 * 根据 osName 执行相应命令
		 * </p>
		 */
		try {
			String osName = System.getProperty("os.name");
			if (osName != null) {
				if (osName.contains("Mac")) {
					Runtime.getRuntime().exec("open " + config.getSaveDir());
				} else if (osName.contains("Windows")) {
					Runtime.getRuntime().exec("cmd /c start " + config.getSaveDir());
				} else {
					System.err.println("save dir:" + config.getSaveDir());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(" generate success! ");

	}
	
	/**
	 * 
	 * 生成文件地址
	 * 
	 * @param segment
	 *            文件地址片段
	 * @return
	 */
	private static String getFilePath(String savePath, String segment) {
		File folder = new File(savePath + File.separator + segment);
		if (!folder.exists()) {
			folder.mkdir();
		}
		return folder.getPath();
	}

	/**
	 * 生成映射文件
	 * 
	 * @param includePrefix
	 *            是否包含前缀，例如: tb_xxx 其中 tb_ 为前缀
	 */
	public void generate() {
		Connection conn = null;
		try {
			/**
			 * 创建连接
			 */
			Class.forName(config.getDbDriverName());
			conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());

			/**
			 * 获取数据库表相关信息
			 */
			boolean isOracle = false;
			if ( config.getConfigDataSource() == ConfigDataSource.ORACLE ) {
				isOracle = true;
			}
			List<String> tables = getTables(conn);
			Map<String, String> tableComments = getTableComment(conn);
			for (String table : tables) {
				List<String> columns = new ArrayList<String>();
				List<String> types = new ArrayList<String>();
				List<String> comments = new ArrayList<String>();
				Map<String, IdInfo> idMap = new HashMap<String, IdInfo>();
				String tableFieldsSql = String.format(config.getConfigDataSource().getTableFieldsSql(), table);
				ResultSet results = conn.prepareStatement(tableFieldsSql).executeQuery();
				while (results.next()) {
					String field = results.getString(config.getConfigDataSource().getFieldName());
					columns.add(field);
					types.add(results.getString(config.getConfigDataSource().getFieldType()));
					comments.add(results.getString(config.getConfigDataSource().getFieldComment()));
					if ( !isOracle ) {
						/* MYSQL 主键ID 处理方式 */
						String key = results.getString(config.getConfigDataSource().getFieldKey());
						if ("PRI".equals(key)) {
							boolean autoIncrement = false;
							if ("auto_increment".equals(results.getString("EXTRA"))) {
								autoIncrement = true;
							}
							idMap.put(field, new IdInfo(field, autoIncrement));
						}
					}
				}
				if ( isOracle ) {
					/* ORACLE 主键ID 处理方式 */
					String idSql = String.format("SELECT A.COLUMN_NAME FROM USER_CONS_COLUMNS A, USER_CONSTRAINTS B WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND B.CONSTRAINT_TYPE = 'P' AND A.TABLE_NAME = '%s'", table);
					ResultSet rs = conn.prepareStatement(idSql).executeQuery();
					while (rs.next()) {
						String field = rs.getString(config.getConfigDataSource().getFieldKey());
						idMap.put(field, new IdInfo(field, false));
					}
				}
				String beanName = getBeanName(table, config.isDbPrefix());
				String mapperName = beanName + "Mapper";
				String serviceName = "I" + beanName + "Service";
				String serviceImplName = beanName + "ServiceImpl";

				/**
				 * 生成映射文件
				 */
				buildEntityBean(columns, types, comments, tableComments.get(table), idMap, table, beanName);
				buildMapper(beanName, mapperName);
				buildMapperXml(columns, types, comments, idMap, mapperName);
				buildService(beanName, serviceName);
				buildServiceImpl(beanName, serviceImplName, serviceName, mapperName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取所有的表
	 *
	 * @return
	 * @throws SQLException
	 */
	private List<String> getTables(Connection conn) throws SQLException {
		String[] tableNames = config.getTableNames();
		if ( tableNames != null ) {
			return Arrays.asList(tableNames);
		}
		/**
		 * 数据库中读取所有表名
		 */
		List<String> tables = new ArrayList<String>();
		PreparedStatement pstate = conn.prepareStatement(config.getConfigDataSource().getTablesSql());
		ResultSet results = pstate.executeQuery();
		while (results.next()) {
			tables.add(results.getString(1));
		}
		return tables;
	}

	/**
	 * 生成 beanName
	 * 
	 * @param table
	 *            表名
	 * @return beanName
	 */
	private String getBeanName(String table, boolean includePrefix) {
		StringBuffer sb = new StringBuffer();
		if (table.contains("_")) {
			String[] tables = table.split("_");
			int l = tables.length;
			int s = 0;
			if (includePrefix) {
				s = 1;
			}
			for (int i = s; i < l; i++) {
				String temp = tables[i].trim();
				sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1).toLowerCase());
			}
		} else {
			sb.append(table.substring(0, 1).toUpperCase()).append(table.substring(1).toLowerCase());
		}
		return sb.toString();
	}
	

	private String processType( String type ) {
		if ( config.getConfigDataSource() == ConfigDataSource.ORACLE ) {
			return oracleProcessType(type);
		}
		return mysqlProcessType(type);
	}
	

	/**
	 * MYSQL字段类型转换
	 * 
	 * @param type
	 *            字段类型
	 * @return
	 */
	private String mysqlProcessType(String type) {
		if (type.indexOf("char") > -1) {
			return "String";
		} else if (type.indexOf("bigint") > -1) {
			return "Long";
		} else if (type.indexOf("int") > -1) {
			return "Integer";
		} else if (type.indexOf("date") > -1 || type.indexOf("timestamp") > -1) {
			return "Date";
		} else if (type.indexOf("text") > -1) {
			return "String";
		} else if (type.indexOf("bit") > -1) {
			return "Boolean";
		} else if (type.indexOf("decimal") > -1) {
			return "BigDecimal";
		} else if (type.indexOf("blob") > -1) {
			return "byte[]";
		} else if (type.indexOf("float") > -1) {
			return "Float";
		} else if (type.indexOf("double") > -1) {
			return "Double";
		}
		return null;
	}
	
	/**
	 * ORACLE字段类型转换
	 * 
	 * @param type
	 *            字段类型
	 * @return
	 */
	private String oracleProcessType(String type) {
		if (type.indexOf("CHAR") > -1) {
			return "String";
		} else if (type.indexOf("DATE") > -1) {
			return "Date";
		} else if (type.indexOf("NUMBER") > -1) {
			return "Integer";
		} else if (type.indexOf("FLOAT") > -1) {
			return "Float";
		} else if (type.indexOf("BLOB") > -1) {
			return "Object";
		} else if (type.indexOf("RAW") > -1) {
			return "byte[]";
		}
		return null;
	}
	
	/**
	 * 字段是否为日期类型
	 * 
	 * @param types
	 *            字段类型列表
	 * @return
	 */
	private boolean isDate(List<String> types) {
		int size = types.size();
		for ( int i = 0 ; i < size ; i++ ) {
			String type = types.get(i);
			if (type.indexOf("date") > -1 || type.indexOf("timestamp") > -1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 字段是否为浮点数类型
	 * 
	 * @param types
	 *            字段类型列表
	 * @return
	 */
	private boolean isDecimal(List<String> types) {
		int size = types.size();
		for ( int i = 0 ; i < size ; i++ ) {
			String type = types.get(i);
			if (type.indexOf("decimal") > -1) {
				return true;
			}
		}
		return false;
	}

	private String processField(String field) {
		/*
		 * 驼峰命名直接返回
		 */
		if (config.isColumnHump()) {
			return field;
		}
		
		/* 
		 * 处理下划线分割命名字段
		 */
		StringBuffer sb = new StringBuffer(field.length());
		String[] fields = field.split("_");
		sb.append(fields[0].toLowerCase());
		for (int i = 1; i < fields.length; i++) {
			String temp = fields[i];
			sb.append(temp.substring(0, 1).toUpperCase());
			sb.append(temp.substring(1).toLowerCase());
		}
		return sb.toString();
	}

	/**
	 * 构建类上面的注释
	 *
	 * @param bw
	 * @param text
	 * @return
	 * @throws IOException
	 */
	private BufferedWriter buildClassComment(BufferedWriter bw, String text) throws IOException {
		bw.newLine();
		bw.write("/**");
		bw.newLine();
		bw.write(" *");
		bw.newLine();
		bw.write(" * " + text);
		bw.newLine();
		bw.write(" *");
		bw.newLine();
		bw.write(" */");
		return bw;
	}

	/**
	 * 
	 * 生成实体类
	 *
	 * @param columns
	 * @param types
	 * @param comments
	 * @throws IOException
	 */
	private void buildEntityBean(List<String> columns, List<String> types, List<String> comments, String tableComment,
			Map<String, IdInfo> idMap, String table, String beanName) throws IOException {
		File beanFile = new File(PATH_ENTITY, beanName + ".java");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile)));
		bw.write("package " + config.getEntityPackage() + ";");
		bw.newLine();
		bw.newLine();
		bw.write("import java.io.Serializable;");
		bw.newLine();
		if (isDate(types)) {
			bw.write("import java.util.Date;");
			bw.newLine();
		}
		if (isDecimal(types)) {
			bw.write("import java.math.BigDecimal;");
			bw.newLine();
		}
		bw.newLine();
		if (config.getIdType() != IdType.ID_WORKER) {
			bw.write("import com.baomidou.mybatisplus.annotations.IdType;");
			bw.newLine();
		}
		bw.write("import com.baomidou.mybatisplus.annotations.TableField;");
		bw.newLine();
		bw.write("import com.baomidou.mybatisplus.annotations.TableId;");
		bw.newLine();
		bw.write("import com.baomidou.mybatisplus.annotations.TableName;");
		bw.newLine();
		bw = buildClassComment(bw, tableComment);
		bw.newLine();
		bw.write("@TableName(value = \"" + table + "\")");
		bw.newLine();
		bw.write("public class " + beanName + " implements Serializable {");
		bw.newLine();
		bw.newLine();
		bw.write("\t@TableField(exist = false)");
		bw.newLine();
		bw.write("\tprivate static final long serialVersionUID = 1L;");
		bw.newLine();
		int size = columns.size();
		for (int i = 0; i < size; i++) {
			bw.newLine();
			bw.write("\t/** " + comments.get(i) + " */");
			bw.newLine();
			/*
			 * 判断ID 添加注解
			 * <br>
			 * isLine 是否包含下划线
			 */
			String column = columns.get(i);
			String field = processField(column);
			boolean isLine = column.contains("_");
			IdInfo idInfo = idMap.get(column);
			if (idInfo != null) {
				//@TableId(value = "test_id", type = IdType.AUTO_INCREMENT)
				bw.write("\t@TableId");
				String idType = toIdType();
				if (idInfo.isAutoIncrement()) {
					System.err.println(" Table :{ " + table + " } ID is Auto increment");
					if (isLine) {
						bw.write("(value = \"" + column + "\"");
						if ( idType != null ) {
							bw.write(", ");
							bw.write(idType);
						}
						bw.write(")");
					} else if ( idType != null ) {
						bw.write("(");
						bw.write(idType);
						bw.write(")");
					}
				} else {
					if (isLine) {
						bw.write("(value = \"" + column + "\"");
						if(idType != null){
							bw.write(", ");
							bw.write(idType);
						}
						bw.write(")");
					} else if ( idType != null ) {
						bw.write("(");
						bw.write(idType);
						bw.write(")");
					}
				}
				bw.newLine();
			} else if (isLine) {
				//@TableField(value = "test_type", exist = false)
				bw.write("\t@TableField(value = \"" + column + "\")");
				bw.newLine();
			}
			bw.write("\tprivate " + processType(types.get(i)) + " " + field + ";");
			bw.newLine();
		}

		/*
		 * 生成get 和 set方法
		 */
		for (int i = 0; i < size; i++) {
			String _tempType = processType(types.get(i));
			String _tempField = processField(columns.get(i));
			String _field = _tempField.substring(0, 1).toUpperCase() + _tempField.substring(1);
			bw.newLine();
			bw.write("\tpublic " + _tempType + " get" + _field + "() {");
			bw.newLine();
			bw.write("\t\treturn this." + _tempField + ";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			bw.write("\tpublic void set" + _field + "(" + _tempType + " " + _tempField + ") {");
			bw.newLine();
			bw.write("\t\tthis." + _tempField + " = " + _tempField + ";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
		}

		bw.newLine();
		bw.write("}");
		bw.newLine();
		bw.flush();
		bw.close();
	}
	
	public String toIdType(){
		if ( config.getIdType() == IdType.AUTO ) {
			return "type = IdType.AUTO";
		} else if ( config.getIdType() == IdType.INPUT ) {
			return "type = IdType.INPUT";
		} else if ( config.getIdType() == IdType.UUID ) {
			return "type = IdType.UUID";
		}
		return null;
	}

	/**
	 * 
	 * 构建Mapper文件
	 * 
	 * @param beanName
	 * @param mapperName
	 * @throws IOException
	 */
	private void buildMapper(String beanName, String mapperName) throws IOException {
		File mapperFile = new File(PATH_MAPPER, mapperName + ".java");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile), "utf-8"));
		bw.write("package " + config.getMapperPackage() + ";");
		bw.newLine();
		bw.newLine();
		bw.write("import " + config.getEntityPackage() + "." + beanName + ";");
		bw.newLine();
		if ( config.getConfigIdType() == ConfigIdType.STRING ) {
			bw.write("import com.baomidou.mybatisplus.mapper.CommonMapper;");
		} else {
			bw.write("import com.baomidou.mybatisplus.mapper.AutoMapper;");
		}
		bw.newLine();

		bw = buildClassComment(bw, beanName + " 表数据库控制层接口");
		bw.newLine();
		if ( config.getConfigIdType() == ConfigIdType.STRING ) {
			bw.write("public interface " + mapperName + " extends CommonMapper<" + beanName + "> {");
		} else {
			bw.write("public interface " + mapperName + " extends AutoMapper<" + beanName + "> {");
		}
		bw.newLine();
		bw.newLine();

		// ----------定义mapper中的方法End----------
		bw.newLine();
		bw.write("}");
		bw.flush();
		bw.close();
	}

	/**
	 * 
	 * 构建实体类映射XML文件
	 *
	 * @param columns
	 * @param types
	 * @param comments
	 * @throws IOException
	 */
	private void buildMapperXml( List<String> columns, List<String> types, List<String> comments,
			Map<String, IdInfo> idMap, String mapperName ) throws IOException {
		File mapperXmlFile = new File(PATH_XML, mapperName + ".xml");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlFile)));
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bw.newLine();
		bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
		bw.newLine();
		bw.write("<mapper namespace=\"" + config.getMapperPackage() + "." + mapperName + "\">");
		bw.newLine();
		bw.newLine();

		/*
		 * 下面开始写SqlMapper中的方法
		 */
		buildSQL(bw, idMap, columns);

		bw.write("</mapper>");
		bw.flush();
		bw.close();
	}

	/**
	 * 
	 * 构建service文件
	 * 
	 * @param beanName
	 * @param serviceName
	 * @throws IOException
	 */
	private void buildService(String beanName, String serviceName) throws IOException {
		File serviceFile = new File(PATH_SERVICE, serviceName + ".java");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceFile), "utf-8"));
		bw.write("package " + config.getServicePackage() + ";");
		bw.newLine();
		bw.newLine();
		bw.write("import " + config.getEntityPackage() + "." + beanName + ";");
		bw.newLine();
		if ( config.getConfigIdType() == ConfigIdType.STRING ) {
			bw.write("import com.baomidou.framework.service.ICommonService;");
		} else {
			bw.write("import com.baomidou.framework.service.ISuperService;");
		}
		bw.newLine();

		bw = buildClassComment(bw, beanName + " 表数据服务层接口");
		bw.newLine();
		if ( config.getConfigIdType() == ConfigIdType.STRING ) {
			bw.write("public interface " + serviceName + " extends ICommonService<" + beanName + "> {");
		} else {
			bw.write("public interface " + serviceName + " extends ISuperService<" + beanName + "> {");
		}
		bw.newLine();
		bw.newLine();

		// ----------定义service中的方法End----------
		bw.newLine();
		bw.write("}");
		bw.flush();
		bw.close();
	}
	
	/**
	 * 
	 * 构建service实现类文件
	 * 
	 * @param beanName
	 * @param serviceImplName
	 * @param mapperName
	 * @throws IOException
	 */
	private void buildServiceImpl(String beanName, String serviceImplName, String serviceName, String mapperName) throws IOException {
		File serviceFile = new File(PATH_SERVICE_IMPL, serviceImplName + ".java");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceFile), "utf-8"));
		bw.write("package " + config.getServicePackage() + ".impl;");
		bw.newLine();
		bw.newLine();
		bw.write("import org.springframework.stereotype.Service;");
		bw.newLine();
		bw.newLine();
		bw.write("import " + config.getMapperPackage() + "." + mapperName + ";");
		bw.newLine();
		bw.write("import " + config.getEntityPackage() + "." + beanName + ";");
		bw.newLine();
		bw.write("import " + config.getServicePackage() + "." + serviceName + ";");
		bw.newLine();
		
		String superServiceImpl = config.getSuperServiceImpl();
		bw.write("import " + superServiceImpl + ";");
		bw.newLine();
		
		bw = buildClassComment(bw, beanName + " 表数据服务层接口实现类");
		bw.newLine();
		bw.write("@Service");
		bw.newLine();
		superServiceImpl = superServiceImpl.substring(superServiceImpl.lastIndexOf(".") + 1);
		bw.write("public class " + serviceImplName + " extends " + superServiceImpl
				+ "<" + mapperName + ", " + beanName + "> implements " + serviceName + " {");
		bw.newLine();
		bw.newLine();
		
		// ----------定义service中的方法End----------
		bw.newLine();
		bw.write("}");
		bw.flush();
		bw.close();
	}

	/**
	 * 
	 * 通用返回参数
	 * 
	 * @param bw
	 * @param columns
	 * @throws IOException
	 */
	private void buildSQL( BufferedWriter bw, Map<String, IdInfo> idMap, List<String> columns ) throws IOException {
		int size = columns.size();
		bw.write("\t<!-- 通用查询结果列-->");
		bw.newLine();
		bw.write("\t<sql id=\"Base_Column_List\">");
		bw.newLine();
		
		for (int i = 0; i < size; i++) {
			String column = columns.get(i);
			IdInfo idInfo = idMap.get(column);
			if ( idInfo != null ) {
				bw.write("\t\t " + idInfo.getValue());
			} else {
				bw.write(" " + column);
			}
			if (i != size - 1) {
				bw.write(",");
			}
		}
		bw.newLine();
		bw.write("\t</sql>");
		bw.newLine();
		bw.newLine();
	}

	/**
	 * 
	 * 获取所有的数据库表注释
	 *
	 * @return
	 * @throws SQLException
	 */
	private Map<String, String> getTableComment(Connection conn) throws SQLException {
		Map<String, String> maps = new HashMap<String, String>();
		PreparedStatement pstate = conn.prepareStatement(config.getConfigDataSource().getTableCommentsSql());
		ResultSet results = pstate.executeQuery();
		while (results.next()) {
			maps.put(results.getString(config.getConfigDataSource().getTableName()), 
				results.getString(config.getConfigDataSource().getTableComment()));
		}
		return maps;
	}

	class IdInfo {
		private String value;
		private boolean autoIncrement;

		public IdInfo(String value, boolean autoIncrement) {
			this.value = value;
			this.autoIncrement = autoIncrement;

		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isAutoIncrement() {
			return autoIncrement;
		}

		public void setAutoIncrement(boolean autoIncrement) {
			this.autoIncrement = autoIncrement;
		}
	}
}
