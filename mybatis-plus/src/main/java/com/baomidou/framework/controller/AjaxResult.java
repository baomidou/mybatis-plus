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
package com.baomidou.framework.controller;

/**
 * <p>
 * AJAX 返回对象类
 * </p>
 * 
 * @author hubin
 * @Date 2016-03-15
 */
public class AjaxResult {

	/**
	 * 是否成功
	 */
	private boolean success = true;

	/**
	 * 失败或成功的提示信息
	 */
	private String message;

	/**
	 * 返回的数据
	 */
	private Object data;


	public AjaxResult( boolean success, String message ) {
		this(success, message, null);
	}


	public AjaxResult( Object data ) {
		this(true, null, data);
	}


	public AjaxResult( boolean success, String message, Object data ) {
		this.success = success;
		this.message = message;
		this.data = data;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage( String message ) {
		this.message = message;
	}


	public Object getData() {
		return data;
	}


	public void setData( Object data ) {
		this.data = data;
	}


	public boolean isSuccess() {
		return success;
	}


	public void setSuccess( boolean success ) {
		this.success = success;
	}

}
