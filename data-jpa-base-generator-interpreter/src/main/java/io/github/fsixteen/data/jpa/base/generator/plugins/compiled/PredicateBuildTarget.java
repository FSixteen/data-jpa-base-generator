package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;

/**
 * compiled 主链路内部使用的构建目标.
 *
 * <p>
 * 对外仍保留 {@link BuilderType} 作为稳定入口参数,
 * 但内部统一收敛为 selection / existence 两种语义目标,
 * 避免在实现层继续散落历史命名.
 * </p>
 *
 * <p>
 * 与 {@link CompiledPredicateRoleSpec} 配合后, 执行阶段可以明确判断某个注解
 * 当前是否应该参与构建, 而不必反复读取原始注解角色字段.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum PredicateBuildTarget {

    /**
     * 普通查询条件构建.
     */
    SELECTION,
    /**
     * exists / not exists 场景构建.
     */
    EXISTENCE;

    /**
     * 将公开 {@link BuilderType} 映射为 compiled 内部目标类型.
     */
    public static PredicateBuildTarget from(final BuilderType type) {
        if (BuilderType.EXISTS == type) {
            return EXISTENCE;
        }
        return SELECTION;
    }

    /**
     * 将内部目标类型转换回公开 {@link BuilderType}.
     */
    public BuilderType toBuilderType() {
        if (EXISTENCE == this) {
            return BuilderType.EXISTS;
        }
        return BuilderType.SELECTED;
    }

    /**
     * 判断当前目标是否为普通查询构建.
     */
    public boolean isSelection() {
        return SELECTION == this;
    }

    /**
     * 判断当前目标是否为 existence 构建.
     */
    public boolean isExistence() {
        return EXISTENCE == this;
    }

}
