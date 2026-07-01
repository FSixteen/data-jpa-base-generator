package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * tuple 数据源归一化器。
 *
 * <p>
 * 它把集合、对象数组、primitive 数组与二维数组统一收敛为“逐行可访问”的内部模型，
 * 让后续列读取逻辑不再关心原始容器形态。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class TupleRowNormalizer {

    private TupleRowNormalizer() {
    }

    static List<TupleRowValue> normalize(final Object tupleSource) {
        List<TupleRowValue> rows = new ArrayList<TupleRowValue>();
        if (Objects.isNull(tupleSource)) {
            return rows;
        }
        if (tupleSource instanceof Collection<?>) {
            for (Object row : (Collection<?>) tupleSource) {
                addRow(rows, row);
            }
            return rows;
        }
        if (tupleSource.getClass().isArray()) {
            int length = Array.getLength(tupleSource);
            for (int index = 0; index < length; index++) {
                addRow(rows, Array.get(tupleSource, index));
            }
            return rows;
        }
        addRow(rows, tupleSource);
        return rows;
    }

    private static void addRow(final List<TupleRowValue> rows, final Object row) {
        if (Objects.isNull(row)) {
            return;
        }
        rows.add(TupleRowValue.of(row));
    }

    static final class TupleRowValue {

        private final Object row;

        private TupleRowValue(final Object row) {
            this.row = row;
        }

        static TupleRowValue of(final Object row) {
            return new TupleRowValue(row);
        }

        Object getRow() {
            return this.row;
        }

        boolean isArrayRow() {
            return this.row.getClass().isArray();
        }

        int length() {
            return this.isArrayRow() ? Array.getLength(this.row) : -1;
        }

        Object valueAt(final int index) {
            return Array.get(this.row, index);
        }

    }

}
