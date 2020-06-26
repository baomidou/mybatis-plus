package com.baomidou.mybatisplus.test.plugins.pagination.optimize;

import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.github.pagehelper.parser.CountSqlParser;
import org.apache.ibatis.reflection.MetaObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2019-05-08
 */
class JsqlParserCountOptimizeTest {

    private JsqlParserCountOptimize parser = new JsqlParserCountOptimize(true);
    private MetaObject metaObject = null;

    @Test
    void parserLeftJoin() {
        /* 能进行优化的 SQL */
        assertThat(parser.parser(metaObject, "select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xx = ?").getSql())
            .isEqualTo("SELECT COUNT(1) FROM user u WHERE u.xx = ?");

        assertThat(parser.parser(metaObject, "select * from user LEFT JOIN role ON role.id = user.role_id WHERE user.xx = ?").getSql())
            .isEqualTo("SELECT COUNT(1) FROM user WHERE user.xx = ?");

        /* 不能进行优化的 SQL */
        assertThat(parser.parser(metaObject, "select * from user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? where u.xx = ?").getSql())
            .isEqualTo("SELECT COUNT(1) FROM user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? WHERE u.xx = ?");

        assertThat(parser.parser(metaObject, "select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?").getSql())
            .isEqualTo("SELECT COUNT(1) FROM user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?");

        /* 复杂sql */

        SqlInfo parser = this.parser.parser(metaObject, "select\n" +
            "IF(guideScenicSpotId IS NULL,voiceScenicSpotId,guideScenicSpotId) AS  scenicSpotId,\n" +
            "IF(guideStatisticalDate IS NULL,voiceStatisticalDate,guideStatisticalDate) AS  statisticalDate,\n" +
            "t.*\n" +
            "from (\n" +
            "SELECT g.scenicSpotId      AS guideScenicSpotId,\n" +
            "       g.orderType         AS guideOrderType,\n" +
            "       g.orderNum          AS guideOrderNum,\n" +
            "       g.totalGetAmount    AS guideTotalGetAmount,\n" +
            "       g.refundNum         AS guideRefundNum,\n" +
            "       g.totalRefundAmount AS guideTotalRefundAmount,\n" +
            "       g.statisticalDate   AS guideStatisticalDate,\n" +
            "       v.scenicSpotId      AS voiceScenicSpotId,\n" +
            "       v.orderType         AS voiceOrderType,\n" +
            "       v.orderNum          AS voiceOrderNum,\n" +
            "       v.totalGetAmount    AS voiceTotalGetAmount,\n" +
            "       v.refundNum         AS voiceRefundNum,\n" +
            "       v.totalRefundAmount AS voiceTotalRefundAmount,\n" +
            "       v.statisticalDate   AS voiceStatisticalDate\n" +
            "             FROM `v_guideOrder_day` g LEFT JOIN `v_voiceOrder_day` v ON g.`scenicSpotId` = v.`scenicSpotId`" +
            " AND g.`statisticalDate` = v.`statisticalDate`\n" +
            "             UNION ALL\n" +
            "             SELECT g.scenicSpotId      AS guideScenicSpotId,\n" +
            "                    g.orderType         AS guideOrderType,\n" +
            "                    g.orderNum          AS guideOrderNum,\n" +
            "                    g.totalGetAmount    AS guideTotalGetAmount,\n" +
            "                    g.refundNum         AS guideRefundNum,\n" +
            "                    g.totalRefundAmount AS guideTotalRefundAmount,\n" +
            "                    g.statisticalDate   AS guideStatisticalDate,\n" +
            "                    v.scenicSpotId      AS voiceScenicSpotId,\n" +
            "                    v.orderType         AS voiceOrderType,\n" +
            "                    v.orderNum          AS voiceOrderNum,\n" +
            "                    v.totalGetAmount    AS voiceTotalGetAmount,\n" +
            "                    v.refundNum         AS voiceRefundNum,\n" +
            "                    v.totalRefundAmount AS voiceTotalRefundAmount,\n" +
            "                    v.statisticalDate   AS voiceStatisticalDate\n" +
            "             FROM `v_guideOrder_day` g\n" +
            "                      RIGHT JOIN `v_voiceOrder_day` v\n" +
            "                                 ON g.`scenicSpotId` = v.`scenicSpotId` AND\n" +
            "                                    g.`statisticalDate` = v.`statisticalDate`\n" +
            "         ) t\n" +
            "    where 1=1");
        System.out.println(parser.getSql());
    }

    @Test
    void pageHelper() {
        CountSqlParser parser = new CountSqlParser();
        String sql = parser.getSimpleCountSql("select\n" +
            "IF(guideScenicSpotId IS NULL,voiceScenicSpotId,guideScenicSpotId) AS  scenicSpotId,\n" +
            "IF(guideStatisticalDate IS NULL,voiceStatisticalDate,guideStatisticalDate) AS  statisticalDate,\n" +
            "t.*\n" +
            "from (\n" +
            "SELECT g.scenicSpotId      AS guideScenicSpotId,\n" +
            "       g.orderType         AS guideOrderType,\n" +
            "       g.orderNum          AS guideOrderNum,\n" +
            "       g.totalGetAmount    AS guideTotalGetAmount,\n" +
            "       g.refundNum         AS guideRefundNum,\n" +
            "       g.totalRefundAmount AS guideTotalRefundAmount,\n" +
            "       g.statisticalDate   AS guideStatisticalDate,\n" +
            "       v.scenicSpotId      AS voiceScenicSpotId,\n" +
            "       v.orderType         AS voiceOrderType,\n" +
            "       v.orderNum          AS voiceOrderNum,\n" +
            "       v.totalGetAmount    AS voiceTotalGetAmount,\n" +
            "       v.refundNum         AS voiceRefundNum,\n" +
            "       v.totalRefundAmount AS voiceTotalRefundAmount,\n" +
            "       v.statisticalDate   AS voiceStatisticalDate\n" +
            "             FROM `v_guideOrder_day` g LEFT JOIN `v_voiceOrder_day` v ON g.`scenicSpotId` = v.`scenicSpotId`" +
            " AND g.`statisticalDate` = v.`statisticalDate`\n" +
            "             UNION ALL\n" +
            "             SELECT g.scenicSpotId      AS guideScenicSpotId,\n" +
            "                    g.orderType         AS guideOrderType,\n" +
            "                    g.orderNum          AS guideOrderNum,\n" +
            "                    g.totalGetAmount    AS guideTotalGetAmount,\n" +
            "                    g.refundNum         AS guideRefundNum,\n" +
            "                    g.totalRefundAmount AS guideTotalRefundAmount,\n" +
            "                    g.statisticalDate   AS guideStatisticalDate,\n" +
            "                    v.scenicSpotId      AS voiceScenicSpotId,\n" +
            "                    v.orderType         AS voiceOrderType,\n" +
            "                    v.orderNum          AS voiceOrderNum,\n" +
            "                    v.totalGetAmount    AS voiceTotalGetAmount,\n" +
            "                    v.refundNum         AS voiceRefundNum,\n" +
            "                    v.totalRefundAmount AS voiceTotalRefundAmount,\n" +
            "                    v.statisticalDate   AS voiceStatisticalDate\n" +
            "             FROM `v_guideOrder_day` g\n" +
            "                      RIGHT JOIN `v_voiceOrder_day` v\n" +
            "                                 ON g.`scenicSpotId` = v.`scenicSpotId` AND\n" +
            "                                    g.`statisticalDate` = v.`statisticalDate`\n" +
            "         ) t\n" +
            "    where 1=1");
        System.out.println(sql);
    }
}
