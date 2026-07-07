package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

/**
 * 统一谓词操作符.
 *
 * <p>
 * 该枚举是 compiled 执行层真正消费的操作符集合,
 * 比公开注解层的 {@code CompareOp} 更接近运行期语义.
 * 例如 like 系列在这里已经被拆成 contains / starts-with / ends-with 等可直接执行的形式.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum PredicateOperator {

    EQ,

    NEQ,

    GT,

    GTE,

    LT,

    LTE,

    LIKE_CONTAINS,

    LIKE_NOT_CONTAINS,

    LIKE_STARTS_WITH,

    LIKE_ENDS_WITH,

    IN,

    NOT_IN,

    BETWEEN,

    NOT_BETWEEN,

    IS_NULL,

    IS_NOT_NULL,

    NULL_SWITCH
}
