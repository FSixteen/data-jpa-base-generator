package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * tuple 数据源归一化器.
 *
 * <p>
 * 它把集合、对象数组、primitive 数组与二维数组统一收敛为“逐行可访问”的内部模型,
 * 让后续列读取逻辑不再关心原始容器形态.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class TupleRowNormalizer {

    private TupleRowNormalizer() {
    }

    /**
     * 将任意 tuple 数据源统一归一化为逐行可访问的内部行模型.
     *
     * <p>
     * 支持单对象、集合、对象数组以及 primitive 数组外层容器.
     * 返回结果中的每个元素仅包装“一行”数据, 后续列访问阶段不再关心原始容器形态.
     * </p>
     *
     * @param tupleSource 原始 tuple 数据源
     * @return 归一化后的行列表；当数据源为 {@code null} 时返回空列表
     */
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

    /**
     * 将单行数据安全加入归一化结果.
     *
     * @param rows 归一化结果列表
     * @param row  待加入的单行数据；为 {@code null} 时直接忽略
     */
    private static void addRow(final List<TupleRowValue> rows, final Object row) {
        if (Objects.isNull(row)) {
            return;
        }
        rows.add(TupleRowValue.of(row));
    }

    static final class TupleRowValue {

        private final Object row;

        /**
         * 创建一条归一化后的 tuple 行包装对象.
         *
         * @param row 原始行对象
         */
        private TupleRowValue(final Object row) {
            this.row = row;
        }

        /**
         * 基于原始行对象创建包装实例.
         *
         * @param row 原始行对象
         * @return 对应的包装实例
         */
        static TupleRowValue of(final Object row) {
            return new TupleRowValue(row);
        }

        /**
         * 返回当前包装对象持有的原始行数据.
         *
         * @return 原始行对象
         */
        Object getRow() {
            return this.row;
        }

        /**
         * 判断当前行是否为数组行.
         *
         * @return 当前行底层对象是数组时返回 {@code true}
         */
        boolean isArrayRow() {
            return this.row.getClass().isArray();
        }

        /**
         * 返回数组行的列数.
         *
         * @return 当当前行为数组时返回其长度, 否则返回 {@code -1}
         */
        int length() {
            return this.isArrayRow() ? Array.getLength(this.row) : -1;
        }

        /**
         * 读取数组行指定下标的列值.
         *
         * @param index 列下标
         * @return 指定位置的列值
         * @throws IllegalArgumentException 当当前行不是数组, 或下标非法时由底层数组访问抛出
         */
        Object valueAt(final int index) {
            return Array.get(this.row, index);
        }

    }

}
