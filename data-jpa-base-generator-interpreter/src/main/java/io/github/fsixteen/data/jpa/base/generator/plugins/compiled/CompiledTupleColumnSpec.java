package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Objects;

/**
 * 单列 tuple 映射的 compiled 规格.
 *
 * <p>
 * 当前类型承接 {@code TupleColumn} 在解释器中的稳定快照, 避免执行层再次直接依赖原始注解实例.
 * 它只描述主查询路径、tuple 行取值方式与目标类型信息, 不参与运行时值读取.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTupleColumnSpec {

    private final String leftPath;

    private final String itemPath;

    private final int itemIndex;

    private final Class<?> targetType;

    private final String targetFormat;

    /**
     * 创建一条 tuple 列映射规格.
     *
     * @param leftPath     主查询比较路径
     * @param itemPath     tuple 对象行取值路径
     * @param itemIndex    tuple 数组行取值下标
     * @param targetType   目标类型
     * @param targetFormat 目标格式
     */
    private CompiledTupleColumnSpec(final String leftPath, final String itemPath, final int itemIndex, final Class<?> targetType, final String targetFormat) {
        this.leftPath = null == leftPath ? "" : leftPath;
        this.itemPath = null == itemPath ? "" : itemPath;
        this.itemIndex = itemIndex;
        this.targetType = null == targetType ? Object.class : targetType;
        this.targetFormat = null == targetFormat ? "" : targetFormat;
    }

    /**
     * 基于主查询路径和 tuple 行映射信息创建列规格.
     *
     * @param leftPath     主查询比较路径
     * @param itemPath     tuple 对象行取值路径
     * @param itemIndex    tuple 数组行取值下标
     * @param targetType   目标类型
     * @param targetFormat 目标格式
     * @return 编译后的 tuple 列规格
     */
    public static CompiledTupleColumnSpec of(final String leftPath, final String itemPath, final int itemIndex, final Class<?> targetType,
        final String targetFormat) {
        return new CompiledTupleColumnSpec(leftPath, itemPath, itemIndex, targetType, targetFormat);
    }

    public String getLeftPath() {
        return this.leftPath;
    }

    public String getItemPath() {
        return this.itemPath;
    }

    public int getItemIndex() {
        return this.itemIndex;
    }

    public Class<?> getTargetType() {
        return this.targetType;
    }

    public String getTargetFormat() {
        return this.targetFormat;
    }

    /**
     * 判断当前列是否通过对象路径读取 tuple 值.
     *
     * @return 配置了非空 `itemPath` 时返回 {@code true}
     */
    public boolean usesItemPath() {
        return !this.itemPath.isEmpty();
    }

    /**
     * 判断当前列是否通过数组下标读取 tuple 值.
     *
     * @return `itemIndex >= 0` 时返回 {@code true}
     */
    public boolean usesItemIndex() {
        return 0 <= this.itemIndex;
    }

    /**
     * 判断当前列是否显式声明了目标类型.
     *
     * @return 目标类型不是 {@link Object} 时返回 {@code true}
     */
    public boolean hasExplicitTargetType() {
        return Object.class != this.targetType;
    }

    /**
     * 校验列映射是否满足“必须有 leftPath, 且 itemPath/itemIndex 二选一”的约束.
     *
     * @param owner 规格拥有者描述, 用于补充异常信息
     * @throws IllegalArgumentException 当列映射配置不合法时抛出
     */
    public void validate(final String owner) {
        if (this.leftPath.isEmpty()) {
            throw new IllegalArgumentException(owner + " requires non-empty TupleColumn.leftPath()");
        }
        if (this.usesItemPath() == this.usesItemIndex()) {
            throw new IllegalArgumentException(owner + " requires exactly one of TupleColumn.itemPath() or TupleColumn.itemIndex()");
        }
    }

    @Override
    public String toString() {
        return "CompiledTupleColumnSpec[leftPath=" + this.leftPath + ", itemPath=" + this.itemPath + ", itemIndex=" + this.itemIndex + ", targetType="
            + Objects.toString(this.targetType, Object.class.getName()) + ", targetFormat=" + this.targetFormat + "]";
    }

}
