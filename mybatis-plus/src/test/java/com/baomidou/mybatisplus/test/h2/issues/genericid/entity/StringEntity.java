package com.baomidou.mybatisplus.test.h2.issues.genericid.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_i171cq_string")
@EqualsAndHashCode(callSuper = true)
public class StringEntity extends SuperEntity<String> {

    private String name;

}
