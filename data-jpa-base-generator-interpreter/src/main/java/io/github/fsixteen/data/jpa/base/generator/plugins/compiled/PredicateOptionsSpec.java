package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Arrays;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ArrayMergeMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.OptionSwitch;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;

/**
 * 生效后的谓词公共选项.
 *
 * <p>
 * 该对象表示 {@code PredicateOptions} 在 compiled 主链路中经过默认值补齐、父级继承、
 * merge mode 处理和布尔开关归并之后的最终结果.
 * 运行期所有 ignore/trim/required/not/group/scope 语义都直接以它为准.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class PredicateOptionsSpec {

    private final String[] scope;

    private final GroupInfo[] groups;

    private final boolean required;

    private final boolean negate;

    private final boolean ignoreNull;

    private final boolean ignoreEmpty;

    private final boolean ignoreBlank;

    private final boolean trim;

    private PredicateOptionsSpec(final String[] scope, final GroupInfo[] groups, final boolean required, final boolean negate, final boolean ignoreNull,
        final boolean ignoreEmpty, final boolean ignoreBlank, final boolean trim) {
        this.scope = null == scope ? new String[0] : Arrays.copyOf(scope, scope.length);
        this.groups = null == groups ? new GroupInfo[0] : Arrays.copyOf(groups, groups.length);
        this.required = required;
        this.negate = negate;
        this.ignoreNull = ignoreNull;
        this.ignoreEmpty = ignoreEmpty;
        this.ignoreBlank = ignoreBlank;
        this.trim = trim;
    }

    /**
     * 直接返回指定规格已经生效的公共选项.
     */
    public static PredicateOptionsSpec of(final CompiledAnnotationSpec<?> spec) {
        return spec.getEffectiveOptions();
    }

    /**
     * 解析顶层注解的最终公共选项.
     *
     * <p>
     * 顶层 canonical 注解默认自动补齐 default scope/group 和标准忽略策略,
     * 避免每个注解再次重复声明同一套默认值.
     * </p>
     *
     * @param options 注解原始 options
     * @return PredicateOptionsSpec
     */
    public static PredicateOptionsSpec root(final PredicateOptions options) {
        return merge(PredicateOptionDefaults.defaults(), options);
    }

    /**
     * 解析嵌套/分支场景下基于父级的公共选项.
     *
     * @param base    父级公共选项
     * @param options 当前层 options
     * @return PredicateOptionsSpec
     */
    public static PredicateOptionsSpec inherit(final PredicateOptionsSpec base, final PredicateOptions options) {
        return merge(base, options);
    }

    /**
     * 基于父级选项与当前层原始注解合并生成最终选项.
     */
    private static PredicateOptionsSpec merge(final PredicateOptionsSpec base, final PredicateOptions options) {
        if (Objects.isNull(base)) {
            return merge(null, null, false, false, true, true, true, true, options);
        }
        return merge(base.getScope(), base.getGroups(), base.isRequired(), base.isNegate(), base.isIgnoreNull(), base.isIgnoreEmpty(), base.isIgnoreBlank(),
            base.isTrim(), options);
    }

    /**
     * 以扁平参数形式执行一次完整的默认值、继承和 merge-mode 归并.
     */
    static PredicateOptionsSpec merge(final String[] scope, final GroupInfo[] groups, final boolean required, final boolean negate, final boolean ignoreNull,
        final boolean ignoreEmpty, final boolean ignoreBlank, final boolean trim, final PredicateOptions options) {
        if (Objects.nonNull(options)) {
            return new PredicateOptionsSpec(resolveScope(scope, options), resolveGroups(groups, options),
                resolveBoolean(required, false, options.required(), options.requiredMode()), resolveBoolean(negate, false, options.not(), options.notMode()),
                resolveBoolean(ignoreNull, true, options.ignoreNull(), options.ignoreNullMode()),
                resolveBoolean(ignoreEmpty, true, options.ignoreEmpty(), options.ignoreEmptyMode()),
                resolveBoolean(ignoreBlank, true, options.ignoreBlank(), options.ignoreBlankMode()),
                resolveBoolean(trim, true, options.trim(), options.trimMode()));
        }
        return new PredicateOptionsSpec(scope, groups, required, negate, ignoreNull, ignoreEmpty, ignoreBlank, trim);
    }

    /**
     * 解析最终 scope 数组.
     */
    private static String[] resolveScope(final String[] inherited, final PredicateOptions options) {
        return resolveArray(inherited, options.scope(), options.scopeMode(), String[]::new);
    }

    /**
     * 解析最终 group 数组.
     */
    private static GroupInfo[] resolveGroups(final GroupInfo[] inherited, final PredicateOptions options) {
        return resolveArray(inherited, options.groups(), options.groupsMode(), GroupInfo[]::new);
    }

    /**
     * 按 {@link OptionSwitch} 语义解析布尔选项.
     */
    private static boolean resolveBoolean(final boolean inherited, final boolean defaultValue, final boolean currentValue, final OptionSwitch mode) {
        if (OptionSwitch.ENABLED == mode) {
            return true;
        }
        if (OptionSwitch.DISABLED == mode) {
            return false;
        }
        return currentValue != defaultValue ? currentValue : inherited;
    }

    /**
     * 按 {@link ArrayMergeMode} 语义解析数组类选项.
     */
    private static <T> T[] resolveArray(final T[] inherited, final T[] current, final ArrayMergeMode mode, final java.util.function.IntFunction<T[]> factory) {
        if (ArrayMergeMode.CLEAR == mode) {
            return factory.apply(0);
        }
        if (ArrayMergeMode.REPLACE == mode) {
            return null == current ? factory.apply(0) : Arrays.copyOf(current, current.length);
        }
        if (null != current && 0 < current.length) {
            return Arrays.copyOf(current, current.length);
        }
        return null == inherited ? factory.apply(0) : Arrays.copyOf(inherited, inherited.length);
    }

    /**
     * 返回已生效的 scope 数组.
     */
    public String[] getScope() {
        return Arrays.copyOf(this.scope, this.scope.length);
    }

    /**
     * 返回已生效的 group 数组.
     */
    public GroupInfo[] getGroups() {
        return Arrays.copyOf(this.groups, this.groups.length);
    }

    /**
     * 判断是否启用 required 语义.
     */
    public boolean isRequired() {
        return this.required;
    }

    /**
     * 判断是否启用外层 negate 语义.
     */
    public boolean isNegate() {
        return this.negate;
    }

    /**
     * 判断是否忽略 null 值.
     */
    public boolean isIgnoreNull() {
        return this.ignoreNull;
    }

    /**
     * 判断是否忽略空字符串.
     */
    public boolean isIgnoreEmpty() {
        return this.ignoreEmpty;
    }

    /**
     * 判断是否忽略仅包含空白字符的字符串.
     */
    public boolean isIgnoreBlank() {
        return this.ignoreBlank;
    }

    /**
     * 判断读取到字符串后是否需要 trim.
     */
    public boolean isTrim() {
        return this.trim;
    }

}
