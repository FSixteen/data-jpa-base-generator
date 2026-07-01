package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.ComputerCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;

/**
 * compiled 主链路的统一门面。
 *
 * <p>
 * 该类型为工程内部提供了直接的 selection/existence 语义入口，
 * 同时继续接受公开 API 边界上的 {@link BuilderType}，避免外部调用链与内部执行模型耦合。
 * </p>
 *
 * <p>
 * 如果调用方不需要显式持有
 * {@link io.github.fsixteen.data.jpa.base.generator.plugins.collections.ComputerCollection}，
 * 可以直接使用本类一次性拿到 {@link Predicate} 或 {@link Predicate} 数组。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateFacade {

    private CompiledPredicateFacade() {
    }

    /**
     * 按公开边界上的 {@link BuilderType} 构建运行期结果集合。
     *
     * @return ComputerCollection
     */
    public static ComputerCollection build(final AnnotationCollection annotationCollection, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb, final BuilderType type) {
        return build(annotationCollection, args, root, query, cb, PredicateBuildTarget.from(type));
    }

    /**
     * 按内部 {@link PredicateBuildTarget} 直接构建运行期结果集合。
     *
     * @return ComputerCollection
     */
    public static ComputerCollection build(final AnnotationCollection annotationCollection, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb, final PredicateBuildTarget target) {
        return ComputerCollection.Builder.of().withAnnotationCollection(annotationCollection).withArgs(args).withSpecification(root, query, cb).build(target);
    }

    /**
     * 以 selection 语义构建运行期结果集合。
     */
    public static ComputerCollection selection(final AnnotationCollection annotationCollection, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return build(annotationCollection, args, root, query, cb, PredicateBuildTarget.SELECTION);
    }

    /**
     * 以 existence 语义构建运行期结果集合。
     */
    public static ComputerCollection existence(final AnnotationCollection annotationCollection, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return build(annotationCollection, args, root, query, cb, PredicateBuildTarget.EXISTENCE);
    }

    /**
     * 对外最常见的用法仍然是“直接要一个 Predicate”，这里仅作为薄封装，真正的构建和分组逻辑
     * 仍然留在 compiled 结果对象内部，避免再次分叉一条新实现。
     */
    public static Predicate predicate(final AnnotationCollection annotationCollection, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb, final BuilderType type) {
        return predicate(annotationCollection, args, root, query, cb, PredicateBuildTarget.from(type));
    }

    /**
     * 按内部构建目标直接生成最终单个谓词。
     */
    public static Predicate predicate(final AnnotationCollection annotationCollection, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb, final PredicateBuildTarget target) {
        return build(annotationCollection, args, root, query, cb, target).getPredicate(cb);
    }

    /**
     * 生成 selection 语义下的最终单个谓词。
     */
    public static Predicate selectionPredicate(final AnnotationCollection annotationCollection, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return predicate(annotationCollection, args, root, query, cb, PredicateBuildTarget.SELECTION);
    }

    /**
     * 生成 existence 语义下的最终单个谓词。
     */
    public static Predicate existencePredicate(final AnnotationCollection annotationCollection, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return predicate(annotationCollection, args, root, query, cb, PredicateBuildTarget.EXISTENCE);
    }

    /**
     * 按公开 {@link BuilderType} 生成最终谓词数组。
     */
    public static Predicate[] predicateArray(final AnnotationCollection annotationCollection, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb, final BuilderType type) {
        return predicateArray(annotationCollection, args, root, query, cb, PredicateBuildTarget.from(type));
    }

    /**
     * 按内部构建目标生成最终谓词数组。
     */
    public static Predicate[] predicateArray(final AnnotationCollection annotationCollection, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb, final PredicateBuildTarget target) {
        return build(annotationCollection, args, root, query, cb, target).getPredicateArray(cb);
    }

    /**
     * 生成 selection 语义下的最终谓词数组。
     */
    public static Predicate[] selectionPredicateArray(final AnnotationCollection annotationCollection, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return predicateArray(annotationCollection, args, root, query, cb, PredicateBuildTarget.SELECTION);
    }

    /**
     * 生成 existence 语义下的最终谓词数组。
     */
    public static Predicate[] existencePredicateArray(final AnnotationCollection annotationCollection, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return predicateArray(annotationCollection, args, root, query, cb, PredicateBuildTarget.EXISTENCE);
    }

}
