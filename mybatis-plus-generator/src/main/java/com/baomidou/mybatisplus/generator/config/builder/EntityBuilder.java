package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实体属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.4.1
 */
@Getter
public class EntityBuilder extends BaseBuilder {

    public EntityBuilder(StrategyConfig strategyConfig) {
        super(strategyConfig);
        this.nameConvert = new INameConvert.DefaultNameConvert(strategyConfig);
    }

    /**
     * 名称转换
     */
    private INameConvert nameConvert;

    /**
     * 自定义继承的Entity类全称，带包名
     */
    @Setter(AccessLevel.NONE)
    private String superClass;

    /**
     * 自定义基础的Entity类，公共字段
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> superEntityColumns = new HashSet<>();

    /**
     * 实体是否生成 serialVersionUID
     */
    private boolean serialVersionUID;

    /**
     * 【实体】是否生成字段常量（默认 false）<br>
     * -----------------------------------<br>
     * public static final String ID = "test_id";
     */
    private boolean columnConstant;

    /**
     * 【实体】是否为链式模型（默认 false）<br>
     * -----------------------------------<br>
     * public User setName(String name) { this.name = name; return this; }
     *
     * @since 3.3.2
     */
    private boolean chain;

    /**
     * 【实体】是否为lombok模型（默认 false）<br>
     * <a href="https://projectlombok.org/">document</a>
     */
    private boolean lombok;

    /**
     * Boolean类型字段是否移除is前缀（默认 false）<br>
     * 比如 : 数据库字段名称 : 'is_xxx',类型为 : tinyint. 在映射实体的时候则会去掉is,在实体类中映射最终结果为 xxx
     */
    private boolean booleanColumnRemoveIsPrefix;

    /**
     * 是否生成实体时，生成字段注解
     */
    private boolean tableFieldAnnotationEnable;

    /**
     * 乐观锁属性名称
     */
    private String versionFieldName;

    /**
     * 逻辑删除属性名称
     */
    private String logicDeleteFieldName;

    /**
     * 表填充字段
     */
    private final List<TableFill> tableFillList = new ArrayList<>();

    /**
     * 数据库表映射到实体的命名策略
     */
    private NamingStrategy naming = NamingStrategy.no_change;

    /**
     * 数据库表字段映射到实体的命名策略
     * <p>未指定按照 naming 执行</p>
     */
    private NamingStrategy columnNaming = null;

    /**
     * 名称转换实现
     *
     * @param nameConvert 名称转换实现
     * @return this
     */
    public EntityBuilder nameConvert(INameConvert nameConvert) {
        this.nameConvert = nameConvert;
        return this;
    }

    public EntityBuilder superClass(Class<?> clazz) {
        this.superClass = clazz.getName();
        return this;
    }

    public EntityBuilder superClass(String superEntityClass) {
        this.superClass = superEntityClass;
        return this;
    }

    /**
     * 实体是否生成serialVersionUID
     *
     * @param serialVersionUID 是否生成
     * @return this
     */
    public EntityBuilder serialVersionUID(boolean serialVersionUID) {
        this.serialVersionUID = serialVersionUID;
        return this;
    }

    /**
     * 是否生成字段常量
     *
     * @param columnConstant 是否生成字段常量
     * @return this
     */
    public EntityBuilder columnConstant(boolean columnConstant) {
        this.columnConstant = columnConstant;
        return this;
    }

    /**
     * 实体是否为链式模型
     *
     * @param chain 是否为链式模型
     * @return this
     */
    public EntityBuilder chainModel(boolean chain) {
        this.chain = chain;
        return this;
    }

    /**
     * 是否为lombok模型
     *
     * @param lombok 是否为lombok模型
     * @return this
     */
    public EntityBuilder lombok(boolean lombok) {
        this.lombok = lombok;
        return this;
    }

    /**
     * Boolean类型字段是否移除is前缀
     *
     * @param booleanColumnRemoveIsPrefix 是否移除
     * @return this
     */
    public EntityBuilder booleanColumnRemoveIsPrefix(boolean booleanColumnRemoveIsPrefix) {
        this.booleanColumnRemoveIsPrefix = booleanColumnRemoveIsPrefix;
        return this;
    }

    /**
     * 生成实体时，是否生成字段注解
     *
     * @param tableFieldAnnotationEnable 是否生成
     * @return this
     */
    public EntityBuilder tableFieldAnnotationEnable(boolean tableFieldAnnotationEnable) {
        this.tableFieldAnnotationEnable = tableFieldAnnotationEnable;
        return this;
    }

    /**
     * 乐观锁属性名称
     *
     * @param versionFieldName 乐观锁属性名称
     * @return this
     */
    public EntityBuilder versionFieldName(String versionFieldName) {
        this.versionFieldName = versionFieldName;
        return this;
    }

    /**
     * 逻辑删除属性名称
     *
     * @param logicDeleteFieldName 逻辑删除属性名称
     * @return this
     */
    public EntityBuilder logicDeleteFieldName(String logicDeleteFieldName) {
        this.logicDeleteFieldName = logicDeleteFieldName;
        return this;
    }

    public EntityBuilder naming(NamingStrategy namingStrategy) {
        this.naming = namingStrategy;
        return this;
    }

    public EntityBuilder columnNaming(NamingStrategy namingStrategy) {
        this.columnNaming = namingStrategy;
        return this;
    }

    /**
     * 添加父类公共字段
     *
     * @param superEntityColumns 父类字段(数据库字段列名)
     * @return this
     * @since 3.4.1
     */
    public EntityBuilder addSuperEntityColumns(String... superEntityColumns) {
        this.superEntityColumns.addAll(Arrays.asList(superEntityColumns));
        return this;
    }

    /**
     * 添加表字段填充
     *
     * @param tableFill 填充字段
     * @return this
     * @since 3.4.1
     */
    public EntityBuilder addTableFills(TableFill... tableFill) {
        this.tableFillList.addAll(Arrays.asList(tableFill));
        return this;
    }

    /**
     * 添加表字段填充
     *
     * @param tableFillList 填充字段集合
     * @return this
     * @since 3.4.1
     */
    public EntityBuilder addTableFills(List<TableFill> tableFillList) {
        this.tableFillList.addAll(tableFillList);
        return this;
    }

    /**
     * <p>
     * 父类 Class 反射属性转换为公共字段
     * </p>
     *
     * @param clazz 实体父类 Class
     */
    public void convertSuperEntityColumns(Class<?> clazz) {
        List<Field> fields = TableInfoHelper.getAllFields(clazz);
        this.superEntityColumns.addAll(fields.stream().map(field -> {
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null && StringUtils.isNotBlank(tableId.value())) {
                return tableId.value();
            }
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
                return tableField.value();
            }
            if (null == columnNaming || columnNaming == NamingStrategy.no_change) {
                return field.getName();
            }
            return StringUtils.camelToUnderline(field.getName());
        }).collect(Collectors.toSet()));
    }

    public Set<String> getSuperEntityColumns() {
        if (StringUtils.isNotBlank(this.superClass)) {
            try {
                Class<?> superEntity = ClassUtils.toClassConfident(this.superClass);
                convertSuperEntityColumns(superEntity);
            } catch (Exception e) {
                //当父类实体存在类加载器的时候,识别父类实体字段，不存在的情况就只有通过指定superEntityColumns属性了。
            }
        }
        return this.superEntityColumns;
    }

    public NamingStrategy getColumnNaming() {
        // 未指定以 naming 策略为准
        return Optional.ofNullable(columnNaming).orElse(naming);
    }

    /**
     * 匹配父类字段(忽略大小写)
     *
     * @param fieldName 字段名
     * @return 是否匹配
     * @since 3.4.1
     */
    public boolean matchSuperEntityColumns(String fieldName) {
        // 公共字段判断忽略大小写【 部分数据库大小写不敏感 】
        return superEntityColumns.stream().anyMatch(e -> e.equalsIgnoreCase(fieldName));
    }

}
