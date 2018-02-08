package com.baomidou.mybatisplus.test.h2.entity.persistent;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/26
 */
@Data
@Accessors(chain = true)
@TableName("h2user")
@KeySequence("SEQ_TEST")
public class H2UserSeq {

}
