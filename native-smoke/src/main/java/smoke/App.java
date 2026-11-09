package smoke;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    SmokeInterceptor smokeInterceptor() {
        return new SmokeInterceptor();
    }

    @Bean
    CommandLineRunner smoke(UserMapper mapper, SqlSessionFactory sqlSessionFactory) {
        return args -> {
            check(mapper.selectById(1L).getName().equals("tom"), "selectById");
            List<User> lambda = mapper.selectList(new LambdaQueryWrapper<User>().ge(User::getAge, 20));
            check(lambda.size() == 2, "LambdaQueryWrapper");
            List<User> plain = mapper.selectList(new QueryWrapper<User>().eq("name", "tom"));
            check(plain.size() == 1, "QueryWrapper");
            int updated = mapper
                    .update(null, new LambdaUpdateWrapper<User>().set(User::getAge, 99).eq(User::getName, "tom"));
            check(updated == 1 && mapper.selectById(1L).getAge() == 99, "LambdaUpdateWrapper");
            check(mapper.byAge(30).size() == 1, "xml list");
            Map<String, Object> map = mapper.asMap(2L);
            check(map != null && !map.isEmpty(), "xml map");
            Set<String> names = mapper.names();
            check(names.size() == 3, "xml set");
            mapper.insert(List.of(user(4L, "neo", 40), user(5L, "trinity", 41)));
            check(mapper.selectById(4L) != null && mapper.selectById(5L) != null, "batch insert");
            check(SmokeInterceptor.INVOCATIONS.get() > 0, "interceptor plugin invoked");
            Cache cache = sqlSessionFactory.getConfiguration().getCache("smoke.UserMapper");
            check(cache != null && cache.getSize() == 1, "cache size property");
            System.out.println("SMOKE OK");
        };
    }

    private static User user(long id, String name, int age) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setAge(age);
        return user;
    }

    private static void check(boolean ok, String what) {
        if (!ok) {
            throw new IllegalStateException("SMOKE FAILED: " + what);
        }
    }
}
