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
package com.baomidou.framework.exception;


/**
 * <p>
 * web 层异常
 * </p>
 * 
 * @author hubin
 * @Date 2016-03-15
 */
public class WebException extends RuntimeException {

	private static final long serialVersionUID = 8604424364318396626L;


	/**
	 * 异常代码
	 */
	private String code;

	/**
	 * 异常说明
	 */
	private String desc;


	public WebException() {
		super();
	}


	public WebException( String message ) {
		super(message);
		this.desc = message;
	}


	public WebException( String code, String desc ) {
		this.code = code;
		this.desc = desc;
	}


	public WebException( String code, String desc, Throwable cause ) {
		super(cause);
		this.code = code;
		this.desc = desc;
	}


	public WebException( String code, String desc, String message ) {
		super(message);
		this.code = code;
		this.desc = desc;
	}


	public String getCode() {
		return code;
	}


	public String getDesc() {
		return desc;
	}


	@Override
	public String getMessage() {
		if ( super.getMessage() == null ) {
			return desc;
		}
		return super.getMessage();
	}
}
