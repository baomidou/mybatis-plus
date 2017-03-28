package com.baomidou.mybatisplus.test.plugin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.test.mysql.service.IUserService;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.TimestampVersionUserMapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;

public abstract class UserTestBase {

	@Autowired
	protected IUserService userService;
	
	@Autowired
	protected TimestampVersionUserMapper timeVersionMapper;
	
	@Before
	public void init(){
//		cleanData();
		User userA = new User();
		userA.setId(IdWorker.getId());
		userA.setName("junyu_shi");
		userA.setAge(1);
		userA.setDesc("测试一把");
		userA.setTestType(1);
		userA.setVersion(1);
		userService.insert(userA);
		long id = IdWorker.getId();
		userA = new User(id, "abc", 18, 1);
		userA.setVersion(1);
		userService.insert(userA);
		userA = new User(18);
		userA.setVersion(1);
		userService.insert(userA);
		
		List<User> ul = new ArrayList<User>();
		/* 手动输入 ID */
		ul.add(new User(11L, "1", 1, 0));
		ul.add(new User(12L, "2", 2, 1));
		ul.add(new User(13L, "3", 3, 1));
		ul.add(new User(14L, "delname", 4, 0));
		ul.add(new User(15L, "5", 5, 1));
		ul.add(new User(16L, "6", 6, 0));
		/* 测试 name test_type 填充 */
		ul.add(new User(17L, 7));
		ul.add(new User(18L, 8));
		ul.add(new User(19L, 9));
		ul.add(new User(7));
		ul.add(new User(20L, "deleteByMap", 7, 0));
		/* 使用 ID_WORKER 自动生成 ID */
		ul.add(new User("8", 8, 1));
		ul.add(new User("9", 9, 1));
		for (User u : ul) {
			u.setVersion(1);
			u.setPrice(new BigDecimal("99.99"));
			userService.insert(u);
		}
	}
	
	@After
	public void cleanData(){
		User user = new User();
		user.setDesc(null);
		EntityWrapper<User> ew = new EntityWrapper<User>(user);
		ew.and("1=1");
		userService.delete(ew);
	}
	
}
