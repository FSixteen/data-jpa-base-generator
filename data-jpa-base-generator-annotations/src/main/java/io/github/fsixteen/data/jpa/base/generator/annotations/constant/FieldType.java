package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * 字段(列)类型.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
public enum FieldType {

    /**
     * 默认值.
     */
    AUTO,

    /**
     * 字面量, {@link java.lang.String} 类型.
     */
    LITERAL,

    /**
     * 字面量, {@link java.math.BigDecimal} 类型.
     */
    LITERAL_BIGDECIMAL,

    /**
     * 字面量, {@link java.lang.Double} 类型.
     */
    LITERAL_DOUBLE,

    /**
     * 字面量, {@link java.lang.Float} 类型.
     */
    LITERAL_FLOAT,

    /**
     * 字面量, {@link java.lang.Short} 类型.
     */
    LITERAL_SHORT,

    /**
     * 字面量, {@link java.lang.Integer} 类型.
     */
    LITERAL_INTEGER,

    /**
     * 字面量, {@link java.lang.Long} 类型.
     */
    LITERAL_LONG,

    /**
     * 字段.
     */
    COLUMN,

    /**
     * 字段值.
     */
    VALUE,

    /**
     * 函数.
     */
    FUNCTION,

    /**
     * 自定义函数.
     */
    UDFUNCTION;
}
