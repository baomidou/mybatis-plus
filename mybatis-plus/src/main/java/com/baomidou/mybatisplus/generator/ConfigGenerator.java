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

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.toolkit.StringUtils;


/**
 * <p>
 * 生成器配置类
 * ********************************* 使用前必读 *********************
 * saveDir 文件生成目录
 * entity_package 		entity 包路径
 * mapper_package 		mapper 包路径
 * xmlPackage 			xx_mapper.xml 包路径，默认为mapper/xml
 * servicePackage 		service 包路径
 * controllerPackage 	controller 包路径
 * serviceImplPackage 	serviceImpl包路径，默认为service/impl
 * superService 		service 父类包路径名称
 * superServiceImpl 	service 实现父类包路径名称
 * mapperName			自定义 mapper 名称
 * mapperXMLName		自定义 xml 名称
 * serviceName			自定义 service 名称
 * serviceImplName		自定义 serviceImp 名称
 * tableNames   		要生成的表名称，如为空就直接指定所有表.格式为逗号分割
 * columnConstant  	   【实体】是否生成字段常量（默认 false）
 * buliderModel	   	   【实体】是否为构建者模型（默认 false）
 * fileOverride 		是否覆盖当前已有文件
 * -------------------------------------
 * 以下数据库相关配置：
 * -------------------------------------
 * db_include_prefix 表是否包含前缀，例如: tb_xxx 其中 tb_ 为前缀
 * db_driverName 驱动
 * db_user 用户名
 * db_password 密码
 * db_url 连接地址
 **************************************************************
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class ConfigGenerator {

	protected String saveDir;

	protected String entityPackage;

	protected String mapperPackage;

	protected String xmlPackage;

	protected String servicePackage;

	protected String serviceImplPackage;

	protected String controllerPackage;

	protected String superService;

	protected String superServiceImpl;

	/*
	 * 自定义 mapperName serviceName serviceImplName
	 */
	protected String mapperName = "%sMapper";

	protected String mapperXMLName = "%sMapper";

	protected String serviceName = "I%sService";

	protected String serviceImplName = "%sServiceImpl";

	protected String controllerName = "%sController";

	/*
	 * 指定生成表名
	 */
	protected String[] tableNames = null;
	
	/*
	 * 【实体】是否生成字段常量（默认 false）<br>
	 * -----------------------------------<br>
	 * public static final String ID = "test_id";
	 */
	protected boolean columnConstant = false;

	/*
	 * 【实体】是否为构建者模型（默认 false）<br>
	 * -----------------------------------<br>
	 * 	public User setName(String name) {
	 * 		this.name = name;
	 * 		return this;
	 * 	}
	 */
	protected boolean buliderModel = false;
	
	/*
	 * 是否覆盖当前路径下已有文件（默认 true）
	 */
	protected boolean fileOverride = true;

	/*
	 * true 生成 resultMap ， false 生成通用 Base_Column_List
	 */
	protected boolean resultMap = false;

	/* db_config */
	protected boolean dbPrefix = false;

	/*
	 * 数据库字段使用下划线命名（默认 false）
	 */
	protected boolean dbColumnUnderline = false;

	protected String dbDriverName;

	protected String dbUser;

	protected String dbPassword;

	protected String dbUrl;

	protected IdType idType = null;

	protected ConfigDataSource configDataSource = ConfigDataSource.MYSQL;

	protected ConfigIdType configIdType = ConfigIdType.LONG;

	protected ConfigBaseEntity configBaseEntity = null;


	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public String getEntityPackage() {
		return entityPackage;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	public String getMapperPackage() {
		return mapperPackage;
	}

	public void setMapperPackage(String mapperPackage) {
		this.mapperPackage = mapperPackage;
	}

	public String getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}

	public String getSuperService() {
		if (StringUtils.isEmpty(superService)) {
			return "com.baomidou.framework.service.IService";
		}
		return superService;
	}

	public void setSuperService(String superService) {
		this.superService = superService;
	}

	public String getSuperServiceImpl() {
		if (StringUtils.isEmpty(superServiceImpl)) {
			return "com.baomidou.framework.service.impl.ServiceImpl";
		}
		return superServiceImpl;
	}

	public ConfigBaseEntity getConfigBaseEntity() {
		return configBaseEntity;
	}

	public void setConfigBaseEntity(ConfigBaseEntity configBaseEntity) {
		this.configBaseEntity = configBaseEntity;
	}

	public void setSuperServiceImpl(String superServiceImpl) {
		this.superServiceImpl = superServiceImpl;
	}

	public boolean verifyDefinedName(String definedName) {
		return (null != definedName && definedName.contains("%s"));
	}

	public String getMapperName() {
		return mapperName;
	}

	public void setMapperName(String mapperName) {
		if (verifyDefinedName(mapperName)) {
			this.mapperName = mapperName;
		}
	}

	public String getMapperXMLName() {
		return mapperXMLName;
	}

	public void setMapperXMLName(String mapperXMLName) {
		if (verifyDefinedName(mapperXMLName)) {
			this.mapperXMLName = mapperXMLName;
		}
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		if (verifyDefinedName(serviceName)) {
			this.serviceName = serviceName;
		}
	}

	public String getServiceImplName() {
		return serviceImplName;
	}

	public void setServiceImplName(String serviceImplName) {
		if (verifyDefinedName(serviceImplName)) {
			this.serviceImplName = serviceImplName;
		}
	}

	public boolean isDbPrefix() {
		return dbPrefix;
	}

	public void setDbPrefix(boolean dbPrefix) {
		this.dbPrefix = dbPrefix;
	}

	public boolean isDbColumnUnderline() {
		return dbColumnUnderline;
	}

	public void setDbColumnUnderline(boolean dbColumnUnderline) {
		this.dbColumnUnderline = dbColumnUnderline;
	}

	public String getDbDriverName() {
		return dbDriverName;
	}

	public void setDbDriverName(String dbDriverName) {
		this.dbDriverName = dbDriverName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public ConfigDataSource getConfigDataSource() {
		return configDataSource;
	}

	public void setConfigDataSource(ConfigDataSource configDataSource) {
		this.configDataSource = configDataSource;
	}

	public ConfigIdType getConfigIdType() {
		return configIdType;
	}

	public void setConfigIdType(ConfigIdType configIdType) {
		this.configIdType = configIdType;
	}

	public String getXmlPackage() {
		if (StringUtils.isEmpty(xmlPackage)) {
			xmlPackage = mapperPackage + ".xml";
		}
		return xmlPackage;
	}

	public void setXmlPackage(String xmlPackage) {
		this.xmlPackage = xmlPackage;
	}

	public String[] getTableNames() {
		return tableNames;
	}

	public void setTableNames(String[] tableNames) {
		this.tableNames = tableNames;
	}

	public boolean isColumnConstant() {
		return columnConstant;
	}

	public void setColumnConstant(boolean columnConstant) {
		this.columnConstant = columnConstant;
	}

	public boolean isBuliderModel() {
		return buliderModel;
	}

	public void setBuliderModel(boolean buliderModel) {
		this.buliderModel = buliderModel;
	}

	public boolean isFileOverride() {
		return fileOverride;
	}

	public void setFileOverride(boolean fileOverride) {
		this.fileOverride = fileOverride;
	}

	public boolean isResultMap() {
		return resultMap;
	}

	public void setResultMap(boolean resultMap) {
		this.resultMap = resultMap;
	}

	public String getServiceImplPackage() {
		if (StringUtils.isEmpty(serviceImplPackage)) {
			serviceImplPackage = servicePackage + ".impl";
		}
		return serviceImplPackage;
	}

	public void setServiceImplPackage(String serviceImplPackage) {
		this.serviceImplPackage = serviceImplPackage;
	}

	public String getControllerPackage() {
		return controllerPackage;
	}

	public void setControllerPackage(String controllerPackage) {
		this.controllerPackage = controllerPackage;
	}

	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

}
