package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 函数表达式。
 *
 * <p>
 * 该节点表示一个函数调用，包括函数名、返回类型和参数列表。
 * 参数本身仍是 {@link PredicateExpression}，因此天然支持多层函数嵌套。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class FunctionExpression implements PredicateExpression {

    private final String name;

    private final Class<?> javaType;

    private final List<PredicateExpression> args;

    private FunctionExpression(final String name, final Class<?> javaType, final List<PredicateExpression> args) {
        this.name = name;
        this.javaType = javaType;
        this.args = Collections.unmodifiableList(new ArrayList<>(args));
    }

    public static FunctionExpression of(final String name, final Class<?> javaType, final List<PredicateExpression> args) {
        return new FunctionExpression(name, javaType, args);
    }

    public String getName() {
        return this.name;
    }

    public List<PredicateExpression> getArgs() {
        return this.args;
    }

    @Override
    public ExpressionSource getSource() {
        return ExpressionSource.FUNCTION;
    }

    @Override
    public ExpressionCardinality getCardinality() {
        return ExpressionCardinality.SINGLE;
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

}
