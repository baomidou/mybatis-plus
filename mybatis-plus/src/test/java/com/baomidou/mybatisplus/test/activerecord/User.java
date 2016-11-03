package com.baomidou.mybatisplus.test.activerecord;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.activerecord.Model;

public class User extends Model<User> {
	private static final long serialVersionUID = 1L;
	public String name;
	public String email;
	public String password;

	@Override
	protected Class<User> classType() {
		return User.class;
	}

	public List<Map<String, Object>> getTasks() {
		return new Task().where("user_id = ?", id);
	}

}