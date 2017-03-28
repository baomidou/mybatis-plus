package com.baomidou.mybatisplus.test.plugin;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.TimestampVersionUser;


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
		User user = userService.selectById(11);//if cacheEnabled=true, this object will be cached
		Assert.assertEquals(1, user.getAge().intValue());
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setAge(2);
		userService.updateById(updateUser);
		
		user = userService.selectById(11);
		Assert.assertEquals(2, user.getAge().intValue());
		Assert.assertEquals(1, user.getVersion().intValue());
		
		User user2 = userService.selectById(12);
		updateUser = new User();
		updateUser.setAge(99);
		updateUser.setPrice(new BigDecimal("9.99"));
		userService.updateById(updateUser);
		user2 = userService.selectById(12);
		Assert.assertEquals(1, user2.getVersion().intValue());//test if version is cached in plugin [OK]
	}
	
	@Test
	public void testUpdateWithVersionControl(){
		long userId = 12;
		User user = userService.selectById(userId);
		Assert.assertEquals(1, user.getVersion().intValue());
		User updateUser = new User();
		updateUser.setId(userId);
		updateUser.setAge(3);
		updateUser.setVersion(user.getVersion());
		userService.updateById(updateUser);
		user = userService.selectById(userId);
		
		Assert.assertEquals(3, user.getAge().intValue());
		Assert.assertEquals(2, user.getVersion().intValue());
		
		User where = new User();
		where.setId(userId);
//		updateUser = new User();
//		updateUser.setId(userId);
//		updateUser.setAge(3);
//		updateUser.setVersion(user.getVersion());
		userService.update(user, new EntityWrapper<User>(where));
		user = userService.selectById(userId);
		Assert.assertEquals(3, user.getAge().intValue());
		Assert.assertEquals(3, user.getVersion().intValue());
	}
	
	@Test
	public void testTimestampVersionControl(){
		timeVersionMapper.deleteById(1L);
		TimestampVersionUser user = new TimestampVersionUser();
		user.setId(1L);
		user.setName("name1");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		user.setVersion(new Timestamp(new Date().getTime()));
		timeVersionMapper.insert(user);
		
		cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -1);
		Date date = cal.getTime();
		System.err.println("date="+date);
		int compare = user.getVersion().compareTo(date);
		Assert.assertTrue(compare<0);
		
		TimestampVersionUser updateUser = new TimestampVersionUser();
		updateUser.setId(1L);
		updateUser.setName("updname");
		updateUser.setVersion(user.getVersion());
		
		timeVersionMapper.updateById(updateUser);
		user = timeVersionMapper.selectById(1L);
		Date version = user.getVersion();
		System.err.println("after update: version="+version);
		compare = version.compareTo(date);
		System.err.println("compare="+compare);
		Assert.assertTrue(compare>0);
	}
	
}
