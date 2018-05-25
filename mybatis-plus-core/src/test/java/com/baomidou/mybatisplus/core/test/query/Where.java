package com.baomidou.mybatisplus.core.test.query;


public class Where implements ISqlSegment {

   private Condition condition;

   public Where(Condition condition) {
      this.condition = condition;
   }

    @Override
    public String sqlSegment() {
        return String.format("WHERE %s", condition.sqlSegment());
   }
}
