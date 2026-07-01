package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.function.Predicate;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;

/**
 * 单个 {@code Cases} 分支的编译结果。
 *
 * <p>
 * 该对象把一个分支在运行期需要的全部信息收口为不可变规格：
 * 包括分支作用字段、有效公共选项、命中模式、命中 predicate、canonical when 规格、
 * 执行动作模式、canonical then 规格以及可选 processor。
 * </p>
 *
 * <p>
 * {@link CompiledCasesSupport} 在运行期只消费它，而不再回头读取原始 {@code @Case} 注解。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledCaseBranchSpec {

    /**
     * 分支命中方式。
     */
    public enum MatchMode {
        ALWAYS, CANONICAL, GROUP, REFERENCE
    }

    /**
     * 分支命中后的执行方式。
     */
    public enum ActionMode {
        CANONICAL, GROUP, PROCESSOR
    }

    private final String fieldName;

    private final PredicateOptionsSpec options;

    private final MatchMode matchMode;

    private final Predicate<Object> predicate;

    private final CompiledAnnotationSpec<Compare> canonicalWhenSpec;

    private final PredicateGroupSpec whenGroupSpec;

    private final ActionMode actionMode;

    private final CompiledAnnotationSpec<Compare> canonicalPredicateSpec;

    private final PredicateGroupSpec thenGroupSpec;

    private final PredicateProcessor processor;

    private CompiledCaseBranchSpec(final String fieldName, final PredicateOptionsSpec options, final MatchMode matchMode, final Predicate<Object> predicate,
        final CompiledAnnotationSpec<Compare> canonicalWhenSpec, final PredicateGroupSpec whenGroupSpec, final ActionMode actionMode,
        final CompiledAnnotationSpec<Compare> canonicalPredicateSpec, final PredicateGroupSpec thenGroupSpec, final PredicateProcessor processor) {
        this.fieldName = fieldName;
        this.options = options;
        this.matchMode = matchMode;
        this.predicate = predicate;
        this.canonicalWhenSpec = canonicalWhenSpec;
        this.whenGroupSpec = whenGroupSpec;
        this.actionMode = actionMode;
        this.canonicalPredicateSpec = canonicalPredicateSpec;
        this.thenGroupSpec = thenGroupSpec;
        this.processor = processor;
    }

    /**
     * 创建一条已完全编译的 case 分支规格。
     */
    public static CompiledCaseBranchSpec of(final String fieldName, final PredicateOptionsSpec options, final MatchMode matchMode,
        final Predicate<Object> predicate, final CompiledAnnotationSpec<Compare> canonicalWhenSpec, final PredicateGroupSpec whenGroupSpec,
        final ActionMode actionMode, final CompiledAnnotationSpec<Compare> canonicalPredicateSpec, final PredicateGroupSpec thenGroupSpec,
        final PredicateProcessor processor) {
        return new CompiledCaseBranchSpec(fieldName, options, matchMode, predicate, canonicalWhenSpec, whenGroupSpec, actionMode, canonicalPredicateSpec,
            thenGroupSpec, processor);
    }

    /**
     * 返回该分支作用的字段名。
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * 返回该分支生效后的公共选项。
     */
    public PredicateOptionsSpec getOptions() {
        return this.options;
    }

    /**
     * 返回分支命中模式。
     */
    public MatchMode getMatchMode() {
        return this.matchMode;
    }

    /**
     * 返回引用式命中 predicate。
     */
    public Predicate<Object> getPredicate() {
        return this.predicate;
    }

    /**
     * 返回 canonical when 规格。
     */
    public CompiledAnnotationSpec<Compare> getCanonicalWhenSpec() {
        return this.canonicalWhenSpec;
    }

    /**
     * 返回结构化 when 分组规格。
     */
    public PredicateGroupSpec getWhenGroupSpec() {
        return this.whenGroupSpec;
    }

    /**
     * 返回 then 动作模式。
     */
    public ActionMode getActionMode() {
        return this.actionMode;
    }

    /**
     * 返回 canonical then 谓词规格。
     */
    public CompiledAnnotationSpec<Compare> getCanonicalPredicateSpec() {
        return this.canonicalPredicateSpec;
    }

    /**
     * 返回结构化 then 分组规格。
     */
    public PredicateGroupSpec getThenGroupSpec() {
        return this.thenGroupSpec;
    }

    /**
     * 返回 processor 动作模式下的处理器。
     */
    public PredicateProcessor getProcessor() {
        return this.processor;
    }

    /**
     * 判断该分支是否通过 canonical compare 命中。
     */
    public boolean isCanonicalMatch() {
        return MatchMode.CANONICAL == this.matchMode;
    }

    /**
     * 判断该分支是否通过引用式 predicate 命中。
     */
    public boolean isReferenceMatch() {
        return MatchMode.REFERENCE == this.matchMode;
    }

    /**
     * 判断该分支是否通过结构化 group 命中。
     */
    public boolean isGroupMatch() {
        return MatchMode.GROUP == this.matchMode;
    }

    /**
     * 判断该分支是否总是命中。
     */
    public boolean isAlwaysMatch() {
        return MatchMode.ALWAYS == this.matchMode;
    }

    /**
     * 判断该分支命中后是否走 processor 动作。
     */
    public boolean usesProcessorAction() {
        return ActionMode.PROCESSOR == this.actionMode;
    }

    /**
     * 判断该分支命中后是否走结构化 group 动作。
     */
    public boolean usesGroupAction() {
        return ActionMode.GROUP == this.actionMode;
    }

    /**
     * 判断该分支命中后是否走 canonical 谓词动作。
     */
    public boolean usesCanonicalAction() {
        return ActionMode.CANONICAL == this.actionMode;
    }

}
