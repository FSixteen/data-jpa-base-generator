package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Objects;

/**
 * 单列 tuple 映射的 compiled 规格。
 *
 * <p>
 * 当前类型承接 {@code TupleColumn} 在解释器中的稳定快照，避免执行层再次直接依赖原始注解实例。
 * 它只描述主查询路径、tuple 行取值方式与目标类型信息，不参与运行时值读取。
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

    private CompiledTupleColumnSpec(final String leftPath, final String itemPath, final int itemIndex, final Class<?> targetType, final String targetFormat) {
        this.leftPath = null == leftPath ? "" : leftPath;
        this.itemPath = null == itemPath ? "" : itemPath;
        this.itemIndex = itemIndex;
        this.targetType = null == targetType ? Object.class : targetType;
        this.targetFormat = null == targetFormat ? "" : targetFormat;
    }

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

    public boolean usesItemPath() {
        return !this.itemPath.isEmpty();
    }

    public boolean usesItemIndex() {
        return 0 <= this.itemIndex;
    }

    public boolean hasExplicitTargetType() {
        return Object.class != this.targetType;
    }

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
