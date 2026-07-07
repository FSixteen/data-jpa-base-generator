package io.github.fsixteen.data.jpa.base.generator.plugins.registry;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateOperator;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * 注册式谓词模板.
 *
 * <p>
 * 该对象表示一条完整的二元谓词模板,
 * 包括左表达式、右表达式和操作符.
 * 与只表示单个表达式片段的 {@link PredicateExpressionTemplate} 不同,
 * 它可以被 {@link RegisteredPredicateResolver} 直接解析为最终谓词.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RegisteredPredicateTemplate {

    private final PredicateExpression left;

    private final PredicateExpression right;

    private final PredicateOperator operator;

    private RegisteredPredicateTemplate(final PredicateExpression left, final PredicateExpression right, final PredicateOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static RegisteredPredicateTemplate of(final PredicateExpression left, final PredicateExpression right, final ComparableType type) {
        return new RegisteredPredicateTemplate(left, right, type.getOperator());
    }

    public static RegisteredPredicateTemplate of(final PredicateExpression left, final PredicateExpression right, final PredicateOperator operator) {
        return new RegisteredPredicateTemplate(left, right, operator);
    }

    public PredicateExpression getLeft() {
        return this.left;
    }

    public PredicateExpression getRight() {
        return this.right;
    }

    public PredicateOperator getOperator() {
        return this.operator;
    }

}
