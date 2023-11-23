package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * 函数参数元素类型.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
public enum FunctionArgsType {

    /**
     * 字面值.
     */
    VALUE,

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
     * 自定义函数.
     */
    UDFUNCTION;

}