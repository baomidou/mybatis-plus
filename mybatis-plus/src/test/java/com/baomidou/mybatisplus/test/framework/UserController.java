package com.baomidou.mybatisplus.test.framework;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.test.framework.service.IUserService;
import com.baomidou.mybatisplus.test.mysql.entity.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private IUserService userService;

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public RestResponse<User> getUser(@PathVariable("id") Long id) {
		User user = userService.selectById(id);
		return retRestResponse(user);
	}

	//
	// @ResponseBody
	// @RequestMapping(method = RequestMethod.POST, produces =
	// "application/json")
	// public RestResponse saveUser(@RequestBody User user) {
	// userService.saveUser(user);
	// return new RestResponse<String>(true, "", null);
	// }
	//
	// @ResponseBody
	// @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces
	// = "application/json")
	// public RestResponse deleteUser(@PathVariable("id") int id) {
	// userService.deleteUser(id);
	// return new RestResponse<String>(true, "", null);
	// }
	//
	// @ResponseBody
	// @RequestMapping(method = RequestMethod.PUT, produces =
	// "application/json")
	// public RestResponse updateUser(@RequestBody User user) {
	// userService.updateUser(user);
	// return new RestResponse<String>(true, "", null);
	// }

	public RestResponse<User> retRestResponse() {
		return retRestResponse(null);
	}

	public RestResponse<User> retRestResponse(User user) {
		return retRestResponse("", user);
	}

	public RestResponse<User> retRestResponse(String message, User user) {
		return new RestResponse<User>(true, message, user);
	}

}
