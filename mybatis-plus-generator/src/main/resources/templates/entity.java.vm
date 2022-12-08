package ${package.Entity};

#foreach($pkg in ${table.importPackages})
import ${pkg};
#end
#if(${springdoc})
import io.swagger.v3.oas.annotations.media.Schema;
#elseif(${swagger})
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
#end
#if(${entityLombokModel})
import lombok.Getter;
import lombok.Setter;
#if(${chainModel})
import lombok.experimental.Accessors;
#end
#end

/**
 * <p>
 * $!{table.comment}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
#if(${entityLombokModel})
@Getter
@Setter
  #if(${chainModel})
@Accessors(chain = true)
  #end
#end
#if(${table.convert})
@TableName("${schemaName}${table.name}")
#end
#if(${springdoc})
@Schema(name = "${entity}", description = "$!{table.comment}")
#elseif(${swagger})
@ApiModel(value = "${entity}对象", description = "$!{table.comment}")
#end
#if(${superEntityClass})
public class ${entity} extends ${superEntityClass}#if(${activeRecord})<${entity}>#end {
#elseif(${activeRecord})
public class ${entity} extends Model<${entity}> {
#elseif(${entitySerialVersionUID})
public class ${entity} implements Serializable {
#else
public class ${entity} {
#end
#if(${entitySerialVersionUID})

    private static final long serialVersionUID = 1L;
#end
## ----------  BEGIN 字段循环遍历  ----------
#foreach($field in ${table.fields})

#if(${field.keyFlag})
#set($keyPropertyName=${field.propertyName})
#end
#if("$!field.comment" != "")
  #if(${springdoc})
    @Schema(description = "${field.comment}")
  #elseif(${swagger})
    @ApiModelProperty("${field.comment}")
  #else
    /**
     * ${field.comment}
     */
  #end
#end
#if(${field.keyFlag})
## 主键
  #if(${field.keyIdentityFlag})
    @TableId(value = "${field.annotationColumnName}", type = IdType.AUTO)
  #elseif(!$null.isNull(${idType}) && "$!idType" != "")
    @TableId(value = "${field.annotationColumnName}", type = IdType.${idType})
  #elseif(${field.convert})
    @TableId("${field.annotationColumnName}")
  #end
## 普通字段
#elseif(${field.fill})
## -----   存在字段填充设置   -----
  #if(${field.convert})
    @TableField(value = "${field.annotationColumnName}", fill = FieldFill.${field.fill})
  #else
    @TableField(fill = FieldFill.${field.fill})
  #end
#elseif(${field.convert})
    @TableField("${field.annotationColumnName}")
#end
## 乐观锁注解
#if(${field.versionField})
    @Version
#end
## 逻辑删除注解
#if(${field.logicDeleteField})
    @TableLogic
#end
    private ${field.propertyType} ${field.propertyName};
#end
## ----------  END 字段循环遍历  ----------
#if(!${entityLombokModel})
#foreach($field in ${table.fields})
  #if(${field.propertyType.equals("boolean")})
    #set($getprefix="is")
  #else
    #set($getprefix="get")
  #end

    public ${field.propertyType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }

  #if(${chainModel})
    public ${entity} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
  #else
    public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
  #end
        this.${field.propertyName} = ${field.propertyName};
  #if(${chainModel})
        return this;
  #end
    }
#end
## --foreach end---
#end
## --end of #if(!${entityLombokModel})--
#if(${entityColumnConstant})
  #foreach($field in ${table.fields})

    public static final String ${field.name.toUpperCase()} = "${field.name}";
  #end
#end
#if(${activeRecord})

    @Override
    public Serializable pkVal() {
  #if(${keyPropertyName})
        return this.${keyPropertyName};
  #else
        return null;
  #end
    }
#end
#if(!${entityLombokModel})

    @Override
    public String toString() {
        return "${entity}{" +
  #foreach($field in ${table.fields})
    #if($!{foreach.index}==0)
        "${field.propertyName} = " + ${field.propertyName} +
    #else
        ", ${field.propertyName} = " + ${field.propertyName} +
    #end
  #end
        "}";
    }
#end
}
