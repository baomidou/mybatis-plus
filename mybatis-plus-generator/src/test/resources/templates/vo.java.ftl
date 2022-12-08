package ${package.Entity};


/**
 * <p>
 * 测试自定义VO模板 ${table.comment!}
 * </p>
 *
 * 测试注入 ${cfg.abc}
 */


// 自定义的查询内容
<#list table.fields as field>
    字段： ${field.propertyName}
</#list>
