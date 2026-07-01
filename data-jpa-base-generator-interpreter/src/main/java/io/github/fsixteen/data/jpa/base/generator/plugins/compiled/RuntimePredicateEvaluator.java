package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;

/**
 * 纯运行时 canonical 谓词求值器。
 *
 * <p>
 * 与 `CompiledBuiltInPredicateSupport` 对应，但这里不生成 JPA `Predicate`，而是直接返回布尔结果，
 * 供 `Cases` 的分支命中条件判断等场景复用统一 canonical 规则。
 * </p>
 *
 * <p>
 * 该类型本质上是 compiled compare/in/between/null-check 规则的一份运行时镜像实现，
 * 重点在于让“是否命中某个分支”与“最终落成的查询谓词”遵循同一套比较语义。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RuntimePredicateEvaluator {

    private RuntimePredicateEvaluator() {
    }

    /**
     * 在纯运行时上下文中判断当前规格是否命中。
     */
    public static boolean matches(final CompiledAnnotationSpec<?> spec, final Object args, final Object currentFieldValue) {
        ComparableType type = comparableType(spec);
        CompiledPredicateSpec predicateSpec = type.isRange() ? CompiledPredicateSpecs.between(type.getOperator(), spec)
            : type.isCollection() ? CompiledPredicateSpecs.in(type.getOperator(), spec) : CompiledPredicateSpecs.simple(type.getOperator(), spec);
        Object leftValue = RuntimeExpressionEvaluator.evaluate(predicateSpec.getLeft(), args, currentFieldValue);
        if (type.isRange()) {
            List<Object> rangeValues = RuntimeExpressionEvaluator.evaluateRange(predicateSpec.getRight(), predicateSpec.getExtraOperands(), args,
                currentFieldValue);
            boolean matched = compareBetween(leftValue, rangeValues);
            return ComparableType.NOT_BETWEEN == type ? !matched : matched;
        }
        if (type.isCollection()) {
            Object rightValue = RuntimeExpressionEvaluator.evaluate(predicateSpec.getRight(), args, currentFieldValue);
            boolean matched = compareIn(leftValue, rightValue);
            return type.isNegated() ? !matched : matched;
        }
        if (ComparableType.IS_NULL == type) {
            return Objects.isNull(leftValue);
        }
        if (ComparableType.IS_NOT_NULL == type) {
            return Objects.nonNull(leftValue);
        }
        Object rightValue = RuntimeExpressionEvaluator.evaluate(predicateSpec.getRight(), args, currentFieldValue);
        return compareBinary(type, leftValue, rightValue);
    }

    /**
     * 解析当前规格对应的统一比较类别。
     */
    private static ComparableType comparableType(final CompiledAnnotationSpec<?> spec) {
        return CompiledComparableTypeResolver.resolve(spec.getPredicateCore().getOp(), spec.getCollectionPolicy().isSplit());
    }

    /**
     * 执行普通二元比较。
     */
    private static boolean compareBinary(final ComparableType type, final Object leftValue, final Object rightValue) {
        switch (type) {
            case NEQ:
                return !Objects.equals(leftValue, rightValue);
            case GT:
                return compareComparable(leftValue, rightValue) > 0;
            case GTE:
                return compareComparable(leftValue, rightValue) >= 0;
            case LT:
                return compareComparable(leftValue, rightValue) < 0;
            case LTE:
                return compareComparable(leftValue, rightValue) <= 0;
            case CONTAINS:
                return string(leftValue).contains(string(rightValue));
            case NOT_CONTAINS:
                return !string(leftValue).contains(string(rightValue));
            case START_WITH:
            case LEFT:
                return string(leftValue).startsWith(string(rightValue));
            case END_WITH:
            case RIGHT:
                return string(leftValue).endsWith(string(rightValue));
            case EQ:
            default:
                return Objects.equals(leftValue, rightValue);
        }
    }

    /**
     * 按 Comparable 语义比较两个值。
     */
    private static int compareComparable(final Object leftValue, final Object rightValue) {
        if (Objects.isNull(leftValue) || Objects.isNull(rightValue)) {
            return Objects.equals(leftValue, rightValue) ? 0 : Objects.isNull(leftValue) ? -1 : 1;
        }
        return asComparable(leftValue).compareTo(rightValue);
    }

    /**
     * 执行 between 语义比较。
     */
    private static boolean compareBetween(final Object leftValue, final List<Object> rangeValues) {
        if (null == rangeValues || rangeValues.size() < 2) {
            return false;
        }
        return compareComparable(leftValue, rangeValues.get(0)) >= 0 && compareComparable(leftValue, rangeValues.get(1)) <= 0;
    }

    /**
     * 执行 in 语义比较。
     */
    private static boolean compareIn(final Object leftValue, final Object rightValue) {
        if (rightValue instanceof Collection<?>) {
            return ((Collection<?>) rightValue).contains(leftValue);
        }
        if (rightValue instanceof Object[]) {
            for (Object value : (Object[]) rightValue) {
                if (Objects.equals(value, leftValue)) {
                    return true;
                }
            }
            return false;
        }
        return Objects.equals(leftValue, rightValue);
    }

    /**
     * 将任意值规范为字符串，null 回退为空串。
     */
    private static String string(final Object value) {
        return Objects.toString(value, "");
    }

    /**
     * 将值受控收窄为 Comparable。
     */
    private static Comparable<Object> asComparable(final Object value) {
        return castComparable(value);
    }

    @SuppressWarnings({ "unchecked" })
    private static <T> Comparable<T> castComparable(final Object value) {
        // 纯运行时比较同样只能在命中 GT/GTE/LT/LTE/BETWEEN 等语义后再假定值实现了 Comparable。
        return (Comparable<T>) value;
    }

}
