package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType;

/**
 * 编译后的集合值处理策略。
 *
 * <p>
 * `Membership`、`SplitIn`、`FilterIn` 这一类注解会共享：
 * 分隔符、正则过滤、目标类型转换、格式化信息以及条件过滤扩展。
 * 这些配置原本散落在 {@code CompiledAnnotationSpec} 上，这里统一收口，避免继续扩张
 * annotation spec 本体。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class CompiledCollectionPolicySpec {

    private final String decollator;

    private final String regexp;

    private final TargetType targetType;

    private final String targetFormat;

    private final boolean split;

    private final CompiledPredicateFilterSpec predicateFilter;

    private CompiledCollectionPolicySpec(final String decollator, final String regexp, final TargetType targetType, final String targetFormat,
        final boolean split, final CompiledPredicateFilterSpec predicateFilter) {
        this.decollator = decollator;
        this.regexp = regexp;
        this.targetType = targetType;
        this.targetFormat = targetFormat;
        this.split = split;
        this.predicateFilter = predicateFilter;
    }

    /**
     * 创建一份编译后的集合处理策略。
     */
    static CompiledCollectionPolicySpec of(final String decollator, final String regexp, final TargetType targetType, final String targetFormat,
        final boolean split, final CompiledPredicateFilterSpec predicateFilter) {
        return new CompiledCollectionPolicySpec(decollator, regexp, targetType, targetFormat, split, predicateFilter);
    }

    /**
     * 返回集合拆分分隔符。
     */
    String getDecollator() {
        return this.decollator;
    }

    /**
     * 返回集合元素正则过滤规则。
     */
    String getRegexp() {
        return this.regexp;
    }

    /**
     * 返回集合元素目标类型。
     */
    TargetType getTargetType() {
        return this.targetType;
    }

    /**
     * 返回目标类型转换格式化模板。
     */
    String getTargetFormat() {
        return this.targetFormat;
    }

    /**
     * 判断是否启用字符串拆分集合语义。
     */
    boolean isSplit() {
        return this.split;
    }

    /**
     * 返回集合元素自定义过滤扩展。
     */
    CompiledPredicateFilterSpec getPredicateFilter() {
        return this.predicateFilter;
    }

}
