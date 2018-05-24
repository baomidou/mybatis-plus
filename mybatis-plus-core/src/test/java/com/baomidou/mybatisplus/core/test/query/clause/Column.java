package com.baomidou.mybatisplus.core.test.query.clause;


public class Column {

   private String name;

   public Column(String name) {
      this.name = name;
   }

   public Column eq(String anotherColumn) {
      return this;
   }

   public Column greaterThan(String anotherColumn) {
      return this;
   }
}
