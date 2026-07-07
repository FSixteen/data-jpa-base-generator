package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Cases;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Null;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryPredicate;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleInValues;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleNotExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleNotInValues;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProvider;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry;

/**
 * 内建 compiled provider 注册入口.
 *
 * <p>
 * 该类型负责把框架内置支持的 canonical 根注解和少量特殊注解注册到
 * {@link CompiledPredicateProviderRegistry}. 它只处理“框架自带能力”的首轮注册；
 * 组合注解、快捷注解和 SPI 扩展仍通过 registry 的元注解递归解析或 ServiceLoader 装配完成.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class BuiltInCompiledPredicateProviders {

    private BuiltInCompiledPredicateProviders() {
    }

    /**
     * 注册 compiled 主链路内建支持的 provider.
     */
    public static void registerAll() {
        // 这里优先注册 canonical 家族与少数特殊能力注解.
        // Selectable / Existed 这类角色元注解也不再直接注册 compare provider,
        // 而是和其余快捷注解、组合注解一起统一交给 registry 的递归元注解路由解析,
        // 避免每新增一个语义别名都要在这里再登记一遍.
        register(Compare.class, new GenericCompareProvider());
        register(SubqueryPredicate.class, new SubqueryProvider());
        register(Cases.class, new CasesProvider());
        register(Null.class, new NullProvider());
        register(TupleInValues.class, new TupleValuesProvider());
        register(TupleNotInValues.class, new TupleValuesProvider());
        register(TupleExists.class, new TupleSubqueryProvider());
        register(TupleNotExists.class, new TupleSubqueryProvider());
    }

    /**
     * 在目标注解尚未显式注册时写入一条 provider 映射.
     */
    private static void register(final Class<? extends Annotation> annotationType, final CompiledPredicateProvider provider) {
        // 内建 provider 需要优先于 @Constraint 的懒加载适配器注册.
        if (!CompiledPredicateProviderRegistry.containsRegistered(annotationType)) {
            CompiledPredicateProviderRegistry.register(annotationType, provider);
        }
    }

    private abstract static class AbstractDelegatingProvider implements CompiledPredicateProvider {

        @Override
        public Predicate create(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root, final AbstractQuery<?> query,
            final CriteriaBuilder cb) {
            // provider 层只暴露 Predicate；真正的执行细节统一收口到 compiled support.
            return this.toPredicate(spec, args, root, query, cb);
        }

        /**
         * 将已编译注解规格转换为最终的 JPA {@link Predicate}.
         */
        protected abstract Predicate toPredicate(CompiledAnnotationSpec<? extends Annotation> spec, Object args, Root<?> root, AbstractQuery<?> query,
            CriteriaBuilder cb);

    }

    private static final class GenericCompareProvider extends AbstractDelegatingProvider {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Annotation.class;
        }

        /**
         * 统一分发 compare / in / like / between / null-check 这类通用比较语义.
         */
        @Override
        protected Predicate toPredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb) {
            ComparableType type = CompiledComparableTypeResolver.resolve(spec.getPredicateCore().getOp(), spec.getCollectionPolicy().isSplit());
            if (type.isRange()) {
                return CompiledBuiltInPredicateSupport.createBetween(spec, args, root, query, cb, type);
            }
            if (type.isCollection()) {
                return CompiledBuiltInPredicateSupport.createIn(spec, args, root, query, cb, type);
            }
            if (ComparableType.IS_NULL == type) {
                return CompiledBuiltInPredicateSupport.createIsNull(spec, args, root, query, cb, false);
            }
            if (ComparableType.IS_NOT_NULL == type) {
                return CompiledBuiltInPredicateSupport.createIsNull(spec, args, root, query, cb, true);
            }
            if (type.isLike()) {
                return CompiledBuiltInPredicateSupport.createLike(spec, args, root, query, cb, type);
            }
            return CompiledBuiltInPredicateSupport.createComparable(spec, args, root, query, cb, type);
        }

    }

    private static final class SubqueryProvider extends AbstractDelegatingProvider {

        @Override
        public Class<? extends Annotation> annotationType() {
            return SubqueryPredicate.class;
        }

        /**
         * 将子查询注解规格交给统一子查询执行器展开.
         */
        @Override
        protected Predicate toPredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb) {
            return CompiledSubquerySupport.create(spec, args, root, query, cb);
        }

    }

    private static final class CasesProvider extends AbstractDelegatingProvider {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Cases.class;
        }

        /**
         * 执行 case 分支匹配与 then 动作生成.
         */
        @Override
        protected Predicate toPredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb) {
            return CompiledCasesSupport.create(cast(spec, Cases.class), args, root, query, cb);
        }

    }

    private static final class NullProvider extends AbstractDelegatingProvider {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Null.class;
        }

        /**
         * 执行布尔开关式 null 谓词.
         */
        @Override
        protected Predicate toPredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb) {
            return CompiledBuiltInPredicateSupport.createNull(cast(spec, Null.class), args, root, query, cb);
        }

    }

    private static final class TupleValuesProvider extends AbstractDelegatingProvider {

        @Override
        public Class<? extends Annotation> annotationType() {
            return TupleInValues.class;
        }

        /**
         * 执行 tuple-value 家族谓词.
         */
        @Override
        protected Predicate toPredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb) {
            return CompiledTuplePredicateSupport.create(spec, args, root, query, cb);
        }

    }

    private static final class TupleSubqueryProvider extends AbstractDelegatingProvider {

        @Override
        public Class<? extends Annotation> annotationType() {
            return TupleExists.class;
        }

        /**
         * 执行多列 tuple exists/not-exists 子查询.
         */
        @Override
        protected Predicate toPredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb) {
            return CompiledTupleSubquerySupport.create(spec, args, root, query, cb);
        }

    }

    /**
     * 将泛型规格安全收窄到目标注解类型.
     *
     * @throws IllegalArgumentException 当原始注解类型与目标类型不匹配
     */
    private static <A extends Annotation> CompiledAnnotationSpec<A> cast(final CompiledAnnotationSpec<? extends Annotation> spec,
        final Class<A> annotationType) {
        if (!annotationType.isInstance(spec.getAnnotation())) {
            throw new IllegalArgumentException("Expected @" + annotationType.getSimpleName() + " spec but got @" + spec.getAnnotationType().getSimpleName());
        }
        return castSpec(spec);
    }

    /**
     * 在调用方已完成类型检查后执行无额外成本的泛型转换.
     */
    @SuppressWarnings("unchecked")
    private static <A extends Annotation> CompiledAnnotationSpec<A> castSpec(final CompiledAnnotationSpec<? extends Annotation> spec) {
        return (CompiledAnnotationSpec<A>) spec;
    }

}
