package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link IEnum} 是枚举与数据库列之间唯一的接口约定：core 用 {@code isAssignableFrom} 判断走不走这条分支，<br>
 * 再反射调用 {@code getValue()} 取存储值。加抽象方法或放宽类型参数上界都会影响所有已存在的枚举实现。
 *
 * @author taoyu
 */
@Tag("unit")
class IEnumTest {

    @Test
    void valueIsTheOnlyContractMethod() {
        assertThat(Arrays.stream(IEnum.class.getDeclaredMethods()).map(Method::getName).toList())
                .as("再加一个抽象方法，所有已实现的枚举会集体编译失败；改名则会让已有实现静默失配").containsExactly("getValue");
    }

    @Test
    void storedValueTypeIsBoundedToSerializable() {
        TypeVariable<Class<IEnum>>[] parameters = IEnum.class.getTypeParameters();
        assertThat(parameters).as("只有一个类型参数，代表库里那一列的值类型").hasSize(1);
        assertThat(parameters[0].getBounds()).as("上界收成 Object 会把不可序列化的值送进 MyBatis 参数与缓存")
                .containsExactly(Serializable.class);
    }

    @Test
    void implementationIsRecognisedByTypeAndKeepsItsValueType() {
        IEnum<Integer> low = Level.LOW;
        assertThat(IEnum.class.isAssignableFrom(Level.class)).as("core 就靠这句决定用哪套取值方式").isTrue();
        assertThat(low.getValue()).as("接口方法拿到的就是枚举自己声明的存储值").isEqualTo(1);
        assertThat(IEnum.class.isAssignableFrom(Grade.class)).as("没实现接口的普通枚举走 @EnumValue 那条分支").isFalse();
    }

    enum Level implements IEnum<Integer> {

        LOW(1),
        HIGH(2);

        private final Integer value;

        Level(Integer value) {
            this.value = value;
        }

        @Override
        public Integer getValue() {
            return value;
        }
    }

    enum Grade {
        PRIMARY,
        SECONDARY;
    }

}
