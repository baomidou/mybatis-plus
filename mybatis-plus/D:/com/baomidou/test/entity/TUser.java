package com.baomidou.test.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
/**
 * <p>
 * 
 * </p>
 *
 * @author Yanghu
 * @since 2017-01-11
 */
@TableName("t_user")
public class TUser extends Model<TUser> {

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long userid;
	/**
	 * 
	 */
	private String username;
	/**
	 * 
	 */
	private Integer age;


	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	protected Serializable pkVal() {
		return this.userid;
	}

}
