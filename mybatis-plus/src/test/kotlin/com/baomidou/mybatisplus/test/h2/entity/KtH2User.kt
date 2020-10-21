package com.baomidou.mybatisplus.test.h2.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.annotation.Version
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum
import java.math.BigDecimal
import java.util.*

@TableName("h2user")
class KtH2User {

    var name: String? = null

    var age: AgeEnum? = null

    var price: BigDecimal? = null

    var testType: Int? = null

    @TableField("`desc`")
    var desc: String? = null

    @TableField(select = false)
    var testDate: Date? = null

    @Version
    var version: Int? = null

    @TableLogic
    val deleted: Int? = null

}
