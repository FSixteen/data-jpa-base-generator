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
 * JPA 表达式解析器。
 *
 * <p>
 * 该类型负责把统一表达式模型 {@link PredicateExpression} 解析为 JPA
 * {@link Expression}。路径、运行时值、固定字面量和函数调用都在这里汇合，
 * 因此上游编译过程不需要关心表达式最终如何落到 Criteria API。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class JpaExpressionResolver {

    private JpaExpressionResolver() {
    }

    public static boolean canResolve(final PredicateExpression expression, final Object fieldValue) {
        return canResolve(expression, null, fieldValue);
    }

    public static boolean canResolve(final PredicateExpression expression, final Object args, final Object fieldValue) {
        if (Objects.isNull(expression)) {
            return false;
        }
        ExpressionSource source = expression.getSource();
        // 先做一轮“静态可解析性”判断，避免真正构建 Predicate 时才在深层函数参数里失败。
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

    public static <T> Expression<T> resolve(final PredicateExpression expression, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        return resolve(expression, null, fieldValue, root, query, cb);
    }

    public static <T> Expression<T> resolve(final PredicateExpression expression, final Object args, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        if (Objects.isNull(expression)) {
            return null;
        }
        // 统一表达式模型到这里才真正落地为 JPA Expression，
        // 上游不再关心数据来自字段值、固定 literal 还是路径引用。
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
            // 函数参数本身也是 PredicateExpression，因此这里天然支持函数嵌套函数。
            return cb.function(functionExpression.getName(), castJavaType(functionExpression.getJavaType()),
                resolve(functionExpression.getArgs(), args, fieldValue, root, query, cb));
        }
        return null;
    }

    public static Expression<?>[] resolve(final List<PredicateExpression> expressions, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return resolve(expressions, null, fieldValue, root, query, cb);
    }

    public static Expression<?>[] resolve(final List<PredicateExpression> expressions, final Object args, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        List<Expression<?>> resolvedArgs = new ArrayList<Expression<?>>(expressions.size());
        for (PredicateExpression expression : expressions) {
            resolvedArgs.add(resolve(expression, args, fieldValue, root, query, cb));
        }
        return resolvedArgs.toArray(new Expression<?>[resolvedArgs.size()]);
    }

    private static boolean canParse(final LiteralExpression expression) {
        try {
            LiteralCodecs.parse(expression.getRawValue(), expression.getJavaType());
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Expression<T> castExpression(final Expression<?> expression) {
        // JPA Criteria API 自身以通配符暴露大部分表达式类型，这里只做“调用方已经决定目标类型”的桥接。
        return (Expression<T>) expression;
    }

    @SuppressWarnings("unchecked")
    private static <T> Expression<T> literal(final CriteriaBuilder cb, final Object value) {
        // cb.literal 的返回泛型依赖调用点语境，运行时值本身已经是最终 literal。
        return cb.literal((T) value);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castJavaType(final Class<?> javaType) {
        // 函数返回类型由注解 DSL 直接声明，这里只把 Class<?> 收窄回 CriteriaBuilder.function 需要的签名。
        return (Class<T>) javaType;
    }

}
