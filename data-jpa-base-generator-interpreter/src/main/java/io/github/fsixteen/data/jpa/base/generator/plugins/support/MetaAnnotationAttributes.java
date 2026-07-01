package io.github.fsixteen.data.jpa.base.generator.plugins.support;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.fsixteen.data.jpa.base.generator.annotations.PredicateRole;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledPredicateRoleSpec;

/**
 * canonical 元注解属性读取辅助器。
 *
 * <p>
 * 编译主链路允许业务定义“自定义注解 + 元注解”的快捷写法，因此这里统一负责两类事情：
 * 1. 按照最近优先、递归回退的规则读取 canonical 属性
 * 2. 解析注解最终参与的 predicate role
 * </p>
 *
 * <p>
 * 这保证了快捷注解、组合注解与直接使用 canonical 注解时的字段覆盖语义一致，
 * 也让 registry 与 provider 无需关心属性究竟来自当前注解还是来自上一层元注解。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class MetaAnnotationAttributes {

    private MetaAnnotationAttributes() {
    }

    public static Object fieldValue(final Annotation annotation, final String fieldName) {
        try {
            return annotation.annotationType().getMethod(fieldName).invoke(annotation);
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return metaAnnotationFieldValue(annotation, fieldName);
        }
    }

    public static <T> T fieldValue(final Annotation annotation, final String fieldName, final Class<T> type) {
        return cast(type, fieldValue(annotation, fieldName));
    }

    public static Object metaAnnotationFieldValue(final Annotation annotation, final String fieldName) {
        return readFieldValueFromMetaAnnotations(annotation, fieldName, new HashSet<Class<?>>());
    }

    public static boolean isSelectionAnnotation(final Class<?> annotationType) {
        return resolvePredicateRole(annotationType).isSelection();
    }

    public static boolean isExistenceAnnotation(final Class<?> annotationType) {
        return resolvePredicateRole(annotationType).isExistence();
    }

    public static CompiledPredicateRoleSpec resolvePredicateRole(final Class<?> annotationType) {
        return resolvePredicateRole(annotationType, new HashSet<Class<?>>());
    }

    private static Object readFieldValueFromMetaAnnotations(final Annotation annotation, final String fieldName, final Set<Class<?>> visited) {
        if (Objects.isNull(annotation) || Objects.isNull(annotation.annotationType()) || !visited.add(annotation.annotationType())) {
            return null;
        }
        for (Annotation metaAnnotation : annotation.annotationType().getAnnotations()) {
            Class<? extends Annotation> metaType = metaAnnotation.annotationType();
            if (isJdkMetaAnnotation(metaType)) {
                continue;
            }
            try {
                return metaType.getMethod(fieldName).invoke(metaAnnotation);
            } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
                // ignore and continue to the recursive pass
            }
        }
        for (Annotation metaAnnotation : annotation.annotationType().getAnnotations()) {
            Class<? extends Annotation> metaType = metaAnnotation.annotationType();
            if (isJdkMetaAnnotation(metaType)) {
                continue;
            }
            Object nestedValue = readFieldValueFromMetaAnnotations(metaAnnotation, fieldName, new HashSet<Class<?>>(visited));
            if (Objects.nonNull(nestedValue)) {
                return nestedValue;
            }
        }
        return null;
    }

    private static CompiledPredicateRoleSpec resolvePredicateRole(final Class<?> annotationType, final Set<Class<?>> visited) {
        if (Objects.isNull(annotationType) || !annotationType.isAnnotation() || !visited.add(annotationType)) {
            return CompiledPredicateRoleSpec.of(false, false);
        }
        PredicateRole direct = annotationType.getAnnotation(PredicateRole.class);
        if (Objects.nonNull(direct)) {
            return CompiledPredicateRoleSpec.of(resolveSelection(direct), resolveExistence(direct));
        }
        CompiledPredicateRoleSpec sameLevelExplicit = resolveSameLevelExplicitRoles(annotationType);
        if (sameLevelExplicit.isSelection() || sameLevelExplicit.isExistence()) {
            return sameLevelExplicit;
        }
        boolean selection = false;
        boolean existence = false;
        for (Annotation metaAnnotation : annotationType.getAnnotations()) {
            Class<? extends Annotation> metaType = metaAnnotation.annotationType();
            if (isJdkMetaAnnotation(metaType)) {
                continue;
            }
            CompiledPredicateRoleSpec nested = resolvePredicateRole(metaType, visited);
            selection = selection || nested.isSelection();
            existence = existence || nested.isExistence();
            if (selection && existence) {
                break;
            }
        }
        return CompiledPredicateRoleSpec.of(selection, existence);
    }

    private static CompiledPredicateRoleSpec resolveSameLevelExplicitRoles(final Class<?> annotationType) {
        boolean hasSpecificRoleCarrier = false;
        for (Annotation metaAnnotation : annotationType.getAnnotations()) {
            Class<? extends Annotation> metaType = metaAnnotation.annotationType();
            if (isJdkMetaAnnotation(metaType)) {
                continue;
            }
            if (Compare.class != metaType && Objects.nonNull(metaType.getAnnotation(PredicateRole.class))) {
                hasSpecificRoleCarrier = true;
                break;
            }
        }
        boolean selection = false;
        boolean existence = false;
        for (Annotation metaAnnotation : annotationType.getAnnotations()) {
            Class<? extends Annotation> metaType = metaAnnotation.annotationType();
            if (isJdkMetaAnnotation(metaType)) {
                continue;
            }
            PredicateRole role = metaType.getAnnotation(PredicateRole.class);
            if (Objects.isNull(role)) {
                continue;
            }
            if (hasSpecificRoleCarrier && Compare.class == metaType) {
                continue;
            }
            selection = selection || resolveSelection(role);
            existence = existence || resolveExistence(role);
        }
        return CompiledPredicateRoleSpec.of(selection, existence);
    }

    private static boolean resolveSelection(final PredicateRole role) {
        return Objects.nonNull(role) && (role.selection() || role.selectable());
    }

    private static boolean resolveExistence(final PredicateRole role) {
        return Objects.nonNull(role) && (role.existence() || role.existed());
    }

    private static boolean isJdkMetaAnnotation(final Class<? extends Annotation> annotationType) {
        return null != annotationType.getPackage() && "java.lang.annotation".equals(annotationType.getPackage().getName());
    }

    private static <T> T cast(final Class<T> type, final Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalStateException("Annotation field value type mismatch, expected " + type.getName() + " but got " + value.getClass().getName());
        }
        return type.cast(value);
    }

}
