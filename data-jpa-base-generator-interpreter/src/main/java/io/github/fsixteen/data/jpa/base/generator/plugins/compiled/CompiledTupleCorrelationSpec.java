package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

/**
 * 多列 tuple 子查询中的单对关联映射规格。
 *
 * <p>
 * 它只保存外层路径与子查询路径，不承载运行时值或 JPA 对象。
 * 真正的等值关联谓词在执行阶段由 {@code CompiledTupleSubquerySupport} 构建。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTupleCorrelationSpec {

    private final String leftPath;

    private final String rightPath;

    private CompiledTupleCorrelationSpec(final String leftPath, final String rightPath) {
        this.leftPath = null == leftPath ? "" : leftPath;
        this.rightPath = null == rightPath ? "" : rightPath;
    }

    public static CompiledTupleCorrelationSpec of(final String leftPath, final String rightPath) {
        return new CompiledTupleCorrelationSpec(leftPath, rightPath);
    }

    public String getLeftPath() {
        return this.leftPath;
    }

    public String getRightPath() {
        return this.rightPath;
    }

    public void validate(final String owner) {
        if (this.leftPath.isEmpty() || this.rightPath.isEmpty()) {
            throw new IllegalArgumentException(owner + " requires non-empty TuplePair.left/right path");
        }
    }

}
