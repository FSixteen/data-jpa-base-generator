package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * 数组型注解属性的合并策略。
 *
 * <p>
 * 主要用于 {@code PredicateOptions.scope/groups} 这类需要继承外层默认值、局部替换或显式清空的场景。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum ArrayMergeMode {

    /**
     * 继承上层配置。
     */
    INHERIT,

    /**
     * 使用当前注解提供的数组值替换上层值。
     */
    REPLACE,

    /**
     * 显式清空上层值。
     */
    CLEAR
}
