package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 运行时字段值表达式。
 *
 * <p>
 * 该节点表示值来自当前请求参数对象。
 * 它既可以把读取结果当作最终 literal 使用，也可以把读取结果再解释为一段路径，
 * 从而支持“字段值决定另一个字段路径”的动态场景。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class FieldValueExpression implements PredicateExpression {

    private final ExpressionSource source;

    private final ExpressionCardinality cardinality;

    private final String valueField;

    private FieldValueExpression(final ExpressionSource source, final ExpressionCardinality cardinality, final String valueField) {
        this.source = source;
        this.cardinality = cardinality;
        this.valueField = valueField;
    }

    private static FieldValueExpression of(final ExpressionSource source, final ExpressionCardinality cardinality, final String valueField) {
        return new FieldValueExpression(source, cardinality, valueField);
    }

    public static FieldValueExpression literal() {
        return literal(null);
    }

    public static FieldValueExpression literal(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE, ExpressionCardinality.SINGLE, valueField);
    }

    public static FieldValueExpression literalRange() {
        return literalRange(null);
    }

    public static FieldValueExpression literalRange(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE, ExpressionCardinality.RANGE, valueField);
    }

    public static FieldValueExpression literalCollection() {
        return literalCollection(null);
    }

    public static FieldValueExpression literalCollection(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE, ExpressionCardinality.COLLECTION, valueField);
    }

    public static FieldValueExpression path() {
        return path(null);
    }

    public static FieldValueExpression path(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE_PATH, ExpressionCardinality.SINGLE, valueField);
    }

    public static FieldValueExpression pathRange() {
        return pathRange(null);
    }

    public static FieldValueExpression pathRange(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE_PATH, ExpressionCardinality.RANGE, valueField);
    }

    public static FieldValueExpression pathCollection() {
        return pathCollection(null);
    }

    public static FieldValueExpression pathCollection(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE_PATH, ExpressionCardinality.COLLECTION, valueField);
    }

    @Override
    public ExpressionSource getSource() {
        return this.source;
    }

    @Override
    public ExpressionCardinality getCardinality() {
        return this.cardinality;
    }

    @Override
    public Class<?> getJavaType() {
        return Object.class;
    }

    public String getValueField() {
        return this.valueField;
    }

}
