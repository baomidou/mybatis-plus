package com.baomidou.mybatisplus.core.test.query.clause;

import com.baomidou.mybatisplus.core.test.query.ISqlSegment;
import com.baomidou.mybatisplus.core.test.query.predicate.Condition;


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
