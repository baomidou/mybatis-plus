package com.baomidou.mybatisplus.generator;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Customizes generator field metadata after type conversion and before templates render.
 * <p>
 * The customizer runs after the database metadata has been converted to {@link TableField.MetaInfo}
 * and {@link TableField#setColumnType(com.baomidou.mybatisplus.generator.config.rules.IColumnType) column type},
 * and before property-name conversion, annotation handling,
 * import collection, and template rendering.
 * <p>
 * Example: keep most fields on the default conversion path, but adjust a special database type such
 * as {@code POINT} before generated templates see it:
 * <pre>{@code
 * new StrategyConfig.Builder()
 *     .entityBuilder()
 *     .tableFieldMetaInfoCustomizer((tableInfo, tableField) -> {
 *         TableField.MetaInfo metaInfo = tableField.getMetaInfo();
 *         if ("POINT".equalsIgnoreCase(metaInfo.getTypeName())) {
 *             metaInfo.setJdbcType(JdbcType.OTHER);
 *             tableField.setColumnType(DbColumnType.OBJECT);
 *         }
 *     });
 * }</pre>
 *
 * @since 3.5.18
 */
@FunctionalInterface
public interface ITableFieldMetaInfoCustomizer {

    /**
     * Customize current field metadata.
     *
     * @param tableInfo current table info
     * @param tableField current field info, including {@link TableField.MetaInfo}
     */
    void customize(@NotNull TableInfo tableInfo, @NotNull TableField tableField);

    /**
     * Returns a composed customizer that performs this customizer,
     * followed by the {@code after} customizer.
     *
     * @param after the customizer to perform after this customizer
     * @return a composed customizer
     */
    @NotNull
    default ITableFieldMetaInfoCustomizer andThen(@NotNull ITableFieldMetaInfoCustomizer after) {
        Objects.requireNonNull(after, "after");
        return (tableInfo, tableField) -> {
            customize(tableInfo, tableField);
            after.customize(tableInfo, tableField);
        };
    }
}
