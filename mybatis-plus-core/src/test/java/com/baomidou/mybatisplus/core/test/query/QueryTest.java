package com.baomidou.mybatisplus.core.test.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class QueryTest {

    private void log(String message) {
        System.out.println(message);
    }

    @Test
    public void test() {
        String sql = new Query()
            .select("b.*")
            .from("bunnies b")
            .where("b.age > 18", condition ->
                condition.and("b.type = 'rabid'")
                    .or("b.abc=12"))
            .sqlSegment();

        log(sql);
    }

    @Test
    public void testBunnies() {

        String sql = new Query()
            .select("*")
            .from("bunnies")
            .sqlSegment();

        log(sql);
        assertEquals("SELECT * FROM bunnies", sql);
    }

    @Test
    public void testCrazyManlyBunnies() {

        String sql = new Query()
            .select("b.*")
            .from("bunnies b")
            .where("b.age > 18", condition ->
                condition.and("b.type = 'rabid'"))
            .sqlSegment();

        log(sql);
        assertEquals("SELECT b.* FROM bunnies b WHERE b.age > 18 AND b.type = 'rabid'", sql);
    }

    @Test
    public void testLigers() {

        String sql = new Query()
            .select("t.id", "l.id")
            .from("tigers t", table ->
                table.innerJoin("lions l").on("t.lion_id = l.id"))
            .sqlSegment();

        log(sql);
        assertEquals("SELECT t.id, l.id FROM tigers t INNER JOIN lions l ON t.lion_id = l.id", sql);
    }

    class Sloth {

        private Integer slownessLevel;

        public Sloth(Integer slownessLevel) {
            this.slownessLevel = slownessLevel;
        }

        public String repeat(String message) {

            StringBuilder toRepeat = new StringBuilder();
            char[] chars = message.toCharArray();

            for (char aChar : chars)
                if (aChar == ' ') toRepeat.append(aChar);
                else for (int j = 0; j < slownessLevel; j++)
                    toRepeat.append(aChar);

            return toRepeat.toString();
        }
    }

    @Test
    public void testSpecificSloth() {

        String sql = new Query()
            .select("s.*")
            .from("sloths s", "slowness n")
            .where("s.slowness_id = n.id", condition ->
                condition.and(nested ->
                    nested.apply("s.name = 'Kristen Bell'").or("n.level = 1.5")))
            .sqlSegment();

        log(sql);
        assertEquals("SELECT s.* FROM sloths s, slowness n WHERE s.slowness_id = n.id AND ( s.name = 'Kristen Bell' OR n.level = 1.5 )", sql);
    }

}
