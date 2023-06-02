package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * 值类型.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public enum ValueType {

    /**
     * 默认值, 即{@link COLUMN}.
     */
    AUTO,

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
    FUNCTION;
}
