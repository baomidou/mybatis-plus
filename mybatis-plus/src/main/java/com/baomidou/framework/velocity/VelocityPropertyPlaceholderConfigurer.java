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
package com.baomidou.framework.velocity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * <p>
 * velocity 模式加载 properties
 * </p>
 * <p>
 * 支持 properties 文件使用 velocity 标签控制，注入 VelocityContext 可定义标签内容。
 * </p>
 * @author hubin
 * @Date 2016-01-27
 */
public class VelocityPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	private String charsetName = "UTF-8";

	private VelocityContext velocityContext = new VelocityContext();

	private Resource[] locations;


	@Override
	public void setLocation( Resource location ) {
		this.locations = new Resource[ ] { location };
	}


	@Override
	public void setLocations( Resource... locations ) {
		this.locations = locations;
	}


	public void fillMergeProperties( Properties prop, InputStream input ) {
		try {
			StringWriter writer = new StringWriter();
			BufferedReader br = new BufferedReader(new InputStreamReader(input, this.charsetName));
			Velocity.evaluate(this.velocityContext, writer, "VelocityPropertyPlaceholderConfigurer", br);
			prop.load(new StringReader(writer.toString()));
		} catch ( Exception e ) {
			throw new MybatisPlusException(e);
		}
	}


	@Override
	protected void loadProperties( Properties props ) throws IOException {
		if ( this.locations != null && props != null ) {
			for ( Resource location : this.locations ) {
				if ( logger.isInfoEnabled() ) {
					logger.info("Loading properties file from " + location);
				}
				this.fillMergeProperties(props, location.getInputStream());
			}
		}
	}


	public void setCharsetName( String charsetName ) {
		this.charsetName = charsetName;
	}


	public void setVelocityContext( VelocityContext velocityContext ) {
		this.velocityContext = velocityContext;
	}

}
