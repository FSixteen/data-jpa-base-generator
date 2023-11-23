package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * 字段(列)类型.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
public enum FieldType {

    /**
     * 默认值, 即{@link #COLUMN}.
     */
    AUTO,

    /**
     * 字面量(保留字).
     */
    LITERAL,

    /**
     * 字段.
     */
    COLUMN,

    /**
     * 字面值.
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
