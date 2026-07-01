package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 表达式值基数。
 *
 * <p>
 * 该枚举描述一个表达式最终应产生单值、范围值还是集合值，
 * 供范围比较、成员判断和运行时函数求值共享。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum ExpressionCardinality {

    /**
     * 单值.
     */
    SINGLE,

    /**
     * 双值.
     */
    RANGE,

    /**
     * 集合值.
     */
    COLLECTION;
}
