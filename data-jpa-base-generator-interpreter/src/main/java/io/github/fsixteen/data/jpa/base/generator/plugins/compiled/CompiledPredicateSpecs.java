package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.CanonicalExpressionMapper;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionCardinality;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * 将注解元数据统一编译成谓词规格.
 *
 * <p>
 * 该工厂负责把 {@link CompiledAnnotationSpec} 中的 canonical 左右表达式、额外操作数
 * 和公共选项, 组合成可直接执行的 {@link CompiledPredicateSpec}.
 * compare、in、between、null-check 等不同形态都在这里完成最终规格归一.
 * </p>
 *
 * <p>
 * 这样 provider 与执行器都只依赖同一套规格对象, 不再自己判断 cardinality
 * 或重复调用表达式映射器.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateSpecs {

    private CompiledPredicateSpecs() {
    }

    /**
     * 编译普通二元比较规格.
     */
    public static CompiledPredicateSpec simple(final PredicateOperator operator, final CompiledAnnotationSpec<? extends Annotation> spec) {
        return CompiledPredicateSpec.of(operator, left(spec), right(spec, ExpressionCardinality.SINGLE), Collections.<PredicateExpression>emptyList(),
            PredicateOptionsSpec.of(spec));
    }

    /**
     * 编译 null-check 规格, 仅保留左侧表达式.
     */
    public static CompiledPredicateSpec nullCheck(final PredicateOperator operator, final CompiledAnnotationSpec<? extends Annotation> spec) {
        return CompiledPredicateSpec.of(operator, left(spec), null, Collections.<PredicateExpression>emptyList(), PredicateOptionsSpec.of(spec));
    }

    /**
     * 编译集合比较规格.
     */
    public static CompiledPredicateSpec in(final PredicateOperator operator, final CompiledAnnotationSpec<? extends Annotation> spec) {
        return CompiledPredicateSpec.of(operator, left(spec), right(spec, ExpressionCardinality.COLLECTION), Collections.<PredicateExpression>emptyList(),
            PredicateOptionsSpec.of(spec));
    }

    /**
     * 编译范围比较规格.
     */
    public static CompiledPredicateSpec between(final PredicateOperator operator, final CompiledAnnotationSpec<? extends Annotation> spec) {
        return CompiledPredicateSpec.of(operator, left(spec), right(spec, ExpressionCardinality.RANGE), extra(spec), PredicateOptionsSpec.of(spec));
    }

    /**
     * 直接基于给定函数表达式对构造函数-函数比较规格.
     */
    public static CompiledPredicateSpec functionPair(final PredicateOperator operator, final FunctionExpression left, final FunctionExpression right,
        final CompiledAnnotationSpec<? extends Annotation> spec) {
        return CompiledPredicateSpec.of(operator, left, right, Collections.<PredicateExpression>emptyList(), PredicateOptionsSpec.of(spec));
    }

    /**
     * 将左侧注解表达式映射为单值执行表达式.
     */
    private static PredicateExpression left(final CompiledAnnotationSpec<? extends Annotation> spec) {
        return CanonicalExpressionMapper.expression(spec.getPredicateCore().getLeft(), spec.getBindingPath(), ExpressionCardinality.SINGLE);
    }

    /**
     * 将右侧注解表达式按指定基数映射为执行表达式.
     */
    private static PredicateExpression right(final CompiledAnnotationSpec<? extends Annotation> spec, final ExpressionCardinality cardinality) {
        return CanonicalExpressionMapper.expression(spec.getPredicateCore().getRight(), spec.getBindingPath(), cardinality);
    }

    /**
     * 将附加表达式列表映射为统一执行表达式列表.
     */
    private static List<PredicateExpression> extra(final CompiledAnnotationSpec<? extends Annotation> spec) {
        List<PredicateExpression> expressions = new java.util.ArrayList<PredicateExpression>();
        if (Objects.isNull(spec.getPredicateCore().getExtra())) {
            return expressions;
        }
        for (io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr expr : spec.getPredicateCore().getExtra()) {
            PredicateExpression mapped = CanonicalExpressionMapper.expression(expr, spec.getBindingPath(), ExpressionCardinality.SINGLE);
            if (Objects.nonNull(mapped)) {
                expressions.add(mapped);
            }
        }
        return expressions;
    }

}
