package com.baomidou.mybatisplus.generator.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

public class ClassAnnotationAttributesTest {

    @Test
    void test() {
        var classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class);
        Assertions.assertNull(classAnnotationAttributes.getDisplayNameFunction());
        Assertions.assertEquals("@TableName", classAnnotationAttributes.getDisplayName());
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName()));

        classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class,(tableInfo -> "@Test"));
        classAnnotationAttributes.handleDisplayName(Mockito.mock(TableInfo.class));
        Assertions.assertNotNull(classAnnotationAttributes.getDisplayNameFunction());
        Assertions.assertEquals("@Test", classAnnotationAttributes.getDisplayName());
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName()));

        classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class,(tableInfo -> "@Test"), "com.baomidou.test");
        classAnnotationAttributes.handleDisplayName(Mockito.mock(TableInfo.class));
        Assertions.assertNotNull(classAnnotationAttributes.getDisplayNameFunction());
        Assertions.assertEquals("@Test", classAnnotationAttributes.getDisplayName());
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName()));
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test"));

        classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class, "@Test");
        Assertions.assertNull(classAnnotationAttributes.getDisplayNameFunction());
        Assertions.assertEquals("@Test", classAnnotationAttributes.getDisplayName());
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName()));

        classAnnotationAttributes = new ClassAnnotationAttributes(TableName.class, "@Test", "com.baomidou.test");
        Assertions.assertNull(classAnnotationAttributes.getDisplayNameFunction());
        Assertions.assertEquals("@Test", classAnnotationAttributes.getDisplayName());
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains(TableName.class.getName()));
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test"));

        classAnnotationAttributes = new ClassAnnotationAttributes("@Test", "com.baomidou.test");
        Assertions.assertNull(classAnnotationAttributes.getDisplayNameFunction());
        Assertions.assertEquals("@Test", classAnnotationAttributes.getDisplayName());
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test"));

        classAnnotationAttributes = new ClassAnnotationAttributes("com.baomidou.test", (tableInfo -> "@Test"));
        classAnnotationAttributes.handleDisplayName(Mockito.mock(TableInfo.class));
        Assertions.assertNotNull(classAnnotationAttributes.getDisplayNameFunction());
        Assertions.assertEquals("@Test", classAnnotationAttributes.getDisplayName());
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test"));

        classAnnotationAttributes = new ClassAnnotationAttributes(Set.of("com.baomidou.test1","com.baomidou.test2"), (tableInfo -> "@Test"));
        classAnnotationAttributes.handleDisplayName(Mockito.mock(TableInfo.class));
        Assertions.assertNotNull(classAnnotationAttributes.getDisplayNameFunction());
        Assertions.assertEquals("@Test", classAnnotationAttributes.getDisplayName());
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test1"));
        Assertions.assertTrue(classAnnotationAttributes.getImportPackages().contains("com.baomidou.test2"));
    }

}
