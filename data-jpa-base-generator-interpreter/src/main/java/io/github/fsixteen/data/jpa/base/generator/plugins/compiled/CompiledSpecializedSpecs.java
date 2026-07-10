package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Case;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseElse;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Cases;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Exists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryPredicate;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.MetaAnnotationAttributes;

/**
 * 特殊能力注解的编译器.
 *
 * <p>
 * 普通比较类注解通常可直接映射为统一谓词规格；
 * 而 {@code Cases}、{@code InTable}、{@code Exists}、{@code NotExists}、
 * {@code SubqueryPredicate} 这类注解需要额外的结构化编译步骤.
 * 这些 specialized spec 的产出统一收口到这里.
 * </p>
 *
 * <p>
 * 这里的职责是把“注解专属结构”转成 compiled 主链路可消费的中间规格,
 * 例如 case 分支列表、子查询结构与默认补全后的 Compare 叶子节点,
 * 而不是直接生成最终 JPA Predicate.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledSpecializedSpecs {

    private CompiledSpecializedSpecs() {
    }

    /**
     * 将一个 {@link Cases} 注解编译为不可变分支规格列表.
     */
    public static List<CompiledCaseBranchSpec> cases(final CompiledAnnotationSpec<Cases> spec) throws ReflectiveOperationException {
        Cases cases = spec.getAnnotation();
        String defaultField = CompiledCanonicalCompareFactory.configuredPath(spec.getPredicateCore().getLeft(), spec.getBindingPath());
        PredicateOptionsSpec defaultOptions = spec.getEffectiveOptions();
        List<CompiledCaseBranchSpec> branches = new ArrayList<CompiledCaseBranchSpec>(cases.value().length);
        for (Case cs : cases.value()) {
            String fieldName = CompiledCanonicalCompareFactory.configuredPath(cs.left(), defaultField);
            PredicateOptionsSpec branchOptions = PredicateOptionsSpec.inherit(defaultOptions, cs.options());
            CompiledCasesResolver.ResolvedWhen resolvedWhen = CompiledCasesResolver.resolveWhen(spec, cs.when());
            CompiledCasesResolver.ResolvedThen resolvedThen = CompiledCasesResolver.resolveThen(spec, cs.then());
            branches.add(CompiledCaseBranchSpec.of(fieldName, branchOptions, resolvedWhen.getMatchMode(), resolvedWhen.getPredicate(),
                canonicalCaseWhenSpec(spec, fieldName, cs.when(), resolvedWhen.getMatchMode()), resolvedWhen.getGroupSpec(), resolvedThen.getActionMode(),
                canonicalCasePredicateSpec(spec, fieldName, cs.then(), resolvedThen.hasCanonicalPredicate()), resolvedThen.getGroupSpec(),
                resolvedThen.getProcessor()));
        }
        CaseElse otherwise = cases.otherwise();
        if (null != otherwise && otherwise.enabled()) {
            String fieldName = CompiledCanonicalCompareFactory.configuredPath(otherwise.left(), defaultField);
            PredicateOptionsSpec branchOptions = PredicateOptionsSpec.inherit(defaultOptions, otherwise.options());
            CompiledCasesResolver.ResolvedThen resolvedThen = CompiledCasesResolver.resolveThen(spec, otherwise.then());
            branches.add(CompiledCaseBranchSpec.of(fieldName, branchOptions, CompiledCaseBranchSpec.MatchMode.ALWAYS, null, null, null,
                resolvedThen.getActionMode(), canonicalCasePredicateSpec(spec, fieldName, otherwise.then(), resolvedThen.hasCanonicalPredicate()),
                resolvedThen.getGroupSpec(), resolvedThen.getProcessor()));
        }
        return Collections.unmodifiableList(branches);
    }

    /**
     * 将任意子查询家族注解编译为统一子查询规格.
     */
    public static CompiledSubquerySpec subquery(final CompiledAnnotationSpec<? extends Annotation> spec) {
        return buildSubquerySpec(CanonicalSubquerySource.of(spec));
    }

    /**
     * 将 {@link InTable} 编译为统一子查询规格.
     */
    public static CompiledSubquerySpec inTable(final CompiledAnnotationSpec<InTable> spec) {
        return subquery(spec);
    }

    /**
     * 将 {@link Exists} 编译为统一子查询规格.
     */
    public static CompiledSubquerySpec exists(final CompiledAnnotationSpec<Exists> spec) {
        return subquery(spec);
    }

    /**
     * 为 case 分支补齐最终执行的 canonical compare 谓词.
     */
    private static CompiledAnnotationSpec<Compare> canonicalCasePredicateSpec(final CompiledAnnotationSpec<Cases> ownerSpec, final String fieldName,
        final io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseThen then, final boolean hasCanonicalPredicate) {
        if (hasCanonicalPredicate) {
            return CompiledCanonicalCompareFactory.compareSpec(ownerSpec.getObjClass(), ownerSpec.getValueField(), then.op(),
                SyntheticAnnotations.pathExpr(fieldName), then.right(), then.extra());
        }
        return CompiledCanonicalCompareFactory.eqValueCompareSpec(ownerSpec.getObjClass(), ownerSpec.getValueField(), fieldName);
    }

    /**
     * 为 case 分支补齐命中判断所需的 canonical compare 谓词.
     */
    private static CompiledAnnotationSpec<Compare> canonicalCaseWhenSpec(final CompiledAnnotationSpec<Cases> ownerSpec, final String fieldName,
        final io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhen when, final CompiledCaseBranchSpec.MatchMode matchMode) {
        if (CompiledCaseBranchSpec.MatchMode.CANONICAL != matchMode) {
            return null;
        }
        return CompiledCanonicalCompareFactory.compareSpec(ownerSpec.getObjClass(), ownerSpec.getValueField(), when.op(),
            CompiledCanonicalCompareFactory.pathOrFallback(when.left(), fieldName), CompiledCanonicalCompareFactory.pathOrFallback(when.right(), fieldName),
            when.extra());
    }

    /**
     * 将标准化子查询源信息转为统一子查询规格.
     */
    private static CompiledSubquerySpec buildSubquerySpec(final CanonicalSubquerySource source) {
        if (SubqueryMode.IN == source.getMode()) {
            return CompiledSubquerySpec.of(source.getMode(), source.getTargetEntity(), source.getRightPath(), source.getSourcePath(),
                inTableNestedPredicateSpec(source.getOwnerSpec(), source.getRightPath(), source.getWhereCompare()), source.getPredicateGroupSpec());
        }
        return CompiledSubquerySpec.of(source.getMode(), source.getTargetEntity(), source.getSelectPath(), source.getSourcePath(), source.getRightPath(), null,
            source.getPredicateGroupSpec());
    }

    private static final class CanonicalSubquerySource {

        private final CompiledAnnotationSpec<? extends Annotation> ownerSpec;

        private final SubqueryMode mode;

        private final Class<?> targetEntity;

        private final String sourcePath;

        private final String rightPath;

        private final String selectPath;

        private final PredicateGroupSpec predicateGroupSpec;

        private final Compare whereCompare;

        private CanonicalSubquerySource(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final SubqueryMode mode, final Class<?> targetEntity,
            final String sourcePath, final String rightPath, final String selectPath, final PredicateGroupSpec predicateGroupSpec, final Compare whereCompare) {
            this.ownerSpec = ownerSpec;
            this.mode = mode;
            this.targetEntity = targetEntity;
            this.sourcePath = sourcePath;
            this.rightPath = rightPath;
            this.selectPath = selectPath;
            this.predicateGroupSpec = predicateGroupSpec;
            this.whereCompare = whereCompare;
        }

        static CanonicalSubquerySource of(final CompiledAnnotationSpec<? extends Annotation> spec) {
            Annotation annotation = spec.getAnnotation();
            String defaultPath = spec.getBindingPath();
            if (annotation instanceof SubqueryPredicate) {
                SubqueryPredicate subquery = SubqueryPredicate.class.cast(annotation);
                return build(spec, subquery.mode(), subquery.targetEntity(), subquery.left(), subquery.right(), subquery.select(),
                    CompiledSubqueryGroupSpecs.compile(spec, subquery.where()), subquery.whereCompare(), defaultPath);
            }
            if (annotation instanceof Exists) {
                Exists exists = Exists.class.cast(annotation);
                return build(spec, SubqueryMode.EXISTS, exists.targetEntity(), exists.left(), exists.right(), exists.select(),
                    CompiledSubqueryGroupSpecs.compile(spec, exists.where()), SyntheticAnnotations.autoCompare(), defaultPath);
            }
            if (annotation instanceof NotExists) {
                NotExists notExists = NotExists.class.cast(annotation);
                return build(spec, SubqueryMode.NOT_EXISTS, notExists.targetEntity(), notExists.left(), notExists.right(), notExists.select(),
                    CompiledSubqueryGroupSpecs.compile(spec, notExists.where()), SyntheticAnnotations.autoCompare(), defaultPath);
            }
            if (annotation instanceof InTable) {
                InTable inTable = InTable.class.cast(annotation);
                return build(spec, SubqueryMode.IN, inTable.targetEntity(), inTable.left(), inTable.right(), inTable.right(),
                    CompiledSubqueryGroupSpecs.compile(spec, inTable.where()), inTable.whereCompare(), defaultPath);
            }
            return fromMeta(spec, annotation, defaultPath);
        }

        private static CanonicalSubquerySource build(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final SubqueryMode mode,
            final Class<?> targetEntity, final io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr left,
            final io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr right,
            final io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr select, final PredicateGroupSpec predicateGroupSpec,
            final Compare whereCompare, final String defaultPath) {
            // 所有子查询家族共享同一组 fallback 规则, 避免 Exists/InTable/CanonicalSubquery
            // 各自维护一份近似但不完全一致的默认 path 推导逻辑.
            String sourcePath = CompiledCanonicalCompareFactory.configuredPath(left, defaultPath);
            String rightPath = CompiledCanonicalCompareFactory.configuredPath(right, defaultPath);
            String selectPath = SubqueryMode.IN == mode ? rightPath : CompiledCanonicalCompareFactory.configuredPath(select, rightPath);
            return new CanonicalSubquerySource(ownerSpec, mode, targetEntity, sourcePath, rightPath, selectPath, predicateGroupSpec, whereCompare);
        }

        private static CanonicalSubquerySource fromMeta(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final Annotation annotation,
            final String defaultPath) {
            SubqueryMode mode = MetaAnnotationAttributes.fieldValue(annotation, "mode", SubqueryMode.class);
            Class<?> targetEntity = MetaAnnotationAttributes.fieldValue(annotation, "targetEntity", Class.class);
            Expr left = defaultIfNull(MetaAnnotationAttributes.fieldValue(annotation, "left", Expr.class), ownerSpec.getPredicateCore().getLeft());
            Expr right = defaultIfNull(MetaAnnotationAttributes.fieldValue(annotation, "right", Expr.class), ownerSpec.getPredicateCore().getRight());
            Expr select = MetaAnnotationAttributes.fieldValue(annotation, "select", Expr.class);
            SubqueryGroup where = MetaAnnotationAttributes.fieldValue(annotation, "where", SubqueryGroup.class);
            Compare whereCompare = MetaAnnotationAttributes.fieldValue(annotation, "whereCompare", Compare.class);
            return build(ownerSpec, mode, targetEntity, left, right, select, CompiledSubqueryGroupSpecs.compile(ownerSpec, where), whereCompare, defaultPath);
        }

        CompiledAnnotationSpec<? extends Annotation> getOwnerSpec() {
            return this.ownerSpec;
        }

        SubqueryMode getMode() {
            return this.mode;
        }

        Class<?> getTargetEntity() {
            return this.targetEntity;
        }

        String getSourcePath() {
            return this.sourcePath;
        }

        String getRightPath() {
            return this.rightPath;
        }

        String getSelectPath() {
            return this.selectPath;
        }

        PredicateGroupSpec getPredicateGroupSpec() {
            return this.predicateGroupSpec;
        }

        Compare getWhereCompare() {
            return this.whereCompare;
        }

    }

    /**
     * 为 in-table 语义编译子查询内部默认叶子 compare 规格.
     */
    private static CompiledAnnotationSpec<?> inTableNestedPredicateSpec(final CompiledAnnotationSpec<? extends Annotation> spec, final String selectPath,
        final Compare whereCompare) {
        if (null != whereCompare && CompiledCanonicalCompareFactory.hasConfiguredCompare(whereCompare)) {
            return CompiledCanonicalCompareFactory.compareSpec(spec.getObjClass(), spec.getValueField(), whereCompare, selectPath, selectPath);
        }
        return null;
    }

    /**
     * 在元注解字段缺省为空时回退到调用方提供的默认值.
     */
    private static <T> T defaultIfNull(final T value, final T fallback) {
        return null == value ? fallback : value;
    }

}
