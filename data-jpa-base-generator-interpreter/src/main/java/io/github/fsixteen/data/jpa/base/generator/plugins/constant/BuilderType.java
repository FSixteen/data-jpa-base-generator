package io.github.fsixteen.data.jpa.base.generator.plugins.constant;

/**
 * 对外稳定的构建类型入口.
 *
 * <p>
 * 该枚举保留给既有调用链使用, compiled 主链路内部会进一步映射为
 * selection / existence 语义目标.
 * </p>
 *
 * <p>
 * 外部项目仍可继续通过
 * {@code AnnotationCollection -> ComputerCollection -> build(BuilderType)}
 * 这条链路选择查询构建模式, 而无需感知内部 compiled provider 的重构细节.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public enum BuilderType {
    /**
     * 查询.
     */
    SELECTED,
    /**
     * 判断已存在.
     */
    EXISTS
}
