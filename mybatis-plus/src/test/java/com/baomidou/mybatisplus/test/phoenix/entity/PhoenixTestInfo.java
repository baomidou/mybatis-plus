package com.baomidou.mybatisplus.test.phoenix.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fly
 * @date 2019/5/22 17:52
 * description: phoenix hbase TEST_INFO table
 */
@Data
@Accessors(chain = true)
@TableName("test_info")
public class PhoenixTestInfo {

    /**
     * Phoenix主键Mybatis不支持自增，需要插入传入或者设置key generator
     */
    private Integer id;

    private String name;
    private String phone;
    private String position;
    private String department;
    private String company;
    private String fileName;
    private String posDepCom;
}
