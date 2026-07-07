package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 结构化 case-when 分组的纯运行时求值器.
 */
public final class RuntimePredicateGroupEvaluator {

    private RuntimePredicateGroupEvaluator() {
    }

    /**
     * 在纯运行时上下文中递归判断一个分组树是否命中.
     *
     * <p>
     * 该方法是 `Cases` 结构化分组匹配的布尔入口,
     * 会统一复用叶子注解的运行时谓词求值逻辑, 并按当前分组的 junction 规则聚合子结果.
     * </p>
     *
     * @param groupSpec         待求值的分组规格
     * @param args              当前请求或查询对象, 用于读取各叶子节点绑定字段
     * @param currentFieldValue 当前字段值, 用于支持字段值表达式在运行时求值
     * @return 若分组命中则返回 {@code true}, 否则返回 {@code false}
     */
    public static boolean matches(final PredicateGroupSpec groupSpec, final Object args, final Object currentFieldValue) {
        return evaluate(groupSpec, args, currentFieldValue).orElse(Boolean.FALSE).booleanValue();
    }

    /**
     * 递归求值一个分组节点, 并保留“无有效结果”的三态信息.
     *
     * <p>
     * 当分组为空、全部叶子都因 ignore 规则被跳过, 或全部子组都无法产生有效布尔值时,
     * 返回 {@link Optional#empty()}, 以便上层决定是否把该分组视为“不命中”.
     * </p>
     *
     * @param groupSpec         待求值的分组规格
     * @param args              当前请求或查询对象, 用于读取叶子节点值
     * @param currentFieldValue 当前字段值, 用于支持字段值表达式求值
     * @return 分组求值结果；无有效子结果时返回空 Optional
     */
    private static Optional<Boolean> evaluate(final PredicateGroupSpec groupSpec, final Object args, final Object currentFieldValue) {
        if (Objects.isNull(groupSpec) || groupSpec.isEmpty()) {
            return Optional.empty();
        }
        List<Boolean> values = new ArrayList<Boolean>();
        for (CompiledAnnotationSpec<?> annotation : groupSpec.getAnnotations()) {
            Object fieldValue = annotation.readAndTrim(args);
            if (annotation.shouldIgnore(fieldValue)) {
                continue;
            }
            values.add(Boolean.valueOf(RuntimePredicateEvaluator.matches(annotation, args, currentFieldValue)));
        }
        for (PredicateGroupSpec child : groupSpec.getGroups()) {
            Optional<Boolean> childValue = evaluate(child, args, currentFieldValue);
            if (childValue.isPresent()) {
                values.add(childValue.get());
            }
        }
        if (values.isEmpty()) {
            return Optional.empty();
        }
        if (PredicateGroupSpec.JunctionType.OR == groupSpec.getJunctionType()) {
            for (Boolean value : values) {
                if (Boolean.TRUE.equals(value)) {
                    return Optional.of(Boolean.TRUE);
                }
            }
            return Optional.of(Boolean.FALSE);
        }
        for (Boolean value : values) {
            if (!Boolean.TRUE.equals(value)) {
                return Optional.of(Boolean.FALSE);
            }
        }
        return Optional.of(Boolean.TRUE);
    }

}
