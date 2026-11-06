package smoke;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
    CommandLineRunner smoke(UserMapper mapper) {
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
            System.out.println("SMOKE OK");
        };
    }

    private static void check(boolean ok, String what) {
        if (!ok) {
            throw new IllegalStateException("SMOKE FAILED: " + what);
        }
    }
}
