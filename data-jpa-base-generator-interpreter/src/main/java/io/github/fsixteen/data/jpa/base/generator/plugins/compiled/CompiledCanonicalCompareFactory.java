package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.reflect.Field;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.CanonicalExpressionMapper;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionCardinality;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * canonical Compare synthetic spec 工厂.
 *
 * <p>
 * specialized compiled builder 经常需要把快捷注解配置补全为标准 Compare 规格,
 * 例如 InTable 默认 where leaf、Cases 的 when/then canonical 分支.
 * 这些补全过程统一收口到这里, 避免每个 builder 各自维护一套默认补全规则.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class CompiledCanonicalCompareFactory {

    private CompiledCanonicalCompareFactory() {
    }

    /**
     * 解析表达式中实际配置的路径, 未配置时回退到默认路径.
     *
     * @param expr     原始表达式定义
     * @param fallback 当表达式未显式声明有效路径时使用的默认路径
     * @return 实际生效的路径字符串
     */
    static String configuredPath(final Expr expr, final String fallback) {
        PredicateExpression expression = CanonicalExpressionMapper.expression(expr, fallback, ExpressionCardinality.SINGLE);
        if (expression instanceof PathExpression && Objects.nonNull(((PathExpression) expression).getPath())
            && !((PathExpression) expression).getPath().isEmpty()) {
            return ((PathExpression) expression).getPath();
        }
        return fallback;
    }

    /**
     * 将 path/auto 缺省表达式补齐为明确的 path 表达式.
     *
     * <p>
     * 该方法用于把“未显式配置但语义上应落到某个默认路径”的表达式补齐,
     * 从而避免上游 builder 在多处重复维护 path fallback 规则.
     * </p>
     *
     * @param expr     原始表达式定义
     * @param fallback 默认路径
     * @return 补齐后的表达式；若原表达式已显式配置语义则直接返回原值
     */
    static Expr pathOrFallback(final Expr expr, final String fallback) {
        if (Objects.isNull(expr)) {
            return SyntheticAnnotations.pathExpr(fallback);
        }
        if (ExprType.PATH == expr.type() && expr.path().isEmpty()) {
            return SyntheticAnnotations.pathExpr(fallback);
        }
        if (ExprType.AUTO == expr.type() && !CanonicalExpressionMapper.isConfigured(expr)) {
            return SyntheticAnnotations.pathExpr(fallback);
        }
        return expr;
    }

    /**
     * 判断给定表达式是否显式配置了任何语义.
     *
     * @param expr 原始表达式定义
     * @return 只要表达式超出默认零配置形态即返回 {@code true}
     */
    static boolean isConfigured(final Expr expr) {
        return CanonicalExpressionMapper.isConfigured(expr);
    }

    /**
     * 判断一组 compare 参数是否超出了默认等值比较的零配置形态.
     *
     * @param op    compare 操作符
     * @param left  左表达式
     * @param right 右表达式
     * @param extra 额外操作数
     * @return 只要 compare 配置不是“默认 EQ 且无任何显式表达式/extra”即返回 {@code true}
     */
    static boolean hasConfiguredCompare(final CompareOp op, final Expr left, final Expr right, final Expr[] extra) {
        return CompareOp.EQ != op || isConfigured(left) || isConfigured(right) || null != extra && 0 < extra.length;
    }

    /**
     * 判断 compare 注解是否显式配置了自定义语义.
     *
     * @param compare compare 注解
     * @return 注解非空且配置超出默认零配置形态时返回 {@code true}
     */
    static boolean hasConfiguredCompare(final Compare compare) {
        return null != compare && hasConfiguredCompare(compare.op(), compare.left(), compare.right(), compare.extra());
    }

    /**
     * 基于显式 compare 参数构造一份合成 compare 规格.
     *
     * @param objClass   宿主参数类型
     * @param valueField 绑定字段
     * @param op         compare 操作符
     * @param left       左表达式
     * @param right      右表达式
     * @param extra      额外操作数
     * @return 合成后的 compare 注解规格
     */
    static CompiledAnnotationSpec<Compare> compareSpec(final Class<?> objClass, final Field valueField, final CompareOp op, final Expr left, final Expr right,
        final Expr[] extra) {
        return CompiledAnnotationSpec.of(objClass, SyntheticAnnotations.compare(op, left, right, extra), valueField);
    }

    /**
     * 基于原始 compare 注解和左右默认路径构造一份补齐后的 compare 规格.
     *
     * @param objClass      宿主参数类型
     * @param valueField    绑定字段
     * @param compare       原始 compare 注解
     * @param leftFallback  左表达式默认路径
     * @param rightFallback 右表达式默认路径
     * @return 补齐默认路径后的 compare 注解规格
     */
    static CompiledAnnotationSpec<Compare> compareSpec(final Class<?> objClass, final Field valueField, final Compare compare, final String leftFallback,
        final String rightFallback) {
        Expr left = pathOrFallback(compare.left(), leftFallback);
        Expr right = pathOrFallback(compare.right(), rightFallback);
        return compareSpec(objClass, valueField, compare.op(), left, right, compare.extra());
    }

    /**
     * 创建一条 path = path 的默认等值 compare 规格.
     *
     * @param objClass   宿主参数类型
     * @param valueField 绑定字段
     * @param leftPath   左侧路径
     * @param rightPath  右侧路径
     * @return 合成后的默认等值 compare 规格
     */
    static CompiledAnnotationSpec<Compare> eqPathCompareSpec(final Class<?> objClass, final Field valueField, final String leftPath, final String rightPath) {
        return compareSpec(objClass, valueField, CompareOp.EQ, SyntheticAnnotations.pathExpr(leftPath), SyntheticAnnotations.pathExpr(rightPath), new Expr[0]);
    }

    /**
     * 创建一条 path = value 的默认等值 compare 规格.
     *
     * @param objClass   宿主参数类型
     * @param valueField 绑定字段
     * @param leftPath   左侧路径
     * @return 合成后的默认 path=value compare 规格
     */
    static CompiledAnnotationSpec<Compare> eqValueCompareSpec(final Class<?> objClass, final Field valueField, final String leftPath) {
        return compareSpec(objClass, valueField, CompareOp.EQ, SyntheticAnnotations.pathExpr(leftPath), SyntheticAnnotations.valueExpr(), new Expr[0]);
    }

}
