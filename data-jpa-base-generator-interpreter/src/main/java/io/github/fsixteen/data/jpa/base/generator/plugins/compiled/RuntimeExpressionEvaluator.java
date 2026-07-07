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
 * 纯运行时表达式求值器.
 *
 * <p>
 * 该组件服务于 `Cases` 分支命中条件等“不需要 JPA Root/CriteriaBuilder, 只需要对参数对象当前状态做判断”的场景.
 * 它复用 canonical 表达式模型, 避免再维护一套单独的 CaseWhen 字段解析协议.
 * </p>
 *
 * <p>
 * 支持的输入仍然与 compiled 执行层保持一致, 包括路径引用、当前字段值、字面量与函数表达式,
 * 因而可以在不依赖 JPA 基础设施的情况下复现绝大部分 canonical 比较语义.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RuntimeExpressionEvaluator {

    private RuntimeExpressionEvaluator() {
    }

    /**
     * 单参字符串函数枚举.
     */
    private enum UnaryStringOperator {
        LOWER("lower"), UPPER("upper"), TRIM("trim");

        private final String functionName;

        /**
         * 构造一个运行时支持的单参字符串函数标识.
         *
         * @param functionName 函数名, 需与 DSL/JPA 中使用的函数名保持一致
         */
        UnaryStringOperator(final String functionName) {
            this.functionName = functionName;
        }

        /**
         * 返回当前枚举对应的函数名.
         *
         * @return 当前操作符的标准函数名
         */
        private String functionName() {
            return this.functionName;
        }
    }

    /**
     * 在不依赖 JPA 的前提下求值一个 canonical 表达式.
     *
     * <p>
     * 该方法是纯运行时执行层的统一入口, 用于把路径引用、字段值引用、字面量与函数表达式解析为最终 Java 值.
     * 其目标不是生成 Criteria API 对象, 而是在 `Cases` 分支判断等场景下尽可能复现与 JPA 编译阶段一致的取值结果.
     * </p>
     *
     * @param expression        待求值的 canonical 表达式；为 {@code null} 时直接返回 {@code null}
     * @param args              当前请求或查询对象, 用于解析路径表达式和字段值路径表达式
     * @param currentFieldValue 当前正在处理的字段值, 供
     *                          {@code FIELD_VALUE}/{@code FIELD_VALUE_PATH} 表达式复用
     * @return 表达式对应的运行时值；当表达式为空或类型当前不支持时返回 {@code null}
     * @throws IllegalArgumentException 当函数表达式引用了当前运行时执行层不支持的内建函数时抛出
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
     * 执行运行时支持的内建函数表达式.
     *
     * <p>
     * 当前实现只覆盖需要在纯运行时环境中与 JPA 行为保持一致的内建函数,
     * 包括 {@code length/lower/upper/trim/concat/coalesce}.
     * 所有函数参数会先递归求值, 再按函数名称分派到对应的实现.
     * </p>
     *
     * @param expression        函数表达式, 函数名取自 {@link FunctionExpression#getName()}
     * @param args              当前请求或查询对象, 用于递归解析函数参数中的路径或字段值表达式
     * @param currentFieldValue 当前字段值, 用于递归解析函数参数中的字段值引用
     * @return 函数执行后的运行时结果；不同函数可能返回 {@link String}、{@link Integer} 或原始对象
     * @throws IllegalArgumentException 当函数名不受支持, 或函数参数类型不满足目标函数约束时抛出
     */
    private static Object applyFunction(final FunctionExpression expression, final Object args, final Object currentFieldValue) {
        String functionName = null == expression.getName() ? "" : expression.getName().trim().toLowerCase(Locale.ROOT);
        List<Object> values = new ArrayList<Object>(expression.getArgs().size());
        for (PredicateExpression arg : expression.getArgs()) {
            values.add(evaluate(arg, args, currentFieldValue));
        }
        switch (functionName) {
            case "length":
                return length(values, 0);
            case "lower":
                return unaryStringFunction(values, 0, UnaryStringOperator.LOWER);
            case "upper":
                return unaryStringFunction(values, 0, UnaryStringOperator.UPPER);
            case "trim":
                return unaryStringFunction(values, 0, UnaryStringOperator.TRIM);
            case "concat":
                return concat(values);
            case "coalesce":
                return coalesce(values);
            default:
                throw new IllegalArgumentException("Unsupported runtime function: " + expression.getName());
        }
    }

    /**
     * 拼接函数参数中的全部非 null 值.
     *
     * <p>
     * 该实现刻意忽略 {@code null} 参数, 并按参数出现顺序直接追加对象的字符串表现,
     * 用于在运行时模拟 DSL 中 {@code concat(...)} 的常见语义.
     * </p>
     *
     * @param values 已完成求值的函数参数列表, 允许包含 {@code null}
     * @return 由全部非空参数拼接得到的字符串；当所有参数均为空时返回空串
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
     * 返回第一个非 null 参数值.
     *
     * <p>
     * 该行为与 SQL/JPA 中 {@code coalesce(...)} 的核心语义保持一致：
     * 依次遍历参数并返回第一个非空值；若全部为空则返回 {@code null}.
     * </p>
     *
     * @param values 已完成求值的函数参数列表, 允许包含 {@code null}
     * @return 第一个非空参数；若不存在非空参数则返回 {@code null}
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
     * 尽量贴近 SQL/JPA `length(...)` 语义：入参为 null 时返回 null, 而不是回退为 0.
     *
     * <p>
     * 只有 {@link CharSequence} 类型参数才允许参与长度计算,
     * 从而避免把任意对象强制 {@code toString()} 后得到与数据库不一致的结果.
     * </p>
     *
     * @param values 已完成求值的函数参数列表
     * @param index  目标参数下标；越界时视为参数值为 {@code null}
     * @return 字符串长度；当目标参数为 {@code null} 时返回 {@code null}
     * @throws IllegalArgumentException 当目标参数存在但并非 {@link CharSequence} 时抛出
     */
    private static Integer length(final List<Object> values, final int index) {
        Object value = rawValue(values, index);
        if (Objects.isNull(value)) {
            return null;
        }
        if (!(value instanceof CharSequence)) {
            throw new IllegalArgumentException("Function length(...) only supports CharSequence arguments, but got: " + value.getClass().getName());
        }
        return Integer.valueOf(((CharSequence) value).length());
    }

    /**
     * 对 lower/upper/trim 等单参字符串函数做 SQL 风格空值传播.
     *
     * <p>
     * 该方法要求目标参数必须是单个字符串值, 并在参数为 {@code null} 时返回 {@code null},
     * 从而与数据库函数的空值传播行为保持一致, 而不是回退为空串.
     * </p>
     *
     * @param values   已完成求值的函数参数列表
     * @param index    目标参数下标；越界时视为参数值为 {@code null}
     * @param operator 需要执行的具体字符串操作符
     * @return 处理后的字符串；当目标参数为 {@code null} 时返回 {@code null}
     * @throws IllegalArgumentException 当目标参数存在但并非 {@link CharSequence} 时抛出
     */
    private static String unaryStringFunction(final List<Object> values, final int index, final UnaryStringOperator operator) {
        Object value = rawValue(values, index);
        if (Objects.isNull(value)) {
            return null;
        }
        if (!(value instanceof CharSequence)) {
            throw new IllegalArgumentException(
                "Function " + operator.functionName() + "(...) only supports CharSequence arguments, but got: " + value.getClass().getName());
        }
        String stringValue = value.toString();
        switch (operator) {
            case LOWER:
                return stringValue.toLowerCase(Locale.ROOT);
            case UPPER:
                return stringValue.toUpperCase(Locale.ROOT);
            case TRIM:
            default:
                return stringValue.trim();
        }
    }

    /**
     * 读取指定下标的函数参数.<br>
     *
     * <p>
     * 该方法只做安全读取, 不做默认值补偿, 也不进行任何类型转换.
     * 当调用方访问了不存在的参数位时, 返回 {@code null}, 以便上层自行决定是否按 SQL 空值语义处理.
     * </p>
     *
     * @param values 函数参数列表
     * @param index  参数下标
     * @return 指定下标的参数值；若下标越界则返回 {@code null}
     */
    private static Object rawValue(final List<Object> values, final int index) {
        return index < values.size() ? values.get(index) : null;
    }

    /**
     * 求值范围比较所需的起止值列表.
     *
     * <p>
     * `between` 风格比较在 canonical 模型中允许右值以“单个 range literal”或“右值 + 额外操作数”两种形式出现.
     * 该方法负责把这两类输入统一摊平成一个按顺序排列的值列表, 供后续严格校验“必须恰好为 2 个值”.
     * </p>
     *
     * @param right             主右值表达式, 可能本身就是一个范围集合, 也可能只是起始值
     * @param extra             额外操作数列表；当前只会使用第一个元素作为结束值
     * @param args              当前请求或查询对象, 用于解析参与范围比较的动态表达式
     * @param currentFieldValue 当前字段值, 用于解析参与范围比较的字段值引用
     * @return 已求值的范围值列表；返回结果可能为空、单值或多值, 最终有效性由调用方进一步校验
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
