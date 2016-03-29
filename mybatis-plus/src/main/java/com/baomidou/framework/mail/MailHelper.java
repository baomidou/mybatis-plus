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
package com.baomidou.framework.mail;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * <p>
 * 邮件帮助类
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-15
 */
public class MailHelper {

	protected Logger logger = LoggerFactory.getLogger(MailHelper.class);

	private final static String ENCODING = "UTF-8";

	private VelocityEngine velocityEngine;

	private JavaMailSender mailSender;

	private String encoding = ENCODING;

	private String mailTitle;

	private String mailFrom;


	public boolean sendMail( String to, String subject, String tplName, Map<String, Object> data ) {
		return sendMail(new String[ ] { to }, subject, tplName, data);
	}


	public boolean sendMail( String[] to, String subject, String tplName, Map<String, Object> data ) {
		return sendMail(this.mailTitle, this.mailFrom, to, subject, tplName, data);
	}


	/**
	 * 发送邮件
	 * 
	 * @param personal
	 *            名称（邮件发送者名称）
	 * @param from
	 *            发送者（邮件地址）
	 * @param to
	 *            发送至（邮件地址）
	 * @param subject
	 *            主题（内容标题）
	 * @param tplName
	 *            模板名称（xxx.vm ， 模板地址：/WEB-INF/views/mail/..）
	 * @param data
	 *            参数（模板参数）
	 * @return
	 */
	protected boolean sendMail( String personal, String from, String[] to, String subject, String tplName,
			Map<String, Object> data ) {
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper msgHelper = new MimeMessageHelper(msg, ENCODING);
			msgHelper.setFrom(from, personal);
			msgHelper.setTo(to);
			msgHelper.setSubject(subject);
			msgHelper.setText(this.getHtmltext(tplName, data), true);
			mailSender.send(msg);
			return true;
		} catch ( Exception e ) {
			logger.error("send mail error.", e);
		}
		return false;
	}


	/**
	 * <p>
	 * velocity 模板转 html
	 * </p>
	 * 
	 * @param tplName
	 * 				模板文件名称
	 * @param data
	 * 				参数
	 * @return
	 */
	public String getHtmltext( String tplName, Map<String, Object> data ) {
		return VelocityEngineUtils.mergeTemplateIntoString(this.velocityEngine, tplName, ENCODING, data);
	}


	public void setVelocityEngine( VelocityEngine velocityEngine ) {
		this.velocityEngine = velocityEngine;
	}


	public void setMailSender( JavaMailSender mailSender ) {
		this.mailSender = mailSender;
	}


	public String getEncoding() {
		return encoding;
	}


	public void setEncoding( String encoding ) {
		this.encoding = encoding;
	}


	public String getMailTitle() {
		return mailTitle;
	}


	public void setMailTitle( String mailTitle ) {
		this.mailTitle = mailTitle;
	}


	public String getMailFrom() {
		return mailFrom;
	}


	public void setMailFrom( String mailFrom ) {
		this.mailFrom = mailFrom;
	}

}
