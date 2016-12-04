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
package com.baomidou.mybatisplus.generator.config.po;

import java.util.List;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 表信息，关联到当前字段信息
 * </p>
 *
 * @author YangHu
 * @since 2016/8/30
 */
public class TableInfo {

	private String name;
	private String comment;

	private String entityName;
	private String mapperName;
	private String xmlName;
	private String serviceName;
	private String serviceImplName;
	private String controllerName;

	private List<TableField> fields;
	private String fieldNames;
	private boolean hasDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getEntityPath() {
		StringBuffer ep = new StringBuffer();
		ep.append(entityName.substring(0, 1).toLowerCase());
		ep.append(entityName.substring(1));
		return ep.toString();
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getMapperName() {
		return mapperName;
	}

	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
	}

	public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceImplName() {
		return serviceImplName;
	}

	public void setServiceImplName(String serviceImplName) {
		this.serviceImplName = serviceImplName;
	}

	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public List<TableField> getFields() {
		return fields;
	}

	public void setFields(List<TableField> fields) {
		this.fields = fields;
	}

	/**
	 * 转换filed实体为xmlmapper中的basecolumn字符串信息
	 *
	 * @return
	 */
	public String getFieldNames() {
		if (StringUtils.isEmpty(fieldNames)) {
			StringBuilder names = new StringBuilder();
			for (int i = 0; i < fields.size(); i++) {
				TableField fd = fields.get(i);
				if (i == fields.size() - 1) {
					names.append(cov2col(fd));
				} else {
					names.append(cov2col(fd)).append(", ");
				}
			}
			fieldNames = names.toString();
		}
		return fieldNames;
	}

	/**
	 * 判断字段中是否包含日期类型
	 *
	 * @return 是否
	 */
	public boolean isHasDate() {
		for (TableField fieldInfo : fields) {
			if (fieldInfo.getPropertyType().equals("Date")) {
				hasDate = true;
				break;
			}
		}
		return hasDate;
	}

	/**
	 * mapper xml中的字字段添加as
	 *
	 * @param field
	 *            字段实体
	 * @return 转换后的信息
	 */
	private String cov2col(TableField field) {
		if (null != field) {
			return field.isConvert() ? field.getName() + " AS " + field.getPropertyName() : field.getName();
		}
		return StringUtils.EMPTY;
	}

}
