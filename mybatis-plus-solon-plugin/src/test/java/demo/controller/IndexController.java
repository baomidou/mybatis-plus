package demo.controller;

import demo.dso.service.UserService;
import demo.model.User;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.util.List;

@Controller
public class IndexController {

    @Inject
    UserService userService;

    @Mapping("/")
    public List<User> index() {
        return userService.getUserList();
    }
}
