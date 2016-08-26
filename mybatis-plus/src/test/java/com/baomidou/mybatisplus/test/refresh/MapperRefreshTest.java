package com.baomidou.mybatisplus.test.refresh;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.refresh.MapperRefresh;
import com.baomidou.mybatisplus.test.mysql.MySqlInjector;
import com.baomidou.mybatisplus.test.mysql.UserMapper;
import com.baomidou.mybatisplus.test.mysql.UserMapperTest;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nieqiurong on 2016/8/26 6:36.
 */
public class MapperRefreshTest {
    public static void main(String[] args) throws IOException, InterruptedException {

        InputStream in = UserMapperTest.class.getClassLoader().getResourceAsStream("mysql-config.xml");
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
        mf.setSqlInjector(new MySqlInjector());
        Resource[] resource = new ClassPathResource[]{new ClassPathResource("mysql/UserMapper.xml")};
        SqlSessionFactory sessionFactory = mf.build(in);
        new MapperRefresh(resource,sessionFactory,0,5);
        boolean isReturn = false;
        SqlSession session=null;
        while (!isReturn){
            try {
                session = sessionFactory.openSession();
                UserMapper userMapper = session.getMapper(UserMapper.class);
                userMapper.selectListRow(new Pagination(1,10));
                resource[0].getFile().setLastModified(System.currentTimeMillis());
                session.commit();
                session.close();
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
//                isReturn = true;
            }finally {
                if(session!=null)session.close();
                Thread.sleep(5000);
            }
        }
        System.exit(0);
    }
}
