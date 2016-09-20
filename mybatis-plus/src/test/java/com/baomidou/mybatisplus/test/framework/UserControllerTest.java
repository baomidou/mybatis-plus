package com.baomidou.mybatisplus.test.framework;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.baomidou.mybatisplus.test.framework.service.IUserService;
import com.baomidou.mybatisplus.test.mysql.entity.User;

/*
 * Retrieve user with ID=3: GET http://localhost:8080/user/3
 * Save a new User: POST http://localhost:8080/user
 * Update User: PUT http://localhost:8080/user
 * Delete use with ID=3: DELETE http://localhost:8080/user/3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mysql/spring-test-context.xml" })

/*
 * 事务配置<br>
 * DependencyInjectionTestExecutionListener 监听测试类中的依赖注入是否正确<br>
 * TransactionalTestExecutionListener 监听测试类中的事务<br>
 */
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserControllerTest {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;

	@Spy
	private IUserService userService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void testSave() throws Exception {
		mockMvc.perform(post("/user").contentType(APPLICATION_JSON_UTF8).content(new User().toString()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("success", is(true)));
	}

	@Test
	public void testGet() throws Exception {

		int userId = 3;

		mockMvc.perform(get("/user/" + userId)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("data.age", is(15)))
				.andExpect(jsonPath("data.firstName", is("bob"))).andExpect(jsonPath("data.id", is(userId)))
				.andExpect(jsonPath("success", is(true)));
	}

	// @Test
	// public void testUpdate() throws Exception {
	// mockMvc.perform(
	// put("/user")
	// .contentType(TestUtil.APPLICATION_JSON_UTF8)
	// .content(TestUtil.convertObjectToJsonBytes(new User())))
	// .andExpect(MockMvcResultMatchers.status().isOk())
	// .andExpect(jsonPath("success", is(true)));
	// }
	//
	// @Test
	// public void testDelete() throws Exception {
	// mockMvc.perform(
	// delete("/user/3"))
	// .andExpect(MockMvcResultMatchers.status().isOk())
	// .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	// .andExpect(jsonPath("success", is(true)));
	// }

}
