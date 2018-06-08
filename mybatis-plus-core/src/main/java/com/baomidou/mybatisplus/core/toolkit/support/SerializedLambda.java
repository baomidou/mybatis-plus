package com.baomidou.mybatisplus.core.toolkit.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;

import lombok.Getter;

/**
 * 这个类是从 {@link java.lang.invoke.SerializedLambda} 里面 copy 过来的
 * 字段信息完全一样
 * <p>
 * 负责将一个支持序列的 Function 序列化为 SerializedLambda
 *
 * @author HCL
 * @since 2018/05/10
 */
@Getter
public class SerializedLambda implements Serializable {

    private static final long serialVersionUID = 8025925345765570181L;
    private Class<?> capturingClass;
    private String functionalInterfaceClass;
    private String functionalInterfaceMethodName;
    private String functionalInterfaceMethodSignature;
    private String implClass;
    private String implMethodName;
    private String implMethodSignature;
    private int implMethodKind;
    private String instantiatedMethodType;
    private Object[] capturedArgs;

    /**
     * 通过反序列化转换 class
     *
     * @param lambda lambda对象
     * @return 返回解析后的 SerializedLambda
     */
    public static SerializedLambda convert(Property lambda) {
        byte[] bytes = SerializationUtils.serialize(lambda);
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(bytes)) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                Class<?> clazz = super.resolveClass(objectStreamClass);
                return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
            }
        }) {
            return (SerializedLambda) objIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new MybatisPlusException("This is impossible to happen", e);
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(super.toString())
            .append(implClass.replace("/", "."))
            .append("#").append(implMethodName).toString();
    }
}
