package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.List;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessorContext;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Cases;

/**
 * {@link Cases} 的运行期执行器.
 *
 * <p>
 * 该类型负责在 compiled 主链路下执行分支选择逻辑：
 * 先读取字段运行时值, 再按 {@link CompiledCaseBranchSpec} 的命中规则逐个匹配,
 * 最后根据分支动作模式生成 canonical 谓词或调用自定义 processor.
 * </p>
 *
 * <p>
 * 它不再解析原始注解, 只消费编译阶段产出的分支规格.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledCasesSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CompiledCasesSupport.class);

    private CompiledCasesSupport() {
    }

    /**
     * 执行一个 {@link Cases} 注解的分支选择并生成最终谓词.
     */
    public static javax.persistence.criteria.Predicate create(final CompiledAnnotationSpec<Cases> spec, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        try {
            Object rawFieldValue = spec.readFieldValue(args);
            List<CompiledCaseBranchSpec> branches = CompiledSpecializedSpecs.cases(spec);
            for (CompiledCaseBranchSpec branch : branches) {
                if (!matchesBranch(branch, args, rawFieldValue)) {
                    continue;
                }
                Object fieldValue = branchFieldValue(rawFieldValue, branch.getOptions());
                if (IGNORE_BRANCH == fieldValue) {
                    continue;
                }
                javax.persistence.criteria.Predicate predicate = createResolvedBranchPredicate(branch, args, fieldValue, root, query, cb);
                if (null == predicate) {
                    return null;
                }
                return branch.getOptions().isNegate() ? predicate.not() : predicate;
            }
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 按编译阶段确定的匹配模式判断当前分支是否命中.
     */
    private static boolean matchesBranch(final CompiledCaseBranchSpec branch, final Object args, final Object currentFieldValue) {
        // 分支命中规则已在编译阶段固化, 这里只按模式执行.
        if (branch.isCanonicalMatch()) {
            return RuntimePredicateEvaluator.matches(branch.getCanonicalWhenSpec(), args, currentFieldValue);
        }
        if (branch.isGroupMatch()) {
            return RuntimePredicateGroupEvaluator.matches(branch.getWhenGroupSpec(), args, currentFieldValue);
        }
        if (branch.isReferenceMatch()) {
            return branch.getPredicate().test(args);
        }
        if (branch.isAlwaysMatch()) {
            return true;
        }
        throw new IllegalStateException("Unsupported case branch match mode");
    }

    /**
     * 根据分支动作模式生成 canonical 谓词或调用自定义 processor.
     */
    private static javax.persistence.criteria.Predicate createResolvedBranchPredicate(final CompiledCaseBranchSpec branch, final Object args,
        final Object fieldValue, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        // 执行动作同样由编译阶段一次定型, 运行期不再回看 then 配置细节.
        if (branch.usesProcessorAction()) {
            return branch.getProcessor().create(PredicateProcessorContext.of(canonicalAnnotation(branch.getCanonicalWhenSpec()),
                canonicalAnnotation(branch.getCanonicalPredicateSpec()), args, branch.getFieldName(), fieldValue, root, query, cb));
        }
        if (branch.usesGroupAction()) {
            return CompiledCasePredicateGroupResolver.create(branch.getThenGroupSpec(), args, fieldValue, root, query, cb);
        }
        if (branch.usesCanonicalAction()) {
            return CompiledBuiltInPredicateSupport.createCasePredicate(branch.getCanonicalPredicateSpec(), args, fieldValue, root, query, cb);
        }
        throw new IllegalStateException("Unsupported case branch action mode");
    }

    /**
     * 按分支级别生效选项规范化当前字段值.
     */
    private static Object branchFieldValue(final Object fieldValue, final PredicateOptionsSpec options) {
        Object normalized = options.isTrim() && fieldValue instanceof String ? String.class.cast(fieldValue).trim() : fieldValue;
        if (options.isRequired()) {
            return normalized;
        }
        if (options.isIgnoreNull() && null == normalized) {
            return IGNORE_BRANCH;
        }
        if (normalized instanceof String) {
            String value = String.class.cast(normalized);
            if (options.isIgnoreEmpty() && value.isEmpty()) {
                return IGNORE_BRANCH;
            }
            if (options.isIgnoreBlank() && value.trim().isEmpty()) {
                return IGNORE_BRANCH;
            }
        }
        return normalized;
    }

    /**
     * 从 compiled compare 规格中提取其原始 compare 注解实例.
     */
    private static io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare canonicalAnnotation(
        final CompiledAnnotationSpec<io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare> spec) {
        return null == spec ? null : spec.getAnnotation();
    }

    private static final Object IGNORE_BRANCH = new Object();

}
