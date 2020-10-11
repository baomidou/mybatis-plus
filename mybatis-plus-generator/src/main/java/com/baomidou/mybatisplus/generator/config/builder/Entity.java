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
public class Entity {

    private Entity() {

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

    public static class Builder extends BaseBuilder {

        private final Entity entity = new Entity();

        public Builder(StrategyConfig strategyConfig) {
            super(strategyConfig);
            this.entity.nameConvert = new INameConvert.DefaultNameConvert(strategyConfig);
        }

        /**
         * 名称转换实现
         *
         * @param nameConvert 名称转换实现
         * @return this
         */
        public Builder nameConvert(INameConvert nameConvert) {
            this.entity.nameConvert = nameConvert;
            return this;
        }

        public Builder superClass(Class<?> clazz) {
            return superClass(clazz.getName());
        }

        public Builder superClass(String superEntityClass) {
            this.entity.superClass = superEntityClass;
            return this;
        }

        /**
         * 实体是否生成serialVersionUID
         *
         * @param serialVersionUID 是否生成
         * @return this
         */
        public Builder serialVersionUID(boolean serialVersionUID) {
            this.entity.serialVersionUID = serialVersionUID;
            return this;
        }

        /**
         * 是否生成字段常量
         *
         * @param columnConstant 是否生成字段常量
         * @return this
         */
        public Builder columnConstant(boolean columnConstant) {
            this.entity.columnConstant = columnConstant;
            return this;
        }

        /**
         * 实体是否为链式模型
         *
         * @param chain 是否为链式模型
         * @return this
         */
        public Builder chainModel(boolean chain) {
            this.entity.chain = chain;
            return this;
        }

        /**
         * 是否为lombok模型
         *
         * @param lombok 是否为lombok模型
         * @return this
         */
        public Builder lombok(boolean lombok) {
            this.entity.lombok = lombok;
            return this;
        }

        /**
         * Boolean类型字段是否移除is前缀
         *
         * @param booleanColumnRemoveIsPrefix 是否移除
         * @return this
         */
        public Builder booleanColumnRemoveIsPrefix(boolean booleanColumnRemoveIsPrefix) {
            this.entity.booleanColumnRemoveIsPrefix = booleanColumnRemoveIsPrefix;
            return this;
        }

        /**
         * 生成实体时，是否生成字段注解
         *
         * @param tableFieldAnnotationEnable 是否生成
         * @return this
         */
        public Builder tableFieldAnnotationEnable(boolean tableFieldAnnotationEnable) {
            this.entity.tableFieldAnnotationEnable = tableFieldAnnotationEnable;
            return this;
        }

        /**
         * 乐观锁属性名称
         *
         * @param versionFieldName 乐观锁属性名称
         * @return this
         */
        public Builder versionFieldName(String versionFieldName) {
            this.entity.versionFieldName = versionFieldName;
            return this;
        }

        /**
         * 逻辑删除属性名称
         *
         * @param logicDeleteFieldName 逻辑删除属性名称
         * @return this
         */
        public Builder logicDeleteFieldName(String logicDeleteFieldName) {
            this.entity.logicDeleteFieldName = logicDeleteFieldName;
            return this;
        }

        public Builder naming(NamingStrategy namingStrategy) {
            this.entity.naming = namingStrategy;
            return this;
        }

        public Builder columnNaming(NamingStrategy namingStrategy) {
            this.entity.columnNaming = namingStrategy;
            return this;
        }

        /**
         * 添加父类公共字段
         *
         * @param superEntityColumns 父类字段(数据库字段列名)
         * @return this
         * @since 3.4.1
         */
        public Builder addSuperEntityColumns(String... superEntityColumns) {
            this.entity.superEntityColumns.addAll(Arrays.asList(superEntityColumns));
            return this;
        }

        /**
         * 添加表字段填充
         *
         * @param tableFill 填充字段
         * @return this
         * @since 3.4.1
         */
        public Builder addTableFills(TableFill... tableFill) {
            this.entity.tableFillList.addAll(Arrays.asList(tableFill));
            return this;
        }

        /**
         * 添加表字段填充
         *
         * @param tableFillList 填充字段集合
         * @return this
         * @since 3.4.1
         */
        public Builder addTableFills(List<TableFill> tableFillList) {
            this.entity.tableFillList.addAll(tableFillList);
            return this;
        }

        public Entity get(){
            return this.entity;
        }
    }
}
