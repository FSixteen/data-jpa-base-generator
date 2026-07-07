package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.codecs.LiteralCodecs;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.RuntimeValueResolver;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionCardinality;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionSource;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.LiteralExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * 纯运行时表达式求值器。
 *
 * <p>
 * 该组件服务于 `Cases` 分支命中条件等“不需要 JPA Root/CriteriaBuilder，只需要对参数对象当前状态做判断”的场景。
 * 它复用 canonical 表达式模型，避免再维护一套单独的 CaseWhen 字段解析协议。
 * </p>
 *
 * <p>
 * 支持的输入仍然与 compiled 执行层保持一致，包括路径引用、当前字段值、字面量与函数表达式，
 * 因而可以在不依赖 JPA 基础设施的情况下复现绝大部分 canonical 比较语义。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RuntimeExpressionEvaluator {

    private RuntimeExpressionEvaluator() {
    }

    /**
     * 在不依赖 JPA 的前提下求值一个 canonical 表达式。
     */
    public static Object evaluate(final PredicateExpression expression, final Object args, final Object currentFieldValue) {
        if (Objects.isNull(expression)) {
            return null;
        }
        if (ExpressionSource.PATH == expression.getSource()) {
            return RuntimeValueResolver.readPath(args, ((PathExpression) expression).getPath());
        }
        if (ExpressionSource.FIELD_VALUE == expression.getSource() || ExpressionSource.FIELD_VALUE_PATH == expression.getSource()) {
            return RuntimeValueResolver.resolve((FieldValueExpression) expression, args, currentFieldValue);
        }
        if (ExpressionSource.LITERAL == expression.getSource()) {
            LiteralExpression literalExpression = (LiteralExpression) expression;
            if (ExpressionCardinality.COLLECTION == literalExpression.getCardinality()) {
                return LiteralCodecs.parseCollection(literalExpression.getRawValue(), ",", literalExpression.getJavaType());
            }
            if (ExpressionCardinality.RANGE == literalExpression.getCardinality()) {
                return LiteralCodecs.parseRange(literalExpression.getRawValue(), ",", literalExpression.getJavaType());
            }
            return LiteralCodecs.parse(literalExpression.getRawValue(), literalExpression.getJavaType());
        }
        if (ExpressionSource.FUNCTION == expression.getSource()) {
            return applyFunction((FunctionExpression) expression, args, currentFieldValue);
        }
        return null;
    }

    /**
     * 执行运行时支持的内建函数表达式。
     */
    private static Object applyFunction(final FunctionExpression expression, final Object args, final Object currentFieldValue) {
        String functionName = null == expression.getName() ? "" : expression.getName().trim().toLowerCase(Locale.ROOT);
        List<Object> values = new ArrayList<Object>(expression.getArgs().size());
        for (PredicateExpression arg : expression.getArgs()) {
            values.add(evaluate(arg, args, currentFieldValue));
        }
        switch (functionName) {
            case "length":
                return Integer.valueOf(toString(values, 0).length());
            case "lower":
                return toString(values, 0).toLowerCase(Locale.ROOT);
            case "upper":
                return toString(values, 0).toUpperCase(Locale.ROOT);
            case "trim":
                return toString(values, 0).trim();
            case "concat":
                return concat(values);
            case "coalesce":
                return coalesce(values);
            default:
                throw new IllegalArgumentException("Unsupported runtime function: " + expression.getName());
        }
    }

    /**
     * 拼接函数参数中的全部非 null 值。
     */
    private static String concat(final List<Object> values) {
        StringBuilder builder = new StringBuilder();
        for (Object value : values) {
            if (Objects.nonNull(value)) {
                builder.append(value);
            }
        }
        return builder.toString();
    }

    /**
     * 返回第一个非 null 参数值。
     */
    private static Object coalesce(final List<Object> values) {
        for (Object value : values) {
            if (Objects.nonNull(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 读取指定下标的函数参数并转为字符串。
     */
    private static String toString(final List<Object> values, final int index) {
        Object value = index < values.size() ? values.get(index) : null;
        return Objects.toString(value, "");
    }

    /**
     * 求值范围比较所需的起止值列表。
     */
    public static List<Object> evaluateRange(final PredicateExpression right, final List<PredicateExpression> extra, final Object args,
        final Object currentFieldValue) {
        List<Object> values = new ArrayList<Object>(2);
        Object rightValue = evaluate(right, args, currentFieldValue);
        if (rightValue instanceof Collection<?>) {
            values.addAll(new ArrayList<Object>((Collection<?>) rightValue));
        } else {
            values.add(rightValue);
        }
        if (!extra.isEmpty()) {
            values.add(evaluate(extra.get(0), args, currentFieldValue));
        }
        return values;
    }

}
