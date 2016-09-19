package com.baomidou.mybatisplus.test.framework;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.baomidou.mybatisplus.test.framework.service.IUserService;

/*
 * Retrieve user with ID=3: GET http://localhost:8080/user/3
Save a new User: POST http://localhost:8080/user
Update User: PUT http://localhost:8080/user
Delete use with ID=3: DELETE http://localhost:8080/user/3

 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-test-context.xml", "classpath:mockTestBeans.xml" })
public class UserControllerTest {

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;

	@Spy
	private IUserService userService;

	@Before
	public void setup() {
		// this must be called for the @Mock annotations above to be processed
		// and for the mock service to be injected into the controller under
		// test.
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	// @Test
	// public void testGet() throws Exception {
	//
	// int userId = 3;
	//
	// mockMvc.perform(
	// get("/user/" + userId))
	// .andExpect(MockMvcResultMatchers.status().isOk())
	// .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	// .andExpect(jsonPath("data.age", is(15)))
	// .andExpect(jsonPath("data.firstName", is("bob")))
	// .andExpect(jsonPath("data.id", is(userId)))
	// .andExpect(jsonPath("success", is(true)));
	// }
	//
	// @Test
	// public void testSave() throws Exception {
	// mockMvc.perform(
	// post("/user")
	// .contentType(TestUtil.APPLICATION_JSON_UTF8)
	// .content(TestUtil.convertObjectToJsonBytes(new User()))
	// )
	// .andExpect(MockMvcResultMatchers.status().isOk())
	// .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	// .andExpect(jsonPath("success", is(true)));
	// }
	//
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
