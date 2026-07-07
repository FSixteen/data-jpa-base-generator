package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 表达式值基数.
 *
 * <p>
 * 该枚举描述一个表达式最终应产生单值、范围值还是集合值,
 * 供范围比较、成员判断和运行时函数求值共享.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum ExpressionCardinality {

    /**
     * 单值.
     *
     * <p>
     * 表示表达式最终只产生一个值, 可直接参与普通二元比较或单参函数调用.
     * </p>
     */
    SINGLE,

    /**
     * 双值.
     *
     * <p>
     * 表示表达式最终应产生一个起止值对, 主要服务于 `between/not between` 语义.
     * </p>
     */
    RANGE,

    /**
     * 集合值.
     *
     * <p>
     * 表示表达式最终应产生多个候选值, 主要服务于 `in/not in` 与集合路径场景.
     * </p>
     */
    COLLECTION;
}
