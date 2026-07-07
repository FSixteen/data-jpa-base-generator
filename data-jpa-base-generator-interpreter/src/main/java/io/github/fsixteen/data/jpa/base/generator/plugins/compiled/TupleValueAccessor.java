package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.RuntimeValueResolver;

/**
 * tuple 行列值访问器.
 *
 * <p>
 * 它只负责按列映射从单行 tuple 数据中取出一个原始值：
 * 对象行走 {@code itemPath}, 数组行走 {@code itemIndex}.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class TupleValueAccessor {

    private TupleValueAccessor() {
    }

    /**
     * 按列映射定义从单行 tuple 数据中读取原始列值.
     *
     * <p>
     * 当列定义使用 `itemIndex` 时按数组下标访问；否则按 `itemPath` 从对象行中读取属性路径.
     * 若行或列定义为空, 或数组行长度不足, 则返回 {@code null}.
     * </p>
     *
     * @param row    已归一化的单行 tuple 数据
     * @param column 列映射定义
     * @return 读取到的原始列值；无法读取时返回 {@code null}
     */
    static Object read(final TupleRowNormalizer.TupleRowValue row, final CompiledTupleColumnSpec column) {
        if (Objects.isNull(row) || Objects.isNull(column)) {
            return null;
        }
        if (column.usesItemIndex()) {
            if (!row.isArrayRow() || row.length() <= column.getItemIndex()) {
                return null;
            }
            return row.valueAt(column.getItemIndex());
        }
        return RuntimeValueResolver.readPath(row.getRow(), column.getItemPath());
    }

}
