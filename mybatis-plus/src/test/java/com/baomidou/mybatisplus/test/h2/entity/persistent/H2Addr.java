package com.baomidou.mybatisplus.test.h2.entity.persistent;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * <p>
 * h2address entity.
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/5/25
 */
@TableName("h2address")
public class H2Addr {

    @TableId("addr_id")
    private Long addrId;

    @TableField("addr_name")
    private String addrName;

    @TableField("test_id")
    private Long testId;

    public Long getAddrId() {
        return addrId;
    }

    public void setAddrId(Long addrId) {
        this.addrId = addrId;
    }

    public String getAddrName() {
        return addrName;
    }

    public void setAddrName(String addrName) {
        this.addrName = addrName;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    @Override
    public String toString() {
        return "H2Addr{" +
                "addrId=" + addrId +
                ", addrName='" + addrName + '\'' +
                ", testId=" + testId +
                '}';
    }
}
