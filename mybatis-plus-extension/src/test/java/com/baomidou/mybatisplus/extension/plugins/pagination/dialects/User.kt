package com.baomidou.mybatisplus.extension.plugins.pagination.dialects

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName

@TableName("sys_user")
class User {

    var id: Int? = null

    @TableField("username")
    var name: String? = null

    var roleId: Int? = null
}
