package smoke;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> byAge(int age);

    Map<String, Object> asMap(long id);

    Set<String> names();
}
