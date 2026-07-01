package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 固定字面量表达式。
 *
 * <p>
 * 该节点表示表达式值来自注解或模板中直接声明的字符串字面量，
 * 后续会结合目标 Java 类型交由 {@code LiteralCodecs} 解析。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class LiteralExpression implements PredicateExpression {

    private final String rawValue;

    private final Class<?> javaType;

    private final ExpressionCardinality cardinality;

    private LiteralExpression(final String rawValue, final Class<?> javaType, final ExpressionCardinality cardinality) {
        this.rawValue = rawValue;
        this.javaType = javaType;
        this.cardinality = cardinality;
    }

    public static LiteralExpression of(final String rawValue, final Class<?> javaType) {
        return new LiteralExpression(rawValue, javaType, ExpressionCardinality.SINGLE);
    }

    public static LiteralExpression of(final String rawValue, final Class<?> javaType, final ExpressionCardinality cardinality) {
        return new LiteralExpression(rawValue, javaType, cardinality);
    }

    public String getRawValue() {
        return this.rawValue;
    }

    @Override
    public ExpressionSource getSource() {
        return ExpressionSource.LITERAL;
    }

    @Override
    public ExpressionCardinality getCardinality() {
        return this.cardinality;
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

}
