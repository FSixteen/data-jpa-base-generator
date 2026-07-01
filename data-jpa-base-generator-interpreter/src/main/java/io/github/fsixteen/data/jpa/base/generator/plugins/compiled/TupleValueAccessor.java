package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.RuntimeValueResolver;

/**
 * tuple 行列值访问器。
 *
 * <p>
 * 它只负责按列映射从单行 tuple 数据中取出一个原始值：
 * 对象行走 {@code itemPath}，数组行走 {@code itemIndex}。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class TupleValueAccessor {

    private TupleValueAccessor() {
    }

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
