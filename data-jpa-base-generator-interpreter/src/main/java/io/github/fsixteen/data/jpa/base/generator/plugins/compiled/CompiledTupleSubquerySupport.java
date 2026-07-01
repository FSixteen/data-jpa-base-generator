package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.JpaPathCompiler;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry;

/**
 * 多列 tuple 子查询执行器。
 *
 * <p>
 * 当前组件负责把 {@link CompiledTupleSubquerySpec} 落成最终的
 * {@code exists/not exists} JPA Predicate。与单列 {@link CompiledSubquerySupport}
 * 的区别在于：这里的相关条件不是一条 source/correlated path，而是一组 tuple pairs。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTupleSubquerySupport {

    private CompiledTupleSubquerySupport() {
    }

    public static Predicate create(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final javax.persistence.criteria.CriteriaBuilder cb) {
        CompiledTupleSubquerySpec spec = CompiledTupleSubquerySpecs.tupleSubquery(ownerSpec);
        Object fieldValue = ownerSpec.readAndTrim(args);
        Predicate nullPredicate = CompiledSubquerySupport.requiredNullPredicate(ownerSpec, fieldValue, root);
        if (Objects.nonNull(nullPredicate)) {
            return nullPredicate;
        }
        if (!Objects.equals(Boolean.TRUE, fieldValue) || ownerSpec.shouldIgnore(fieldValue) || Objects.isNull(query)) {
            return null;
        }
        Subquery<?> subQuery = query.subquery(resolveSelectType(root, spec));
        Root<?> subRoot = subQuery.from(spec.getTargetEntity());
        subQuery.select(selectExpression(subRoot, spec));
        Predicate where = resolveWherePredicate(spec, args, root, subRoot, subQuery, cb);
        if (Objects.nonNull(where)) {
            subQuery.where(where);
        }
        Predicate predicate = cb.exists(subQuery);
        if (spec.isNegate()) {
            predicate = predicate.not();
        }
        return CompiledSubquerySupport.applyOuterNegate(ownerSpec, predicate);
    }

    private static Class<?> resolveSelectType(final Root<?> outerRoot, final CompiledTupleSubquerySpec spec) {
        return JpaPathCompiler.compile(outerRoot, spec.getPairs().get(0).getLeftPath()).getJavaType();
    }

    @SuppressWarnings("unchecked")
    private static <T> Expression<T> selectExpression(final Root<?> subRoot, final CompiledTupleSubquerySpec spec) {
        return (Expression<T>) JpaPathCompiler.compileExpression(subRoot, spec.getPairs().get(0).getRightPath());
    }

    private static Predicate resolveWherePredicate(final CompiledTupleSubquerySpec spec, final Object args, final Root<?> outerRoot, final Root<?> subRoot,
        final AbstractQuery<?> subQuery, final javax.persistence.criteria.CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        for (CompiledTupleCorrelationSpec pair : spec.getPairs()) {
            predicates.add(cb.equal(JpaPathCompiler.compile(subRoot, pair.getRightPath()), JpaPathCompiler.compile(outerRoot, pair.getLeftPath())));
        }
        Predicate nestedPredicate = resolveNestedPredicate(spec, args, subRoot, subQuery, cb);
        if (Objects.nonNull(nestedPredicate)) {
            predicates.add(nestedPredicate);
        }
        Predicate groupPredicate = resolveGroupPredicate(spec, args, subRoot, subQuery, cb);
        if (Objects.nonNull(groupPredicate)) {
            predicates.add(groupPredicate);
        }
        if (predicates.isEmpty()) {
            return null;
        }
        if (1 == predicates.size()) {
            return predicates.get(0);
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private static Predicate resolveNestedPredicate(final CompiledTupleSubquerySpec spec, final Object args, final Root<?> subRoot,
        final AbstractQuery<?> subQuery, final javax.persistence.criteria.CriteriaBuilder cb) {
        if (Objects.nonNull(spec.getNestedPredicateSpec())) {
            return CompiledPredicateProviderRegistry.require(spec.getNestedPredicateSpec().getAnnotationType()).create(spec.getNestedPredicateSpec(), args,
                subRoot, subQuery, cb);
        }
        return null;
    }

    private static Predicate resolveGroupPredicate(final CompiledTupleSubquerySpec spec, final Object args, final Root<?> subRoot,
        final AbstractQuery<?> subQuery, final javax.persistence.criteria.CriteriaBuilder cb) {
        PredicateGroupSpec groupSpec = spec.getPredicateGroupSpec();
        if (Objects.isNull(groupSpec) || groupSpec.isEmpty()) {
            return null;
        }
        for (CompiledAnnotationSpec<?> annotation : groupSpec.getAnnotations()) {
            if (spec.getOwnerSpec().getAnnotationType() == annotation.getAnnotationType()) {
                return null;
            }
        }
        return CompiledPredicateGroupResolver.create(groupSpec, args, subRoot, subQuery, cb);
    }

}
