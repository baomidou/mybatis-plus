package com.baomidou.mybatisplus.core.plugins;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class IgnoreStrategy {
    private Boolean tenantLine;
    private Boolean dynamicTableName;
    private Boolean blockAttack;
    private Boolean illegalSql;
    private Boolean dataPermission;
    private Map<String, Boolean> others;

}
