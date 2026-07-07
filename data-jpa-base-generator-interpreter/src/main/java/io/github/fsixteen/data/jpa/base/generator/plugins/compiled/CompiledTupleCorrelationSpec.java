package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

/**
 * 多列 tuple 子查询中的单对关联映射规格.
 *
 * <p>
 * 它只保存外层路径与子查询路径, 不承载运行时值或 JPA 对象.
 * 真正的等值关联谓词在执行阶段由 {@code CompiledTupleSubquerySupport} 构建.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTupleCorrelationSpec {

    private final String leftPath;

    private final String rightPath;

    /**
     * 创建一条 tuple 关联路径对.
     *
     * @param leftPath  外层查询路径
     * @param rightPath 子查询路径
     */
    private CompiledTupleCorrelationSpec(final String leftPath, final String rightPath) {
        this.leftPath = null == leftPath ? "" : leftPath;
        this.rightPath = null == rightPath ? "" : rightPath;
    }

    /**
     * 基于左右路径创建一条 tuple 关联规格.
     *
     * @param leftPath  外层查询路径
     * @param rightPath 子查询路径
     * @return 关联规格
     */
    public static CompiledTupleCorrelationSpec of(final String leftPath, final String rightPath) {
        return new CompiledTupleCorrelationSpec(leftPath, rightPath);
    }

    /**
     * 返回外层查询路径.
     *
     * @return 外层查询路径
     */
    public String getLeftPath() {
        return this.leftPath;
    }

    /**
     * 返回子查询路径.
     *
     * @return 子查询路径
     */
    public String getRightPath() {
        return this.rightPath;
    }

    /**
     * 校验当前关联规格是否具备完整的左右路径.
     *
     * @param owner 规格拥有者描述, 用于补充异常信息
     * @throws IllegalArgumentException 当左右任一路径为空时抛出
     */
    public void validate(final String owner) {
        if (this.leftPath.isEmpty() || this.rightPath.isEmpty()) {
            throw new IllegalArgumentException(owner + " requires non-empty TuplePair.left/right path");
        }
    }

}
