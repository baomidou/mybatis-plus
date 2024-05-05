/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;

import java.io.*;

/**
 * 当前类是 {@link java.lang.invoke.SerializedLambda } 的一个镜像
 * <p>
 * Create by hcl at 2020/7/17
 */
@SuppressWarnings("ALL")
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

    public static SerializedLambda extract(Serializable serializable) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(serializable);
            oos.flush();
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    Class<?> clazz = super.resolveClass(desc);
                    return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
                }

            }) {
                return (SerializedLambda) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new MybatisPlusException(e);
        }
    }

    public String getInstantiatedMethodType() {
        return instantiatedMethodType;
    }

    public Class<?> getCapturingClass() {
        return capturingClass;
    }

    public String getImplMethodName() {
        return implMethodName;
    }
}
