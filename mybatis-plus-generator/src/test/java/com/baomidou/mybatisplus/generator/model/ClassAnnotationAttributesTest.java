package com.baomidou.mybatisplus.generator.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassAnnotationAttributesTest {

    @Test
    void test() {
        var classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class);
        assertThat(classAnnotationAttributes.getDisplayNameFunction()).isNull();
        assertThat(classAnnotationAttributes.getDisplayName()).isEqualTo("@TableName");
        assertThat(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName())).isTrue();

        classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class, (tableInfo -> "@Test"));
        classAnnotationAttributes.handleDisplayName(Mockito.mock(TableInfo.class));
        assertThat(classAnnotationAttributes.getDisplayNameFunction()).isNotNull();
        assertThat(classAnnotationAttributes.getDisplayName()).isEqualTo("@Test");
        assertThat(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName())).isTrue();

        classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class, (tableInfo -> "@Test"),
                "com.baomidou.test");
        classAnnotationAttributes.handleDisplayName(Mockito.mock(TableInfo.class));
        assertThat(classAnnotationAttributes.getDisplayNameFunction()).isNotNull();
        assertThat(classAnnotationAttributes.getDisplayName()).isEqualTo("@Test");
        assertThat(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName())).isTrue();
        assertThat(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test")).isTrue();

        classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class, "@Test");
        assertThat(classAnnotationAttributes.getDisplayNameFunction()).isNull();
        assertThat(classAnnotationAttributes.getDisplayName()).isEqualTo("@Test");
        assertThat(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName())).isTrue();

        classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class, "@Test", "com.baomidou.test");
        assertThat(classAnnotationAttributes.getDisplayNameFunction()).isNull();
        assertThat(classAnnotationAttributes.getDisplayName()).isEqualTo("@Test");
        assertThat(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName())).isTrue();
        assertThat(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test")).isTrue();

        classAnnotationAttributes = new ClassAnnotationAttributes("@Test", "com.baomidou.test");
        assertThat(classAnnotationAttributes.getDisplayNameFunction()).isNull();
        assertThat(classAnnotationAttributes.getDisplayName()).isEqualTo("@Test");
        assertThat(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test")).isTrue();

        classAnnotationAttributes = new ClassAnnotationAttributes("com.baomidou.test", (tableInfo -> "@Test"));
        classAnnotationAttributes.handleDisplayName(Mockito.mock(TableInfo.class));
        assertThat(classAnnotationAttributes.getDisplayNameFunction()).isNotNull();
        assertThat(classAnnotationAttributes.getDisplayName()).isEqualTo("@Test");
        assertThat(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test")).isTrue();

        classAnnotationAttributes = new ClassAnnotationAttributes(Set.of("com.baomidou.test1", "com.baomidou.test2"),
                (tableInfo -> "@Test"));
        classAnnotationAttributes.handleDisplayName(Mockito.mock(TableInfo.class));
        assertThat(classAnnotationAttributes.getDisplayNameFunction()).isNotNull();
        assertThat(classAnnotationAttributes.getDisplayName()).isEqualTo("@Test");
        assertThat(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test1")).isTrue();
        assertThat(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test2")).isTrue();
    }

}
