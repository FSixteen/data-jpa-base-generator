package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 统一表达式模型接口.
 *
 * <p>
 * 该接口是 compiled 主链路内部所有表达式节点的共同抽象.
 * 无论表达式来自实体路径、运行时值、固定字面量、函数还是自定义 provider,
 * 最终都会表现为一个 {@link PredicateExpression}.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface PredicateExpression {

    /**
     * 表达式来源.
     *
     * @return 当前表达式节点的来源类型
     */
    ExpressionSource getSource();

    /**
     * 表达式值基数.
     *
     * @return 当前表达式返回值的基数语义
     */
    ExpressionCardinality getCardinality();

    /**
     * 表达式目标类型.
     *
     * @return 当前表达式声明的目标 Java 类型
     */
    Class<?> getJavaType();

}
