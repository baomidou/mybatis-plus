package com.baomidou.mybatisplus.test.activerecord;

import com.baomidou.mybatisplus.activerecord.DB;
import com.baomidou.mybatisplus.activerecord.Query;

/**
 * Created by Caratacus on 2016/10/11 0011.
 */
public class ActiveRecord {
    public static void main(String[] args) {
        DB db = DB.open("jdbc:mysql://localhost/mybatis-plus", "root", "521");
        Query where = db.active("user").select().where("1=1");
    }
}
