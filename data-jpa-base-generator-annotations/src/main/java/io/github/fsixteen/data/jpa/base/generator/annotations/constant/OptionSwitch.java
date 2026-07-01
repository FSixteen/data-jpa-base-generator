package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * 三态布尔开关。
 *
 * <p>
 * 它用于解决 Java 注解布尔值只能二选一的问题，使 compiled 规则可以区分“未指定，沿用外层默认值”
 * 与“显式打开 / 显式关闭”。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum OptionSwitch {

    /**
     * 沿用外层或当前默认配置。
     */
    DEFAULT,

    /**
     * 显式启用。
     */
    ENABLED,

    /**
     * 显式禁用。
     */
    DISABLED
}
