package com.baomidou.enjoy.entity;

import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId

import java.time.LocalDateTime
import java.io.Serializable

/**
 * <p>
 * 测试表
 * </p>
 *
 * @author baomidou
 * @since 2025-03-27
 */
@TableName("T_SIMPLE")
class Simple : Serializable {

    /**
     * id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    var id: Int? = null

    /**
     * 姓名
     */
    var name: String? = null

    /**
     * 年龄
     */
    var age: Int? = null

    /**
     * 删除标识1
     */
    var deleteFlag: Byte? = null

    /**
     * 删除标识2
     */
    var deleted: Byte? = null

    /**
     * 测试布尔类型
     */
    var isOk: Byte? = null

    /**
     * 版本
     */
    var version: Long? = null

    /**
     * 创建时间
     */
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    var updateTime: LocalDateTime? = null

    override fun toString(): String {
        return "Simple{" +
        "id=" + id +
        ", name=" + name +
        ", age=" + age +
        ", deleteFlag=" + deleteFlag +
        ", deleted=" + deleted +
        ", isOk=" + isOk +
        ", version=" + version +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}"
    }
}
