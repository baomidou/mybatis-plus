package com.baomidou.mybatisplus.test.plugin;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.mysql.entity.User;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-test.xml" })
public class OptimisticLockerInterceptorJUnitTest extends UserTestBase{
	
	@Test
	public void testSelectWithDecimalData(){
		User user = userService.selectById(11);
		System.err.println(user);
		Assert.assertNotNull(user);
		Assert.assertEquals(new BigDecimal("99.99"), user.getPrice());
	}
	
	@Test
	public void testUpdateWithoutVersionControl(){
		User user = userService.selectById(11);
		Assert.assertEquals(1, user.getAge().intValue());
		user.setVersion(null);
		user.setAge(2);
		userService.updateById(user);
		user = userService.selectById(11);
		Assert.assertEquals(2, user.getAge().intValue());
		Assert.assertEquals(1, user.getVersion().intValue());
	}
	
	@Test
	public void testUpdateWithVersionControl(){
		long userId = 12;
		User user = userService.selectById(userId);
		Assert.assertEquals(2, user.getAge().intValue());
		user.setAge(3);
		userService.updateById(user);
		User where = new User();
		where.setId(userId);
		userService.update(user, new EntityWrapper<User>(where));
		user = userService.selectById(userId);
		Assert.assertEquals(3, user.getAge().intValue());
		Assert.assertEquals(2, user.getVersion().intValue());
	}
	
}
