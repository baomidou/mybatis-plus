package com.baomidou.mybatisplus.test.activerecord;

import com.baomidou.mybatisplus.activerecord.DB;
import com.baomidou.mybatisplus.activerecord.Record;
import com.baomidou.mybatisplus.test.mysql.entity.Test;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

import java.util.List;

/**
 * Created by Caratacus on 2016/10/11 0011.
 */
public class ActiveRecordTest {
    public static void main(String[] args) {
        TableInfoHelper.initTableInfo(Test.class);
        DB db = DB.open("jdbc:mysql://localhost/mybatis-plus", "root", "521");
        List<Record> user = db.active("test").select().all();
        System.out.println(user);
    }
}
