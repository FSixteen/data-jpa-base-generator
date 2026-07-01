package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 结构化 case-when 分组的纯运行时求值器。
 */
public final class RuntimePredicateGroupEvaluator {

    private RuntimePredicateGroupEvaluator() {
    }

    public static boolean matches(final PredicateGroupSpec groupSpec, final Object args, final Object currentFieldValue) {
        return evaluate(groupSpec, args, currentFieldValue).orElse(Boolean.FALSE).booleanValue();
    }

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
