package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.function.Predicate;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseThen;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhen;

/**
 * Cases 分支运行时解析器.
 *
 * <p>
 * 该类将散落在 {@code CompiledCasesSupport} 里的扩展引用解析与缓存逻辑集中起来, 让
 * support 本身只处理“按规格执行”.
 * canonical `when/then` 已由编译阶段固化, 这里只负责识别显式扩展入口.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledCasesResolver {

    public static final class ResolvedWhen {

        private final CompiledCaseBranchSpec.MatchMode matchMode;

        private final Predicate<Object> predicate;

        private final PredicateGroupSpec groupSpec;

        /**
         * 创建一个解析后的 when 分支规格.
         *
         * @param matchMode when 分支命中模式
         * @param predicate 引用式命中逻辑；非引用模式下可为空
         * @param groupSpec 结构化分组命中规格；非 group 模式下可为空
         */
        private ResolvedWhen(final CompiledCaseBranchSpec.MatchMode matchMode, final Predicate<Object> predicate, final PredicateGroupSpec groupSpec) {
            this.matchMode = matchMode;
            this.predicate = predicate;
            this.groupSpec = groupSpec;
        }

        /**
         * 返回分支 when 的命中模式.
         */
        public CompiledCaseBranchSpec.MatchMode getMatchMode() {
            return this.matchMode;
        }

        /**
         * 返回引用式匹配所需的 predicate；canonical/always 模式下可能为空.
         */
        public Predicate<Object> getPredicate() {
            return this.predicate;
        }

        /**
         * 返回结构化匹配所需的 group spec.
         */
        public PredicateGroupSpec getGroupSpec() {
            return this.groupSpec;
        }

    }

    public static final class ResolvedThen {

        private final CompiledCaseBranchSpec.ActionMode actionMode;

        private final boolean hasCanonicalPredicate;

        private final PredicateGroupSpec groupSpec;

        private final io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor processor;

        /**
         * 创建一个解析后的 then 分支规格.
         *
         * @param actionMode            then 分支动作模式
         * @param hasCanonicalPredicate 是否同时声明了 canonical compare 动作
         * @param groupSpec             结构化 then-group 规格；非 group 模式下可为空
         * @param processor             processor 模式下要执行的处理器；非 processor 模式下可为空
         */
        private ResolvedThen(final CompiledCaseBranchSpec.ActionMode actionMode, final boolean hasCanonicalPredicate, final PredicateGroupSpec groupSpec,
            final io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor processor) {
            this.actionMode = actionMode;
            this.hasCanonicalPredicate = hasCanonicalPredicate;
            this.groupSpec = groupSpec;
            this.processor = processor;
        }

        /**
         * 返回 then 分支动作模式.
         */
        public CompiledCaseBranchSpec.ActionMode getActionMode() {
            return this.actionMode;
        }

        /**
         * 判断 then 是否同时声明了 canonical 谓词.
         */
        public boolean hasCanonicalPredicate() {
            return this.hasCanonicalPredicate;
        }

        /**
         * 返回结构化 then 动作 group spec.
         */
        public PredicateGroupSpec getGroupSpec() {
            return this.groupSpec;
        }

        /**
         * 返回 processor 动作模式下要执行的处理器.
         */
        public io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor getProcessor() {
            return this.processor;
        }

    }

    private CompiledCasesResolver() {
    }

    /**
     * 解析一个 {@link CaseWhen} 的命中方式.
     *
     * <p>
     * 解析优先级依次为 canonical compare、结构化 group、引用式 predicate, 若都未显式配置则视为 `always`.
     * 这样可以保证分支配置的解释顺序稳定且与编译期预期一致.
     * </p>
     *
     * @param ownerSpec 当前拥有该 case 配置的注解规格
     * @param when      单个 when 分支定义
     * @return 解析完成的 when 分支规格
     * @throws ReflectiveOperationException 当引用式 predicate 无法通过反射实例化时抛出
     */
    public static ResolvedWhen resolveWhen(final CompiledAnnotationSpec<?> ownerSpec, final CaseWhen when) throws ReflectiveOperationException {
        if (hasCanonicalWhen(when)) {
            return new ResolvedWhen(CompiledCaseBranchSpec.MatchMode.CANONICAL, null, null);
        }
        PredicateGroupSpec whenGroupSpec = CompiledCaseWhenGroupSpecs.compile(ownerSpec, when.group());
        if (!whenGroupSpec.isEmpty()) {
            return new ResolvedWhen(CompiledCaseBranchSpec.MatchMode.GROUP, null, whenGroupSpec);
        }
        if (hasPredicateReference(when)) {
            return new ResolvedWhen(CompiledCaseBranchSpec.MatchMode.REFERENCE, CompiledReferenceResolvers.predicateByClass(when.predicate().predicateClass()),
                null);
        }
        return new ResolvedWhen(CompiledCaseBranchSpec.MatchMode.ALWAYS, null, null);
    }

    /**
     * 解析一个 {@link CaseThen} 的执行方式.
     *
     * <p>
     * 解析优先级依次为显式 processor、结构化 group, 若都不存在则回退为 canonical compare.
     * 同时会额外记录 then 分支是否还声明了 canonical compare, 供后续执行阶段组合动作.
     * </p>
     *
     * @param ownerSpec 当前拥有该 case 配置的注解规格
     * @param then      单个 then 分支定义
     * @return 解析完成的 then 分支规格
     * @throws ReflectiveOperationException 当 processor 无法通过反射实例化时抛出
     */
    public static ResolvedThen resolveThen(final CompiledAnnotationSpec<?> ownerSpec, final CaseThen then) throws ReflectiveOperationException {
        boolean hasCanonicalPredicate = hasCanonicalPredicate(then);
        if (hasExplicitProcessor(then)) {
            return new ResolvedThen(CompiledCaseBranchSpec.ActionMode.PROCESSOR, hasCanonicalPredicate, null,
                CompiledReferenceResolvers.processorByClass(then.processor().processorClass()));
        }
        PredicateGroupSpec thenGroupSpec = CompiledCaseThenGroupSpecs.compile(ownerSpec, then.group());
        if (!thenGroupSpec.isEmpty()) {
            return new ResolvedThen(CompiledCaseBranchSpec.ActionMode.GROUP, hasCanonicalPredicate, thenGroupSpec, null);
        }
        return new ResolvedThen(CompiledCaseBranchSpec.ActionMode.CANONICAL, hasCanonicalPredicate, null, null);
    }

    /**
     * 判断 then 是否显式声明了 processor.
     *
     * @param then then 分支定义
     * @return 显式配置了非 {@link Void} 的 processor 类型时返回 {@code true}
     */
    private static boolean hasExplicitProcessor(final CaseThen then) {
        return null != then.processor().processorClass() && !Void.class.equals(then.processor().processorClass());
    }

    /**
     * 判断 then 是否配置了 canonical compare 动作.
     *
     * @param then then 分支定义
     * @return 只要 compare 配置超出默认零配置形态即返回 {@code true}
     */
    private static boolean hasCanonicalPredicate(final CaseThen then) {
        return CompiledCanonicalCompareFactory.hasConfiguredCompare(then.op(), null, then.right(), then.extra());
    }

    /**
     * 判断 when 是否配置了 canonical compare 命中条件.
     *
     * @param when when 分支定义
     * @return 只要 compare 配置超出默认零配置形态即返回 {@code true}
     */
    private static boolean hasCanonicalWhen(final CaseWhen when) {
        return CompiledCanonicalCompareFactory.hasConfiguredCompare(when.op(), when.left(), when.right(), when.extra());
    }

    /**
     * 判断 when 是否配置了引用式 predicate 命中条件.
     *
     * @param when when 分支定义
     * @return 显式配置了非 {@link Void} 的 predicate 类型时返回 {@code true}
     */
    private static boolean hasPredicateReference(final CaseWhen when) {
        return null != when.predicate().predicateClass() && !Void.class.equals(when.predicate().predicateClass());
    }

}
