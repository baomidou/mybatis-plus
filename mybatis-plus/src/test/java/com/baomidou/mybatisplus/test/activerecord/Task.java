package com.baomidou.mybatisplus.test.activerecord;

import com.baomidou.mybatisplus.activerecord.Model;

public class Task extends Model<Task> {
	private static final long serialVersionUID = 1L;
	public int user_id;
	public String description;
	public boolean done;

	private User user;


	@Override
	protected Class<Task> classType() {
		return Task.class;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		this.user_id = user.id;
	}

}
