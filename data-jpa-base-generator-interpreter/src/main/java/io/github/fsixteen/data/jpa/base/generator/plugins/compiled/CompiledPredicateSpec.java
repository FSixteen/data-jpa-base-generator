package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * 统一谓词规格。
 *
 * <p>
 * 该对象表示“一个谓词在统一执行层里最终应该如何计算”，
 * 即把操作符、左右表达式、附加操作数和生效后的公共选项统一收口到同一模型中。
 * compiled provider、纯运行时判定器和调试测试都围绕它工作。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateSpec {

    private final PredicateOperator operator;

    private final PredicateExpression left;

    private final PredicateExpression right;

    private final List<PredicateExpression> extraOperands;

    private final PredicateOptionsSpec options;

    private CompiledPredicateSpec(final PredicateOperator operator, final PredicateExpression left, final PredicateExpression right,
        final List<PredicateExpression> extraOperands, final PredicateOptionsSpec options) {
        this.operator = operator;
        this.left = left;
        this.right = right;
        this.extraOperands = Collections.unmodifiableList(new ArrayList<PredicateExpression>(extraOperands));
        this.options = options;
    }

    public static CompiledPredicateSpec of(final PredicateOperator operator, final PredicateExpression left, final PredicateExpression right,
        final List<PredicateExpression> extraOperands, final PredicateOptionsSpec options) {
        return new CompiledPredicateSpec(operator, left, right, extraOperands, options);
    }

    public PredicateOperator getOperator() {
        return this.operator;
    }

    public PredicateExpression getLeft() {
        return this.left;
    }

    public PredicateExpression getRight() {
        return this.right;
    }

    public List<PredicateExpression> getExtraOperands() {
        return this.extraOperands;
    }

    public PredicateOptionsSpec getOptions() {
        return this.options;
    }

}
