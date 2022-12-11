package ${package.Entity};


/**
 * <p>
 * 测试自定义DTO模板 ${table.comment!}
 * </p>
 *
 * 测试注入 ${cfg.abc}
 */


// 自定义的查询内容
<#list table.fields as field>
    自定义 abc 内容： ${field.customMap.abc}
    自定义 def 内容： ${field.customMap.def}
</#list>
