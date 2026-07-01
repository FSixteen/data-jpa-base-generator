package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;

/**
 * compiled 主链路中 {@link CompareOp} 到 {@link ComparableType} 的统一解析器。
 *
 * <p>
 * provider 分发、运行时 case 判断和内建 support 都应依赖这里，避免相同语义映射在多个类里重复维护。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledComparableTypeResolver {

    private CompiledComparableTypeResolver() {
    }

    /**
     * 将注解层 {@link CompareOp} 与集合拆分语义解析为统一比较类别。
     *
     * @param op    compare 操作符
     * @param split 是否启用字符串拆分集合语义
     * @return 统一比较类型
     */
    public static ComparableType resolve(final CompareOp op, final boolean split) {
        if (CompareOp.NE == op) {
            return ComparableType.NEQ;
        }
        if (CompareOp.GT == op) {
            return ComparableType.GT;
        }
        if (CompareOp.GTE == op) {
            return ComparableType.GTE;
        }
        if (CompareOp.LT == op) {
            return ComparableType.LT;
        }
        if (CompareOp.LTE == op) {
            return ComparableType.LTE;
        }
        if (CompareOp.LIKE == op) {
            return ComparableType.CONTAINS;
        }
        if (CompareOp.NOT_LIKE == op) {
            return ComparableType.NOT_CONTAINS;
        }
        if (CompareOp.STARTS_WITH == op) {
            return ComparableType.START_WITH;
        }
        if (CompareOp.ENDS_WITH == op) {
            return ComparableType.END_WITH;
        }
        if (CompareOp.IN == op) {
            return split ? ComparableType.SPLIT_IN : ComparableType.IN;
        }
        if (CompareOp.NOT_IN == op) {
            return split ? ComparableType.SPLIT_NOT_IN : ComparableType.NOT_IN;
        }
        if (CompareOp.BETWEEN == op) {
            return ComparableType.BETWEEN;
        }
        if (CompareOp.NOT_BETWEEN == op) {
            return ComparableType.NOT_BETWEEN;
        }
        if (CompareOp.IS_NULL == op) {
            return ComparableType.IS_NULL;
        }
        if (CompareOp.IS_NOT_NULL == op) {
            return ComparableType.IS_NOT_NULL;
        }
        return ComparableType.EQ;
    }

}
