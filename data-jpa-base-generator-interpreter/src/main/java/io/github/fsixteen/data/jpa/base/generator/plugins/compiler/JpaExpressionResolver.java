package io.github.fsixteen.data.jpa.base.generator.plugins.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.plugins.codecs.LiteralCodecs;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionSource;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.LiteralExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * JPA 表达式解析器.
 *
 * <p>
 * 该类型负责把统一表达式模型 {@link PredicateExpression} 解析为 JPA
 * {@link Expression}. 路径、运行时值、固定字面量和函数调用都在这里汇合,
 * 因此上游编译过程不需要关心表达式最终如何落到 Criteria API.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class JpaExpressionResolver {

    private JpaExpressionResolver() {
    }

    /**
     * 判断给定表达式在当前字段值上下文中是否具备基本的 JPA 可解析性.
     *
     * <p>
     * 该重载主要服务于“只依赖当前字段值、不依赖完整参数对象”的调用场景,
     * 内部会把 {@code args} 视为 {@code null} 并转交给完整重载处理.
     * </p>
     *
     * @param expression 待判断的 canonical 表达式；允许为 {@code null}
     * @param fieldValue 当前注解绑定字段的运行时值
     * @return 若表达式在当前上下文中可以继续解析为 JPA {@link Expression}, 返回 {@code true}；否则返回
     *         {@code false}
     */
    public static boolean canResolve(final PredicateExpression expression, final Object fieldValue) {
        return canResolve(expression, null, fieldValue);
    }

    /**
     * 判断给定 canonical 表达式在当前上下文中是否具备基本的 JPA 可解析性.
     *
     * <p>
     * 该判断属于“轻量级前置校验”, 目的是在真正调用 Criteria API 之前,
     * 先过滤掉显然无效的路径、无法读取的值路径、非法字面量或参数不完整的函数表达式,
     * 以避免错误延迟到更深层的谓词构建阶段才暴露.
     * </p>
     *
     * <p>
     * 当前判断规则包括：
     * 路径表达式要求路径字符串有效；
     * 值路径表达式要求运行时能解析出非空且非空白的路径值；
     * 字面量表达式要求能够被成功解码；
     * 函数表达式要求函数名非空, 且其非 custom 参数都可递归解析.
     * </p>
     *
     * @param expression 待判断的 canonical 表达式；允许为 {@code null}
     * @param args       当前查询参数对象；值路径与跨字段取值会从这里读取属性
     * @param fieldValue 当前注解绑定字段的运行时值
     * @return 若表达式在当前上下文中具备继续解析为 JPA 表达式的前提, 返回 {@code true}；否则返回 {@code false}
     */
    public static boolean canResolve(final PredicateExpression expression, final Object args, final Object fieldValue) {
        if (Objects.isNull(expression)) {
            return false;
        }
        ExpressionSource source = expression.getSource();
        // 先做一轮“静态可解析性”判断, 避免真正构建 Predicate 时才在深层函数参数里失败.
        if (ExpressionSource.PATH == source) {
            return ((PathExpression) expression).isValid();
        }
        if (ExpressionSource.FIELD_VALUE == source) {
            return true;
        }
        if (ExpressionSource.FIELD_VALUE_PATH == source) {
            Object runtimeValue = RuntimeValueResolver.resolve((io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression) expression,
                args, fieldValue);
            return Objects.nonNull(runtimeValue) && !Objects.toString(runtimeValue).trim().isEmpty();
        }
        if (ExpressionSource.LITERAL == source) {
            return canParse((LiteralExpression) expression);
        }
        if (ExpressionSource.FUNCTION == source) {
            FunctionExpression functionExpression = (FunctionExpression) expression;
            if (Objects.isNull(functionExpression.getName()) || functionExpression.getName().trim().isEmpty()) {
                return false;
            }
            for (PredicateExpression arg : functionExpression.getArgs()) {
                if (ExpressionSource.CUSTOM == arg.getSource()) {
                    continue;
                }
                if (!canResolve(arg, args, fieldValue)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 将 canonical 表达式解析为 JPA {@link Expression}, 并使用仅依赖字段值的上下文.
     *
     * <p>
     * 该重载适用于不需要访问完整参数对象的调用场景；
     * 内部会把 {@code args} 视为 {@code null} 并转交给完整重载处理.
     * </p>
     *
     * @param expression 待解析的 canonical 表达式；允许为 {@code null}
     * @param fieldValue 当前注解绑定字段的运行时值
     * @param root       当前查询根实体
     * @param query      当前查询对象；部分表达式类型可能不会直接使用它
     * @param cb         当前 CriteriaBuilder
     * @param <T>        目标 JPA 表达式泛型
     * @return 解析后的 JPA 表达式；若输入表达式为 {@code null} 或当前来源不支持解析, 则返回 {@code null}
     * @throws IllegalArgumentException 当函数参数数量或类型不满足约束时抛出
     */
    public static <T> Expression<T> resolve(final PredicateExpression expression, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        return resolve(expression, null, fieldValue, root, query, cb);
    }

    /**
     * 将 canonical 表达式解析为 JPA {@link Expression}.
     *
     * <p>
     * 这是当前 compiled 执行层把统一表达式模型落成 Criteria API 的核心入口.
     * 上游无需关心值来自实体路径、运行时字段值、固定字面量还是函数调用,
     * 都会在这里统一映射为可参与 Predicate 构建的 JPA 表达式.
     * </p>
     *
     * <p>
     * 当前支持的表达式来源包括：
     * 路径表达式；
     * 当前字段值/值路径表达式；
     * 固定字面量表达式；
     * 递归函数表达式.
     * </p>
     *
     * @param expression 待解析的 canonical 表达式；允许为 {@code null}
     * @param args       当前查询参数对象；值路径与函数参数求值会从这里读取属性
     * @param fieldValue 当前注解绑定字段的运行时值
     * @param root       当前查询根实体
     * @param query      当前查询对象；主要用于保持统一签名, 部分表达式分支不会直接使用
     * @param cb         当前 CriteriaBuilder
     * @param <T>        目标 JPA 表达式泛型
     * @return 解析后的 JPA 表达式；若输入表达式为 {@code null} 或当前来源不支持解析, 则返回 {@code null}
     * @throws IllegalArgumentException 当函数字符串参数校验失败, 或下游路径/字面量解析失败时抛出
     */
    public static <T> Expression<T> resolve(final PredicateExpression expression, final Object args, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        if (Objects.isNull(expression)) {
            return null;
        }
        // 统一表达式模型到这里才真正落地为 JPA Expression,
        // 上游不再关心数据来自字段值、固定 literal 还是路径引用.
        if (ExpressionSource.PATH == expression.getSource()) {
            return castExpression(JpaPathCompiler.compile(root, ((PathExpression) expression).getPath()));
        }
        if (ExpressionSource.FIELD_VALUE == expression.getSource()) {
            return literal(cb, RuntimeValueResolver.resolve((io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression) expression,
                args, fieldValue));
        }
        if (ExpressionSource.FIELD_VALUE_PATH == expression.getSource()) {
            Object runtimeValue = RuntimeValueResolver.resolve((io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression) expression,
                args, fieldValue);
            return castExpression(JpaPathCompiler.compile(root, Objects.toString(runtimeValue)));
        }
        if (ExpressionSource.LITERAL == expression.getSource()) {
            LiteralExpression literalExpression = (LiteralExpression) expression;
            return literal(cb, LiteralCodecs.parse(literalExpression.getRawValue(), literalExpression.getJavaType()));
        }
        if (ExpressionSource.FUNCTION == expression.getSource()) {
            FunctionExpression functionExpression = (FunctionExpression) expression;
            // 函数参数本身也是 PredicateExpression, 因此这里天然支持函数嵌套函数.
            return castExpression(resolveFunction(functionExpression, args, fieldValue, root, query, cb));
        }
        return null;
    }

    /**
     * 批量解析表达式列表, 并使用仅依赖字段值的上下文.
     *
     * <p>
     * 该重载适用于函数参数、子表达式列表等“不需要完整参数对象”的场景,
     * 内部会把 {@code args} 视为 {@code null} 并委托给完整重载执行.
     * </p>
     *
     * @param expressions 待批量解析的表达式列表；要求列表本身非 {@code null}
     * @param fieldValue  当前注解绑定字段的运行时值
     * @param root        当前查询根实体
     * @param query       当前查询对象
     * @param cb          当前 CriteriaBuilder
     * @return 解析后的 JPA 表达式数组；数组顺序与输入列表一致
     * @throws IllegalArgumentException 当某个表达式在解析过程中触发参数校验或字面量解析错误时抛出
     */
    public static Expression<?>[] resolve(final List<PredicateExpression> expressions, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return resolve(expressions, null, fieldValue, root, query, cb);
    }

    /**
     * 批量解析表达式列表为 JPA {@link Expression} 数组.
     *
     * <p>
     * 该方法主要服务于函数参数数组构建场景,
     * 会按输入顺序逐个调用单表达式解析入口, 并保持输出数组顺序稳定.
     * </p>
     *
     * @param expressions 待批量解析的表达式列表；要求列表本身非 {@code null}
     * @param args        当前查询参数对象
     * @param fieldValue  当前注解绑定字段的运行时值
     * @param root        当前查询根实体
     * @param query       当前查询对象
     * @param cb          当前 CriteriaBuilder
     * @return 解析后的 JPA 表达式数组；数组顺序与输入列表一致
     * @throws IllegalArgumentException 当某个表达式在解析过程中触发参数校验或字面量解析错误时抛出
     */
    public static Expression<?>[] resolve(final List<PredicateExpression> expressions, final Object args, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        List<Expression<?>> resolvedArgs = new ArrayList<Expression<?>>(expressions.size());
        for (PredicateExpression expression : expressions) {
            resolvedArgs.add(resolve(expression, args, fieldValue, root, query, cb));
        }
        return resolvedArgs.toArray(new Expression<?>[resolvedArgs.size()]);
    }

    /**
     * 解析函数表达式, 并在必要时补充字符串函数参数校验.
     *
     * <p>
     * 当前对 {@code length}、{@code lower}、{@code upper}、{@code trim}
     * 做了统一的参数个数与字符串类型约束, 其余函数仍按通用
     * {@link CriteriaBuilder#function(String, Class, Expression[])} 形式落地.
     * </p>
     *
     * <p>
     * 这里保留 `cb.function(...)` 作为最终落地方式, 是为了兼容现有调试代理、
     * 方言适配和既有函数渲染链路；字符串函数的专用校验只负责提前阻断明显非法的配置.
     * </p>
     *
     * @param functionExpression 待解析的函数表达式
     * @param args               当前查询参数对象
     * @param fieldValue         当前注解绑定字段的运行时值
     * @param root               当前查询根实体
     * @param query              当前查询对象
     * @param cb                 当前 CriteriaBuilder
     * @return 解析后的函数表达式
     * @throws IllegalArgumentException 当字符串函数参数数量或类型不满足约束时抛出
     */
    private static Expression<?> resolveFunction(final FunctionExpression functionExpression, final Object args, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        String functionName = Objects.isNull(functionExpression.getName()) ? "" : functionExpression.getName().trim().toLowerCase();
        Expression<?>[] arguments = resolve(functionExpression.getArgs(), args, fieldValue, root, query, cb);
        if ("length".equals(functionName)) {
            requireStringArgument(arguments, "length");
        } else if ("lower".equals(functionName)) {
            requireStringArgument(arguments, "lower");
        } else if ("upper".equals(functionName)) {
            requireStringArgument(arguments, "upper");
        } else if ("trim".equals(functionName)) {
            requireStringArgument(arguments, "trim");
        }
        return cb.function(functionExpression.getName(), castJavaType(functionExpression.getJavaType()), arguments);
    }

    /**
     * 校验字符串单参函数的参数数量与参数类型.
     *
     * <p>
     * 当前该校验服务于 {@code length}、{@code lower}、{@code upper}、
     * {@code trim} 四类字符串函数. 要求参数数量必须严格等于 1,
     * 且当 JPA 表达式已经暴露出明确 Java 类型时, 该类型必须实现
     * {@link CharSequence}.
     * </p>
     *
     * <p>
     * 若表达式类型仍为 {@code Object.class}, 这里会保守放行,
     * 把最终解释权交给后续运行时或底层 JPA provider.
     * </p>
     *
     * @param arguments    已解析完成的函数参数数组
     * @param functionName 当前待校验的函数名；仅用于拼装异常消息
     * @throws IllegalArgumentException 当参数数量不是 1, 或参数已知类型不是字符串类型时抛出
     */
    private static void requireStringArgument(final Expression<?>[] arguments, final String functionName) {
        if (1 != arguments.length) {
            throw new IllegalArgumentException("Function " + functionName + "(...) requires exactly 1 argument, but got: " + arguments.length);
        }
        Expression<?> argument = arguments[0];
        Class<?> javaType = null == argument ? null : argument.getJavaType();
        if (null != javaType && Object.class != javaType && !CharSequence.class.isAssignableFrom(javaType)) {
            throw new IllegalArgumentException("Function " + functionName + "(...) only supports CharSequence arguments, but got: " + javaType.getName());
        }
    }

    /**
     * 判断字面量表达式是否可以被当前 codec 成功解析.
     *
     * <p>
     * 该方法只做可解析性探测, 不保留解析结果.
     * 若底层 codec 抛出运行时异常, 则视为当前字面量不可解析.
     * </p>
     *
     * @param expression 待探测的字面量表达式
     * @return 若字面量可以按声明的 Java 类型成功解析, 返回 {@code true}；否则返回 {@code false}
     */
    private static boolean canParse(final LiteralExpression expression) {
        try {
            LiteralCodecs.parse(expression.getRawValue(), expression.getJavaType());
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    /**
     * 执行受控泛型桥接, 把通配符 JPA 表达式收窄为调用方期望的泛型.
     *
     * <p>
     * Criteria API 在很多入口上以 {@code Expression<?>} 暴露结果,
     * 这里仅负责做“调用方已经决定目标类型”的静态桥接, 不改变运行时对象本身.
     * </p>
     *
     * @param expression 待桥接的 JPA 表达式
     * @param <T>        调用方期望的表达式值类型
     * @return 桥接后的表达式引用
     * @throws ClassCastException 当调用方对目标泛型的假设与实际不兼容时抛出
     */
    @SuppressWarnings("unchecked")
    private static <T> Expression<T> castExpression(final Expression<?> expression) {
        // JPA Criteria API 自身以通配符暴露大部分表达式类型, 这里只做“调用方已经决定目标类型”的桥接.
        return (Expression<T>) expression;
    }

    /**
     * 将普通 Java 值包装为 Criteria API 字面量表达式.
     *
     * @param cb    当前 CriteriaBuilder
     * @param value 待包装的运行时值；允许为 {@code null}
     * @param <T>   字面量表达式值类型
     * @return 由 {@link CriteriaBuilder#literal(Object)} 创建的字面量表达式
     */
    @SuppressWarnings("unchecked")
    private static <T> Expression<T> literal(final CriteriaBuilder cb, final Object value) {
        // cb.literal 的返回泛型依赖调用点语境, 运行时值本身已经是最终 literal.
        return cb.literal((T) value);
    }

    /**
     * 将通配符 {@link Class} 收窄为 CriteriaBuilder.function 所需的目标返回类型.
     *
     * @param javaType DSL 上声明的函数返回类型
     * @param <T>      调用方期望的函数返回泛型
     * @return 收窄后的目标类型引用
     * @throws ClassCastException 当调用方对目标泛型的假设与实际不兼容时抛出
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> castJavaType(final Class<?> javaType) {
        // 函数返回类型由注解 DSL 直接声明, 这里只把 Class<?> 收窄回 CriteriaBuilder.function 需要的签名.
        return (Class<T>) javaType;
    }

}
