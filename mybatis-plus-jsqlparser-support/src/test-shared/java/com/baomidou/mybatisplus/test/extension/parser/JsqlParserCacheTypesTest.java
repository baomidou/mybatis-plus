/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.test.extension.parser;

import com.baomidou.mybatisplus.extension.parser.JsqlParserCacheTypes;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class JsqlParserCacheTypesTest {

    @Test
    void fstRegistersAllSerializableClasses() {
        assertRegisteredTypes(false);
    }

    @Test
    void furyAndForyRegisterAllSerializableClassesAndEnums() {
        assertRegisteredTypes(true);
    }

    private void assertRegisteredTypes(boolean includeEnums) {
        Set<String> expected;
        try (ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages("net.sf.jsqlparser").scan()) {
            expected = scanResult.getAllClasses().stream().filter(
                    info -> !info.isInterface()
                            && (info.implementsInterface(Serializable.class) || (includeEnums && info.isEnum())))
                    .map(ClassInfo::getName).collect(Collectors.toSet());
        }
        assertThat(expected).as("No serializable JSqlParser classes found").isNotEmpty();

        List<Class<?>> registered = new ArrayList<>();
        if (includeEnums) {
            JsqlParserCacheTypes.registerFuryAndFory(registered::add);
        } else {
            JsqlParserCacheTypes.registerFst(registered::add);
        }
        assertThat(registered).doesNotHaveDuplicates();
        assertThat(registered.stream().map(Class::getName).collect(Collectors.toSet())).isEqualTo(expected);
    }
}
