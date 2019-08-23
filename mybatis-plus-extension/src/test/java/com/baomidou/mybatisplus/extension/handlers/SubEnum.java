package com.baomidou.mybatisplus.extension.handlers;

public enum SubEnum implements SubEnumType<String> {
    ME("我"), YOU("你");

    private final String name;

    SubEnum(String name) {
        this.name = name;
    }

    @Override
    public String anyName() {
        return name;
    }
}
