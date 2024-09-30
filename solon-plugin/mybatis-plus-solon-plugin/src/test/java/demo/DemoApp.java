package demo;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import demo.dso.service.UserService;
import demo.model.User;
import org.noear.solon.Solon;

import java.util.List;

/**
 * @author noear 2021/7/12 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);

        //test
        UserService userService = Solon.context().getBean(UserService.class);

        Assert.notNull(userService.getUserList(), "查询结果异常");
    }
}
