package ${package.Entity}

<#list importEntityPackages as pkg>
import ${pkg}
</#list>

/**
 * <p>
 * ${table.comment}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#list entityClassAnnotations as an>
${an.displayName}
</#list>
<#if superEntityClass??>
class ${entity} : ${superEntityClass}<#if activeRecord><${entity}></#if>() {
<#elseif activeRecord>
class ${entity} : Model<${entity}>() {
<#elseif entitySerialVersionUID>
class ${entity} : Serializable {
<#else>
class ${entity} {
</#if>

<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
<#if field.keyFlag>
    <#assign keyPropertyName="${field.propertyName}"/>
</#if>
<#if field.comment!?length gt 0>
    <#if entityFieldUseJavaDoc>
    /**
     * ${field.comment}
     */
    </#if>
</#if>
<#list field.annotationAttributesList as an>
    ${an.displayName}
</#list>
    <#if field.propertyType == "Integer">
    var ${field.propertyName}: Int? = null
    <#else>
    var ${field.propertyName}: ${field.propertyType}? = null
    </#if>

</#list>
<#-- ----------  END 字段循环遍历  ---------->
<#if entityColumnConstant>
    companion object {
<#list table.fields as field>

        const val ${field.name?upper_case} : String = "${field.name}"

</#list>
    }

</#if>
<#if activeRecord>
    override fun pkVal(): Serializable? {
<#if keyPropertyName??>
        return ${keyPropertyName}
<#else>
        return null
</#if>
    }

</#if>
<#if entityToString>
    override fun toString(): String {
        return "${entity}{" +
<#list table.fields as field>
<#if field_index==0>
        "${field.propertyName}=" + ${field.propertyName} +
<#else>
        ", ${field.propertyName}=" + ${field.propertyName} +
</#if>
</#list>
        "}"
    }
</#if>
}
