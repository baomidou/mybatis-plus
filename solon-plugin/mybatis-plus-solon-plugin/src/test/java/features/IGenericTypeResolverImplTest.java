package features;


import com.baomidou.mybatisplus.core.mapper.Mapper;
import demo.dso.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.GenericUtil;

import java.util.Arrays;
import java.util.Map;

public class IGenericTypeResolverImplTest {


    @Test
    public void resolveTypeArguments() {
        System.out.println(Arrays.toString(GenericUtil.resolveTypeArguments(DemoImpl.class, Map.class)));
        System.out.println(Arrays.toString(GenericUtil.resolveTypeArguments(DemoImpl.class, Demo.class)));

        System.out.println(Arrays.toString(GenericUtil.resolveTypeArguments(UserMapper.class, Mapper.class)));
    }

    private interface Demo<T> {}
    private abstract static class DemoImpl implements Map<Integer, String>, IGenericTypeResolverImplTest.Demo<Double> {

    }
}