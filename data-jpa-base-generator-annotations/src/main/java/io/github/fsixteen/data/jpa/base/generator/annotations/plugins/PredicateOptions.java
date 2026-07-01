package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ArrayMergeMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.OptionSwitch;

/**
 * canonical 谓词公共选项。
 *
 * <p>
 * 这是 compiled 主链路中关于 {@code scope/groups/required/not/ignore*} 的一级入口。新注解或组合注解
 * 应优先通过该注解声明公共行为，而不是继续复制平铺布尔字段。
 * </p>
 *
 * <p>
 * compiled 主链路直接以该注解作为
 * {@code scope/groups/required/not/ignoreXxx/trim} 的统一输入模型。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface PredicateOptions {

    /**
     * 范围查询分组。
     *
     * @return String[]
     */
    String[] scope() default {};

    /**
     * {@link #scope()} 的合并策略。
     *
     * @return ArrayMergeMode
     */
    ArrayMergeMode scopeMode() default ArrayMergeMode.INHERIT;

    /**
     * 条件分组。
     *
     * @return GroupInfo[]
     */
    GroupInfo[] groups() default {};

    /**
     * {@link #groups()} 的合并策略。
     *
     * @return ArrayMergeMode
     */
    ArrayMergeMode groupsMode() default ArrayMergeMode.INHERIT;

    /**
     * 是否必须参与。
     *
     * @return boolean
     */
    boolean required() default false;

    /**
     * {@link #required()} 的三态覆盖入口。
     *
     * @return OptionSwitch
     */
    OptionSwitch requiredMode() default OptionSwitch.DEFAULT;

    /**
     * 是否逻辑反向。
     *
     * @return boolean
     */
    boolean not() default false;

    /**
     * {@link #not()} 的三态覆盖入口。
     *
     * @return OptionSwitch
     */
    OptionSwitch notMode() default OptionSwitch.DEFAULT;

    /**
     * 是否忽略 null。
     *
     * @return boolean
     */
    boolean ignoreNull() default true;

    /**
     * {@link #ignoreNull()} 的三态覆盖入口。
     *
     * @return OptionSwitch
     */
    OptionSwitch ignoreNullMode() default OptionSwitch.DEFAULT;

    /**
     * 是否忽略空字符串。
     *
     * @return boolean
     */
    boolean ignoreEmpty() default true;

    /**
     * {@link #ignoreEmpty()} 的三态覆盖入口。
     *
     * @return OptionSwitch
     */
    OptionSwitch ignoreEmptyMode() default OptionSwitch.DEFAULT;

    /**
     * 是否忽略空白字符串。
     *
     * @return boolean
     */
    boolean ignoreBlank() default true;

    /**
     * {@link #ignoreBlank()} 的三态覆盖入口。
     *
     * @return OptionSwitch
     */
    OptionSwitch ignoreBlankMode() default OptionSwitch.DEFAULT;

    /**
     * 是否 trim。
     *
     * @return boolean
     */
    boolean trim() default true;

    /**
     * {@link #trim()} 的三态覆盖入口。
     *
     * @return OptionSwitch
     */
    OptionSwitch trimMode() default OptionSwitch.DEFAULT;

}
