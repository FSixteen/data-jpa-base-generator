package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 固定字面量表达式.
 *
 * <p>
 * 该节点表示表达式值来自注解或模板中直接声明的字符串字面量,
 * 后续会结合目标 Java 类型交由 {@code LiteralCodecs} 解析.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class LiteralExpression implements PredicateExpression {

    private final String rawValue;

    private final Class<?> javaType;

    private final ExpressionCardinality cardinality;

    /**
     * 创建一条字面量表达式.
     *
     * @param rawValue    原始字符串字面量
     * @param javaType    目标 Java 类型
     * @param cardinality 值基数语义
     */
    private LiteralExpression(final String rawValue, final Class<?> javaType, final ExpressionCardinality cardinality) {
        this.rawValue = rawValue;
        this.javaType = javaType;
        this.cardinality = cardinality;
    }

    /**
     * 创建一条单值字面量表达式.
     *
     * @param rawValue 原始字符串字面量
     * @param javaType 目标 Java 类型
     * @return 单值字面量表达式
     */
    public static LiteralExpression of(final String rawValue, final Class<?> javaType) {
        return new LiteralExpression(rawValue, javaType, ExpressionCardinality.SINGLE);
    }

    /**
     * 创建一条指定基数的字面量表达式.
     *
     * @param rawValue    原始字符串字面量
     * @param javaType    目标 Java 类型
     * @param cardinality 值基数语义
     * @return 字面量表达式
     */
    public static LiteralExpression of(final String rawValue, final Class<?> javaType, final ExpressionCardinality cardinality) {
        return new LiteralExpression(rawValue, javaType, cardinality);
    }

    /**
     * 返回原始字符串字面量.
     *
     * @return 原始字符串字面量
     */
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
