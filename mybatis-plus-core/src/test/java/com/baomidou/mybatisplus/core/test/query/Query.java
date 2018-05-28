package com.baomidou.mybatisplus.core.test.query;

import java.util.function.Function;


public class Query implements ISqlSegment {

   private final SqlStatement statement;

   public Query() {
      this.statement = new SqlStatement();
   }

   /**
    * Helper method to enqueue stringable and return self
    * @param stringable to enqueue
    * @return this
    */
   private Query add(ISqlSegment stringable) {
      statement.enqueue(stringable);
      return this;
   }

   /**
    * WHERE expression
    * @param expression to
    * @return this
    */
   public Query where(String expression) {
      Where where = new Where(new Condition().apply(expression));
      return add(where);
   }

   /**
    * WHERE expression [and, or]
    * @param condition function to populate where predicate
    * @return this
    */
   public Query where(String expression, Function<Condition, Condition> condition) {
      Where where = new Where(condition.apply(new Condition().apply(expression)));
      return add(where);
   }

    @Override
    public String sqlSegment() {
        return statement.sqlSegment();
   }
}
