package com.baomidou.mybatisplus.core.test.query.clause;


import static java.lang.String.format;
import static java.lang.String.join;

import com.baomidou.mybatisplus.core.test.query.ISqlSegment;
import com.baomidou.mybatisplus.core.test.query.predicate.Table;


public class From implements ISqlSegment {

   private String[] tables;
   private Table table;

   public From(String[] tables) {
      this.tables = tables;
   }

   public void joins(Table table) {
      this.table = table;
   }

    @Override
    public String sqlSegment() {
      String sql = format("FROM %s", join(", ", (CharSequence[]) tables));

      if (table != null) {
         sql = sql
            .concat(" ")
            .concat(table.sqlSegment());
      }

      return sql;
   }
}
