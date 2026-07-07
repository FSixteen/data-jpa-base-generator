package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 函数表达式.
 *
 * <p>
 * 该节点表示一个函数调用, 包括函数名、返回类型和参数列表.
 * 参数本身仍是 {@link PredicateExpression}, 因此天然支持多层函数嵌套.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class FunctionExpression implements PredicateExpression {

    private final String name;

    private final Class<?> javaType;

    private final List<PredicateExpression> args;

    /**
     * 创建一条函数表达式.
     *
     * @param name     函数名
     * @param javaType 函数返回类型
     * @param args     函数参数列表
     */
    private FunctionExpression(final String name, final Class<?> javaType, final List<PredicateExpression> args) {
        this.name = name;
        this.javaType = javaType;
        this.args = Collections.unmodifiableList(new ArrayList<>(args));
    }

    /**
     * 基于函数名、返回类型和参数列表创建函数表达式.
     *
     * @param name     函数名
     * @param javaType 函数返回类型
     * @param args     函数参数列表
     * @return 函数表达式实例
     */
    public static FunctionExpression of(final String name, final Class<?> javaType, final List<PredicateExpression> args) {
        return new FunctionExpression(name, javaType, args);
    }

    /**
     * 返回函数名.
     *
     * @return 函数名
     */
    public String getName() {
        return this.name;
    }

    /**
     * 返回函数参数列表.
     *
     * @return 不可变参数列表
     */
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
