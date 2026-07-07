package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 运行时字段值表达式.
 *
 * <p>
 * 该节点表示值来自当前请求参数对象.
 * 它既可以把读取结果当作最终 literal 使用, 也可以把读取结果再解释为一段路径,
 * 从而支持“字段值决定另一个字段路径”的动态场景.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class FieldValueExpression implements PredicateExpression {

    private final ExpressionSource source;

    private final ExpressionCardinality cardinality;

    private final String valueField;

    /**
     * 创建一条字段值表达式.
     *
     * @param source      值来源语义, 决定是 literal 还是 path
     * @param cardinality 值基数语义
     * @param valueField  要读取的字段名；为空时通常表示使用当前默认绑定字段
     */
    private FieldValueExpression(final ExpressionSource source, final ExpressionCardinality cardinality, final String valueField) {
        this.source = source;
        this.cardinality = cardinality;
        this.valueField = valueField;
    }

    /**
     * 统一创建字段值表达式实例.
     *
     * @param source      值来源语义
     * @param cardinality 值基数语义
     * @param valueField  目标字段名
     * @return 字段值表达式实例
     */
    private static FieldValueExpression of(final ExpressionSource source, final ExpressionCardinality cardinality, final String valueField) {
        return new FieldValueExpression(source, cardinality, valueField);
    }

    /**
     * 创建一条“当前字段值作为单值 literal 使用”的表达式.
     *
     * @return 单值字段 literal 表达式
     */
    public static FieldValueExpression literal() {
        return literal(null);
    }

    /**
     * 创建一条“指定字段值作为单值 literal 使用”的表达式.
     *
     * @param valueField 字段名
     * @return 单值字段 literal 表达式
     */
    public static FieldValueExpression literal(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE, ExpressionCardinality.SINGLE, valueField);
    }

    /**
     * 创建一条“当前字段值作为范围 literal 使用”的表达式.
     *
     * @return 范围字段 literal 表达式
     */
    public static FieldValueExpression literalRange() {
        return literalRange(null);
    }

    /**
     * 创建一条“指定字段值作为范围 literal 使用”的表达式.
     *
     * @param valueField 字段名
     * @return 范围字段 literal 表达式
     */
    public static FieldValueExpression literalRange(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE, ExpressionCardinality.RANGE, valueField);
    }

    /**
     * 创建一条“当前字段值作为集合 literal 使用”的表达式.
     *
     * @return 集合字段 literal 表达式
     */
    public static FieldValueExpression literalCollection() {
        return literalCollection(null);
    }

    /**
     * 创建一条“指定字段值作为集合 literal 使用”的表达式.
     *
     * @param valueField 字段名
     * @return 集合字段 literal 表达式
     */
    public static FieldValueExpression literalCollection(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE, ExpressionCardinality.COLLECTION, valueField);
    }

    /**
     * 创建一条“当前字段值解释为单值路径”的表达式.
     *
     * @return 单值字段路径表达式
     */
    public static FieldValueExpression path() {
        return path(null);
    }

    /**
     * 创建一条“指定字段值解释为单值路径”的表达式.
     *
     * @param valueField 字段名
     * @return 单值字段路径表达式
     */
    public static FieldValueExpression path(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE_PATH, ExpressionCardinality.SINGLE, valueField);
    }

    /**
     * 创建一条“当前字段值解释为范围路径”的表达式.
     *
     * @return 范围字段路径表达式
     */
    public static FieldValueExpression pathRange() {
        return pathRange(null);
    }

    /**
     * 创建一条“指定字段值解释为范围路径”的表达式.
     *
     * @param valueField 字段名
     * @return 范围字段路径表达式
     */
    public static FieldValueExpression pathRange(final String valueField) {
        return of(ExpressionSource.FIELD_VALUE_PATH, ExpressionCardinality.RANGE, valueField);
    }

    /**
     * 创建一条“当前字段值解释为集合路径”的表达式.
     *
     * @return 集合字段路径表达式
     */
    public static FieldValueExpression pathCollection() {
        return pathCollection(null);
    }

    /**
     * 创建一条“指定字段值解释为集合路径”的表达式.
     *
     * @param valueField 字段名
     * @return 集合字段路径表达式
     */
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

    /**
     * 返回当前表达式绑定的字段名.
     *
     * @return 显式字段名；为空时表示使用当前默认绑定字段
     */
    public String getValueField() {
        return this.valueField;
    }

}
