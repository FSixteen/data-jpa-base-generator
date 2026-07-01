package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 多列 tuple 子查询的 compiled 规格。
 *
 * <p>
 * 当前类型承接 {@code TupleExists / TupleNotExists} 的稳定快照：
 * 子查询实体、多列关联、可选默认附加叶子以及附加 where-group 都在这里统一汇合。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTupleSubquerySpec {

    private final CompiledAnnotationSpec<? extends Annotation> ownerSpec;

    private final Class<?> targetEntity;

    private final boolean negate;

    private final List<CompiledTupleCorrelationSpec> pairs;

    private final CompiledAnnotationSpec<?> nestedPredicateSpec;

    private final PredicateGroupSpec predicateGroupSpec;

    private CompiledTupleSubquerySpec(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final Class<?> targetEntity, final boolean negate,
        final List<CompiledTupleCorrelationSpec> pairs, final CompiledAnnotationSpec<?> nestedPredicateSpec, final PredicateGroupSpec predicateGroupSpec) {
        this.ownerSpec = ownerSpec;
        this.targetEntity = targetEntity;
        this.negate = negate;
        this.pairs = Collections.unmodifiableList(new ArrayList<CompiledTupleCorrelationSpec>(pairs));
        this.nestedPredicateSpec = nestedPredicateSpec;
        this.predicateGroupSpec = predicateGroupSpec;
    }

    public static CompiledTupleSubquerySpec of(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final Class<?> targetEntity, final boolean negate,
        final List<CompiledTupleCorrelationSpec> pairs, final CompiledAnnotationSpec<?> nestedPredicateSpec, final PredicateGroupSpec predicateGroupSpec) {
        return new CompiledTupleSubquerySpec(ownerSpec, targetEntity, negate, pairs, nestedPredicateSpec, predicateGroupSpec);
    }

    public CompiledAnnotationSpec<? extends Annotation> getOwnerSpec() {
        return this.ownerSpec;
    }

    public Class<?> getTargetEntity() {
        return this.targetEntity;
    }

    public boolean isNegate() {
        return this.negate;
    }

    public List<CompiledTupleCorrelationSpec> getPairs() {
        return this.pairs;
    }

    public CompiledAnnotationSpec<?> getNestedPredicateSpec() {
        return this.nestedPredicateSpec;
    }

    public PredicateGroupSpec getPredicateGroupSpec() {
        return this.predicateGroupSpec;
    }

}
