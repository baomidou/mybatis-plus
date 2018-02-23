package com.baomidou.mybatisplus.extension.test.plugins.optimisticLocker.entity;

import com.baomidou.mybatisplus.annotation.Version;

public class IntVersionFather {

    @Version
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
