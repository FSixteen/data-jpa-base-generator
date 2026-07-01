package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.JpaPathCompiler;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry;

/**
 * compiled 子查询公共支撑。
 *
 * <p>
 * 统一处理 select / where 的公共拼装逻辑，避免 `InTable`、`Exists` 等子查询能力各自维护一套。
 * </p>
 *
 * <p>
 * 该类负责把 {@link CompiledSubquerySpec} 真正展开成 JPA {@link Subquery}：
 * 包括子查询根对象创建、select 路径编译、关联字段约束以及 where 分组递归执行。
 * 因而所有子查询语义最终都只是在外层谓词包裹方式上不同。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledSubquerySupport {

    private CompiledSubquerySupport() {
    }

    /**
     * 在子查询上创建 from 根并应用 select 路径。
     */
    public static Root<?> select(final CompiledSubquerySpec spec, final Subquery<?> subQuery) {
        return selectTyped(spec, subQuery);
    }

    private static <T> Root<?> selectTyped(final CompiledSubquerySpec spec, final Subquery<T> subQuery) {
        Root<?> subRoot = subQuery.from(spec.getFromEntity());
        subQuery.select(pathExpression(subRoot, spec.getSelectPath()));
        return subRoot;
    }

    /**
     * 在 required 且字段值为 null 时生成“绑定路径 is null”短路谓词。
     */
    public static Predicate requiredNullPredicate(final CompiledAnnotationSpec<?> spec, final Object fieldValue, final Root<?> root) {
        return spec.getEffectiveOptions().isRequired() && Objects.isNull(fieldValue) ? JpaPathCompiler.compile(root, spec.getBindingPath()).isNull() : null;
    }

    /**
     * 构造一条完整子查询，包括 select、where 和可选关联约束。
     */
    public static Subquery<?> createSubquery(final CompiledSubquerySpec spec, final Path<?> outerColumn,
        final Class<? extends Annotation> recursiveAnnotationType, final Object args, final Root<?> outerRoot, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        requireQuery(query);
        Subquery<?> subQuery = query.subquery(outerColumn.getJavaType());
        Root<?> subRoot = select(spec, subQuery);
        subQuery.where(resolveWherePredicate(spec, recursiveAnnotationType, args, outerRoot, subRoot, subQuery, cb));
        return subQuery;
    }

    /**
     * 如外层选项声明了 not，则对最终子查询谓词做一次取反。
     */
    public static Predicate applyOuterNegate(final CompiledAnnotationSpec<?> spec, final Predicate predicate) {
        return spec.getEffectiveOptions().isNegate() ? predicate.not() : predicate;
    }

    /**
     * 校验当前执行环境允许创建子查询。
     */
    public static void requireQuery(final AbstractQuery<?> query) {
        if (Objects.isNull(query)) {
            throw new IllegalArgumentException("Subquery-based predicates require a non-null AbstractQuery");
        }
    }

    /**
     * 根据子查询模式分发到 in-table 或 exists/not-exists 执行路径。
     */
    public static Predicate create(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        CompiledSubquerySpec subquerySpec = CompiledSpecializedSpecs.subquery(spec);
        SubqueryMode mode = subquerySpec.getMode();
        if (SubqueryMode.IN == mode) {
            return createInTable(spec, subquerySpec, args, root, query, cb);
        }
        return createExists(spec, subquerySpec, args, root, query, cb, SubqueryMode.NOT_EXISTS == mode);
    }

    /**
     * 执行 in (subquery) 语义。
     */
    private static Predicate createInTable(final CompiledAnnotationSpec<? extends Annotation> spec, final CompiledSubquerySpec subquerySpec, final Object args,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        Object fieldValue = spec.readAndTrim(args);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (Objects.nonNull(nullPredicate)) {
            return nullPredicate;
        }
        if (spec.shouldIgnore(fieldValue)) {
            return null;
        }
        Path<?> outerColumn = JpaPathCompiler.compile(root, subquerySpec.getSourcePath());
        Subquery<?> subQuery = createSubquery(subquerySpec, outerColumn, spec.getAnnotationType(), args, root, query, cb);
        return applyOuterNegate(spec, outerColumn.in(subQuery));
    }

    /**
     * 执行 exists / not exists 语义。
     */
    private static Predicate createExists(final CompiledAnnotationSpec<? extends Annotation> spec, final CompiledSubquerySpec subquerySpec, final Object args,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb, final boolean negate) {
        Object fieldValue = spec.readAndTrim(args);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (Objects.nonNull(nullPredicate)) {
            return nullPredicate;
        }
        if (!Objects.equals(Boolean.TRUE, fieldValue) || spec.shouldIgnore(fieldValue) || Objects.isNull(query)) {
            return null;
        }
        Path<?> outerColumn = JpaPathCompiler.compile(root, subquerySpec.getSourcePath());
        Subquery<?> subQuery = createSubquery(subquerySpec, outerColumn, spec.getAnnotationType(), args, root, query, cb);
        Predicate predicate = cb.exists(subQuery);
        if (negate) {
            predicate = predicate.not();
        }
        return applyOuterNegate(spec, predicate);
    }

    /**
     * 组装子查询 where 条件，包括关联约束、嵌套叶子和 group 树。
     */
    public static Predicate resolveWherePredicate(final CompiledSubquerySpec spec, final Class<? extends Annotation> recursiveAnnotationType, final Object args,
        final Root<?> outerRoot, final Root<?> subRoot, final AbstractQuery<?> subQuery, final CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (Objects.nonNull(spec.getCorrelatedPath()) && !spec.getCorrelatedPath().isEmpty()) {
            predicates.add(cb.equal(JpaPathCompiler.compile(subRoot, spec.getCorrelatedPath()), JpaPathCompiler.compile(outerRoot, spec.getSourcePath())));
        }
        Predicate nestedPredicate = resolveNestedPredicate(spec, args, subRoot, subQuery, cb);
        if (Objects.nonNull(nestedPredicate)) {
            predicates.add(nestedPredicate);
        }
        Predicate groupPredicate = resolveGroupPredicate(spec, recursiveAnnotationType, args, subRoot, subQuery, cb);
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

    /**
     * 递归执行子查询内部的 group 树。
     */
    private static Predicate resolveGroupPredicate(final CompiledSubquerySpec spec, final Class<? extends Annotation> recursiveAnnotationType,
        final Object args, final Root<?> subRoot, final AbstractQuery<?> subQuery, final CriteriaBuilder cb) {
        if (shouldResolveFromGroup(spec, recursiveAnnotationType)) {
            return CompiledPredicateGroupResolver.create(spec.getPredicateGroupSpec(), args, subRoot, subQuery, cb);
        }
        return null;
    }

    /**
     * 执行子查询上的单个嵌套叶子注解。
     */
    private static Predicate resolveNestedPredicate(final CompiledSubquerySpec spec, final Object args, final Root<?> subRoot, final AbstractQuery<?> subQuery,
        final CriteriaBuilder cb) {
        if (Objects.nonNull(spec.getNestedPredicateSpec())) {
            return CompiledPredicateProviderRegistry.require(spec.getNestedPredicateSpec().getAnnotationType()).create(spec.getNestedPredicateSpec(), args,
                subRoot, subQuery, cb);
        }
        return null;
    }

    /**
     * 防止子查询分组树再次回到当前特殊注解自身，造成递归。
     */
    private static boolean shouldResolveFromGroup(final CompiledSubquerySpec spec, final Class<? extends Annotation> recursiveAnnotationType) {
        PredicateGroupSpec groupSpec = spec.getPredicateGroupSpec();
        if (Objects.isNull(groupSpec) || groupSpec.isEmpty()) {
            return false;
        }
        if (!groupSpec.getGroups().isEmpty()) {
            return true;
        }
        for (CompiledAnnotationSpec<?> annotation : groupSpec.getAnnotations()) {
            if (recursiveAnnotationType == annotation.getAnnotationType()) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static <T> Expression<T> pathExpression(final Root<?> root, final String path) {
        // Subquery.select 需要与 Subquery<T> 对齐的精确 Expression<T>；点路径编译后只能在这里做一次受控收窄。
        return (Expression<T>) JpaPathCompiler.compileExpression(root, path);
    }

}
