package com.baomidou.mybatisplus.test.h2.issues.genericid.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
class SuperEntity<ID extends Serializable> {

    @TableId(type = IdType.ASSIGN_ID)
    private ID id;

}
