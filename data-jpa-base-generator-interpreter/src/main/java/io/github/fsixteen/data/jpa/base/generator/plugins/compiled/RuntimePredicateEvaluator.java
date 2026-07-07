package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;

/**
 * 纯运行时 canonical 谓词求值器.
 *
 * <p>
 * 与 `CompiledBuiltInPredicateSupport` 对应, 但这里不生成 JPA `Predicate`, 而是直接返回布尔结果,
 * 供 `Cases` 的分支命中条件判断等场景复用统一 canonical 规则.
 * </p>
 *
 * <p>
 * 该类型本质上是 compiled compare/in/between/null-check 规则的一份运行时镜像实现,
 * 重点在于让“是否命中某个分支”与“最终落成的查询谓词”遵循同一套比较语义.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RuntimePredicateEvaluator {

    private RuntimePredicateEvaluator() {
    }

    /**
     * 在纯运行时上下文中判断当前规格是否命中.
     *
     * <p>
     * 该方法会先把注解规格映射为统一的比较类型, 再按普通比较、集合比较、范围比较和空值判断等分支执行.
     * 其核心目标是保证运行时分支命中逻辑与真正生成的 JPA Predicate 使用同一套 canonical 语义.
     * </p>
     *
     * @param spec              已编译完成的注解规格, 包含比较操作、左右表达式及集合策略
     * @param args              当前请求或查询对象, 用于解析路径和动态字段值
     * @param currentFieldValue 当前字段值, 用于支持基于字段值的比较表达式
     * @return 若当前输入满足该规格对应的比较语义则返回 {@code true}, 否则返回 {@code false}
     * @throws IllegalArgumentException 当 {@code between/not between} 最终无法解析出恰好 2
     *                                  个边界值时抛出
     */
    public static boolean matches(final CompiledAnnotationSpec<?> spec, final Object args, final Object currentFieldValue) {
        ComparableType type = comparableType(spec);
        CompiledPredicateSpec predicateSpec = type.isRange() ? CompiledPredicateSpecs.between(type.getOperator(), spec)
            : type.isCollection() ? CompiledPredicateSpecs.in(type.getOperator(), spec) : CompiledPredicateSpecs.simple(type.getOperator(), spec);
        Object leftValue = RuntimeExpressionEvaluator.evaluate(predicateSpec.getLeft(), args, currentFieldValue);
        if (type.isRange()) {
            List<Object> rangeValues = RuntimeExpressionEvaluator.evaluateRange(predicateSpec.getRight(), predicateSpec.getExtraOperands(), args,
                currentFieldValue);
            validateRangeValueCount(rangeValues, spec.getValueFieldName());
            boolean matched = compareBetween(leftValue, rangeValues);
            return ComparableType.NOT_BETWEEN == type ? !matched : matched;
        }
        if (type.isCollection()) {
            Object rightValue = RuntimeExpressionEvaluator.evaluate(predicateSpec.getRight(), args, currentFieldValue);
            List<Object> normalizedValues = normalizeCollectionOperands(spec, leftValue, rightValue);
            if (normalizedValues.isEmpty()) {
                return false;
            }
            boolean matched = compareIn(leftValue, normalizedValues);
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
     * 解析当前规格对应的统一比较类别.
     *
     * @param spec 已编译完成的注解规格
     * @return 当前规格归一化后的比较类型
     */
    private static ComparableType comparableType(final CompiledAnnotationSpec<?> spec) {
        return CompiledComparableTypeResolver.resolve(spec.getPredicateCore().getOp(), spec.getCollectionPolicy().isSplit());
    }

    /**
     * 执行普通二元比较.
     *
     * <p>
     * 这里统一处理等值、大小比较和字符串包含关系比较.
     * 当任一操作数为 {@code null} 时, 直接按 SQL 三值逻辑中的“比较结果不成立”处理并返回 {@code false},
     * 仅 {@code is null/is not null} 由上层单独处理.
     * </p>
     *
     * @param type       比较类型
     * @param leftValue  左操作数
     * @param rightValue 右操作数
     * @return 比较结果
     */
    private static boolean compareBinary(final ComparableType type, final Object leftValue, final Object rightValue) {
        if (hasSqlNullOperand(leftValue, rightValue)) {
            return false;
        }
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
     * 按 Comparable 语义比较两个值.
     *
     * <p>
     * 该方法假定调用方已经确认当前比较操作要求值具备自然顺序,
     * 因此这里只负责执行受控收窄并调用 {@link Comparable#compareTo(Object)}.
     * </p>
     *
     * @param leftValue  左操作数, 必须实现 {@link Comparable}
     * @param rightValue 右操作数, 应与左操作数具备兼容的比较语义
     * @return 小于 0 表示左值更小, 等于 0 表示相等, 大于 0 表示左值更大
     * @throws ClassCastException 当左右值虽然实现了 {@link Comparable}, 但彼此不可比较时抛出
     */
    private static int compareComparable(final Object leftValue, final Object rightValue) {
        return asComparable(leftValue).compareTo(rightValue);
    }

    /**
     * 执行 between 语义比较.
     *
     * <p>
     * 只有在范围值列表恰好包含 2 个边界值, 且左值与两个边界值都非空时,
     * 才会按闭区间语义执行 {@code left >= start && left <= end} 判断.
     * 其余情况统一返回 {@code false}, 以贴近 SQL 中空值参与范围比较时“不成立”的行为.
     * </p>
     *
     * @param leftValue   左操作数
     * @param rangeValues 已规范化的范围值列表, 预期顺序为起始值、结束值
     * @return 是否命中闭区间范围
     */
    private static boolean compareBetween(final Object leftValue, final List<Object> rangeValues) {
        if (null == rangeValues || 2 != rangeValues.size() || hasSqlNullOperand(leftValue, rangeValues.get(0))
            || hasSqlNullOperand(leftValue, rangeValues.get(1))) {
            return false;
        }
        return compareComparable(leftValue, rangeValues.get(0)) >= 0 && compareComparable(leftValue, rangeValues.get(1)) <= 0;
    }

    /**
     * 执行 in 语义比较.
     *
     * <p>
     * 右操作数可以是集合、对象数组或单个值.
     * 当前实现基于 Java 容器的包含判断语义执行比较, 不会主动做元素级类型转换.
     * </p>
     *
     * @param leftValue  左操作数
     * @param rightValue 右操作数, 可以是集合、数组或单值
     * @return 若左值存在于右侧候选集合中则返回 {@code true}
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
     * 将集合类右操作数规范化为可直接参与 {@code in/not in} 比较的值列表.
     *
     * <p>
     * 该方法会统一展开集合或数组右值, 并应用注解规格中的忽略策略,
     * 从而避免把空串、约定忽略值等无效元素带入最终比较.
     * </p>
     *
     * @param spec       已编译完成的注解规格, 用于判断某个候选值是否应被忽略
     * @param leftValue  左操作数；若其本身为空, 则直接返回空结果列表
     * @param rightValue 右操作数, 可以是集合、数组或单值
     * @return 过滤后的右值列表；当左右操作数任一为空, 或全部候选值均被忽略时返回空列表
     */
    private static List<Object> normalizeCollectionOperands(final CompiledAnnotationSpec<?> spec, final Object leftValue, final Object rightValue) {
        List<Object> normalized = new ArrayList<Object>();
        if (Objects.isNull(leftValue) || Objects.isNull(rightValue)) {
            return normalized;
        }
        if (rightValue instanceof Collection<?>) {
            for (Object value : (Collection<?>) rightValue) {
                if (!spec.shouldIgnore(value)) {
                    normalized.add(value);
                }
            }
            return normalized;
        }
        if (rightValue.getClass().isArray()) {
            int length = Array.getLength(rightValue);
            for (int index = 0; index < length; index++) {
                Object value = Array.get(rightValue, index);
                if (!spec.shouldIgnore(value)) {
                    normalized.add(value);
                }
            }
            return normalized;
        }
        if (!spec.shouldIgnore(rightValue)) {
            normalized.add(rightValue);
        }
        return normalized;
    }

    /**
     * 校验范围比较最终得到的值个数是否满足 {@code between/not between} 语义.
     *
     * <p>
     * 当前实现要求范围值必须严格等于 2 个, 否则说明 DSL 配置与运行时输入无法组成合法区间,
     * 应立即失败, 而不是静默降级为“不命中”.
     * </p>
     *
     * @param rangeValues    已求值的范围值列表
     * @param valueFieldName 当前规格对应的字段名, 仅用于补充异常信息
     * @throws IllegalArgumentException 当范围值数量不是 2 个时抛出
     */
    private static void validateRangeValueCount(final List<Object> rangeValues, final String valueFieldName) {
        if (null == rangeValues || 2 != rangeValues.size()) {
            throw new IllegalArgumentException("Between-style predicate requires exactly 2 values"
                + (Objects.nonNull(valueFieldName) && !valueFieldName.isEmpty() ? " for '" + valueFieldName + "'" : "") + ", but got: " + rangeValues);
        }
    }

    /**
     * 判断当前比较是否包含会导致 SQL 比较结果失效的空操作数.
     *
     * @param leftValue  左操作数
     * @param rightValue 右操作数
     * @return 任一操作数为 {@code null} 时返回 {@code true}
     */
    private static boolean hasSqlNullOperand(final Object leftValue, final Object rightValue) {
        return Objects.isNull(leftValue) || Objects.isNull(rightValue);
    }

    /**
     * 将任意值规范为字符串, null 回退为空串.
     *
     * <p>
     * 该方法仅用于 {@code contains/startWith/endWith} 一类运行时字符串比较,
     * 其空值回退策略与上层“先判空再比较”的逻辑配合使用, 避免额外空指针处理.
     * </p>
     *
     * @param value 任意输入值
     * @return 对应的字符串表现；当值为 {@code null} 时返回空串
     */
    private static String string(final Object value) {
        return Objects.toString(value, "");
    }

    /**
     * 将值受控收窄为 Comparable.
     *
     * @param value 待比较的值
     * @return 受控收窄后的 {@link Comparable} 实例
     * @throws ClassCastException 当值未实现 {@link Comparable} 时抛出
     */
    private static Comparable<Object> asComparable(final Object value) {
        return castComparable(value);
    }

    /**
     * 在比较语义已经确定的前提下, 把原始值收窄为 {@link Comparable}.
     *
     * <p>
     * 该转换不做额外运行时保护, 默认调用链已经保证只有需要自然顺序比较的操作符才会进入这里.
     * </p>
     *
     * @param value 待收窄的值
     * @param <T>   比较目标的泛型类型
     * @return 收窄后的 {@link Comparable} 实例
     * @throws ClassCastException 当值未实现 {@link Comparable} 时抛出
     */
    @SuppressWarnings({ "unchecked" })
    private static <T> Comparable<T> castComparable(final Object value) {
        // 纯运行时比较同样只能在命中 GT/GTE/LT/LTE/BETWEEN 等语义后再假定值实现了 Comparable.
        return (Comparable<T>) value;
    }

}
