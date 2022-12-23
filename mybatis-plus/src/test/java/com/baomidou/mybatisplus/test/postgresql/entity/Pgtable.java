package com.baomidou.mybatisplus.test.postgresql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Pgtable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String compName;
    private Integer age;

}
