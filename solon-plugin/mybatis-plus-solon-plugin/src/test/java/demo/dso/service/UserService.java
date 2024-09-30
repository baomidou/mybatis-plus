package demo.dso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import demo.dso.mapper.UserMapper;
import demo.model.User;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * @author noear 2021/9/9 created
 */
@Component
public class UserService {
    @Inject
    UserMapper userMapper;

    public List<User> getUserList() {
        assert userMapper != null;

        return userMapper.selectList(new QueryWrapper<>());
    }
}
