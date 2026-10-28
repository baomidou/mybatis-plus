package com.baomidou.mybatisplus.test.h2

import org.junit.jupiter.api.Tag

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.apache.ibatis.reflection.MetaObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

/**
 *
 * @author nieqiurong
 */
@Tag("integration")
class MetaObjectHandlerTest {

    /**
     * 特征化 strictInsertFill 的类型闸门：实体属性在字节码里是包装类型，
     * 传原始类型的 Class 匹配不上会被静默跳过，只有 javaObjectType 才填得进去。
     */
    @Test
    fun strictInsertFillOnlyAcceptsBoxedClass() {
        assertThat(Long::class.java).describedAs("原始 long.class 和实体属性的 java.lang.Long 是两个不同的 Class")
            .isNotEqualTo(Long::class.javaObjectType)
        val demo = Demo()
        fillHandler().insertFill(metaObjectOf(demo))
        assertThat(demo.testLong).describedAs("按原始类型 Class 匹配不到属性，既不报错也不填").isNull()
        assertThat(demo.testInt).describedAs("换成包装类型就填得上").isEqualTo(123)
    }

    /**
     * 特征化注解了 INSERT_UPDATE 的字段都会走 insertFill，且已有值不被覆盖。
     */
    @Test
    fun strictInsertFillWritesEveryAnnotatedField() {
        val demo = Demo().apply { testString = "原有值" }
        fillHandler().insertFill(metaObjectOf(demo))
        assertThat(demo.testString).describedAs("有值不覆盖").isEqualTo("原有值")
        assertThat(demo.testDate).isNotNull()
        assertThat(demo.testLocalDateTime).isNotNull()
        assertThat(demo.testLocalDate).isNotNull()
        assertThat(demo.testBoolean).isFalse()
    }

    private fun metaObjectOf(demo: Demo): MetaObject {
        val configuration = MybatisConfiguration()
        TableInfoHelper.initTableInfo(MapperBuilderAssistant(configuration, ""), Demo::class.java)
        return configuration.newMetaObject(demo)
    }

    private fun fillHandler(): MetaObjectHandler = object : MetaObjectHandler {
        override fun insertFill(metaObject: MetaObject) {
            this.strictInsertFill(metaObject, "testString", String::class.java, "123")
            this.strictInsertFill(metaObject, "testInt", Int::class.javaObjectType, 123)
            this.strictInsertFill(metaObject, "testLocalDateTime", LocalDateTime::class.java, LocalDateTime.now())
            this.strictInsertFill(metaObject, "testBoolean", Boolean::class.javaObjectType, false)
            this.strictInsertFill(metaObject, "testDate", Date::class.java, Date())
            this.strictInsertFill(metaObject, "testLocalDate", LocalDate::class.java, LocalDate.now())
            // 故意用原始类型 long.class，用例要靠它演示填充被静默跳过
            this.strictInsertFill(metaObject, "testLong", Long::class.java, 123456L)
        }

        override fun updateFill(metaObject: MetaObject) {
        }
    }

}

@Tag("integration")
class Demo {

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var testString: String? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var testInt: Int? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var testLong: Long? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var testDate: Date? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var testLocalDateTime: LocalDateTime? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var testBoolean: Boolean? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var testLocalDate: LocalDate? = null

    override fun toString(): String {
        return "Demo(testBoolean=$testBoolean, testString=$testString, testInt=$testInt, testLong=$testLong, testDate=$testDate, testLocalDateTime=$testLocalDateTime, testLocalDate=$testLocalDate)"
    }

}
