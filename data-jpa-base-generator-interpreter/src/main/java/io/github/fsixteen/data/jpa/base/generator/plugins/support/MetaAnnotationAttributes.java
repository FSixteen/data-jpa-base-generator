package io.github.fsixteen.data.jpa.base.generator.plugins.support;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.fsixteen.data.jpa.base.generator.annotations.PredicateRole;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledPredicateRoleSpec;

/**
 * canonical 元注解属性读取辅助器.
 *
 * <p>
 * 编译主链路允许业务定义“自定义注解 + 元注解”的快捷写法, 因此这里统一负责两类事情：
 * 1. 按照最近优先、递归回退的规则读取 canonical 属性
 * 2. 解析注解最终参与的 predicate role
 * </p>
 *
 * <p>
 * 这保证了快捷注解、组合注解与直接使用 canonical 注解时的字段覆盖语义一致,
 * 也让 registry 与 provider 无需关心属性究竟来自当前注解还是来自上一层元注解.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class MetaAnnotationAttributes {

    private MetaAnnotationAttributes() {
    }

    /**
     * 读取注解上的指定属性值, 必要时递归回退到元注解链.
     *
     * <p>
     * 该方法先尝试直接读取当前注解属性；若失败, 再按元注解链递归回退,
     * 以支持“组合注解覆盖 canonical 注解属性”的快捷用法.
     * </p>
     *
     * @param annotation 原始注解实例
     * @param fieldName  属性名
     * @return 解析到的属性值；未找到时返回 {@code null}
     */
    public static Object fieldValue(final Annotation annotation, final String fieldName) {
        try {
            return annotation.annotationType().getMethod(fieldName).invoke(annotation);
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return metaAnnotationFieldValue(annotation, fieldName);
        }
    }

    /**
     * 读取注解属性并受控收窄为目标类型.
     *
     * @param annotation 原始注解实例
     * @param fieldName  属性名
     * @param type       目标类型
     * @param <T>        目标类型泛型
     * @return 收窄后的属性值；未找到时返回 {@code null}
     * @throws IllegalStateException 当实际属性值类型与目标类型不兼容时抛出
     */
    public static <T> T fieldValue(final Annotation annotation, final String fieldName, final Class<T> type) {
        return cast(type, fieldValue(annotation, fieldName));
    }

    /**
     * 仅沿元注解链递归读取指定属性值.
     *
     * @param annotation 原始注解实例
     * @param fieldName  属性名
     * @return 从元注解链中解析到的属性值；未找到时返回 {@code null}
     */
    public static Object metaAnnotationFieldValue(final Annotation annotation, final String fieldName) {
        return readFieldValueFromMetaAnnotations(annotation, fieldName, new HashSet<Class<?>>());
    }

    /**
     * 判断注解最终是否具备 selection 角色.
     *
     * @param annotationType 注解类型
     * @return 具备 selection 角色时返回 {@code true}
     */
    public static boolean isSelectionAnnotation(final Class<?> annotationType) {
        return resolvePredicateRole(annotationType).isSelection();
    }

    /**
     * 判断注解最终是否具备 existence 角色.
     *
     * @param annotationType 注解类型
     * @return 具备 existence 角色时返回 {@code true}
     */
    public static boolean isExistenceAnnotation(final Class<?> annotationType) {
        return resolvePredicateRole(annotationType).isExistence();
    }

    /**
     * 解析注解最终生效的 predicate role.
     *
     * @param annotationType 注解类型
     * @return 解析后的 predicate role 规格
     */
    public static CompiledPredicateRoleSpec resolvePredicateRole(final Class<?> annotationType) {
        return resolvePredicateRole(annotationType, new HashSet<Class<?>>());
    }

    /**
     * 按最近优先、递归回退的策略在元注解链中读取属性值.
     *
     * @param annotation 当前注解实例
     * @param fieldName  属性名
     * @param visited    已访问注解类型集合, 用于避免循环递归
     * @return 解析到的属性值；未找到时返回 {@code null}
     */
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

    /**
     * 递归解析注解及其元注解链上的 predicate role.
     *
     * <p>
     * 解析顺序依次为：
     * 1. 当前注解直接声明的 {@link PredicateRole}
     * 2. 同层元注解中的显式 role
     * 3. 更深层元注解链递归汇总
     * </p>
     *
     * @param annotationType 注解类型
     * @param visited        已访问注解类型集合, 用于避免循环递归
     * @return 解析后的 predicate role 规格
     */
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

    /**
     * 解析同层元注解中显式声明的 predicate role.
     *
     * <p>
     * 当同层存在比 {@link Compare} 更具体的 role carrier 时, 会忽略 {@link Compare} 自带的通用角色,
     * 以避免其覆盖业务显式角色.
     * </p>
     *
     * @param annotationType 注解类型
     * @return 同层解析得到的 predicate role 规格
     */
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

    /**
     * 判断 role 是否声明了 selection 语义.
     *
     * @param role predicate role 注解
     * @return 声明了 selection/selectable 之一时返回 {@code true}
     */
    private static boolean resolveSelection(final PredicateRole role) {
        return Objects.nonNull(role) && (role.selection() || role.selectable());
    }

    /**
     * 判断 role 是否声明了 existence 语义.
     *
     * @param role predicate role 注解
     * @return 声明了 existence/existed 之一时返回 {@code true}
     */
    private static boolean resolveExistence(final PredicateRole role) {
        return Objects.nonNull(role) && (role.existence() || role.existed());
    }

    /**
     * 判断当前注解类型是否属于 JDK 自带元注解.
     *
     * @param annotationType 注解类型
     * @return 属于 `java.lang.annotation` 包时返回 {@code true}
     */
    private static boolean isJdkMetaAnnotation(final Class<? extends Annotation> annotationType) {
        return null != annotationType.getPackage() && "java.lang.annotation".equals(annotationType.getPackage().getName());
    }

    /**
     * 将任意对象受控收窄为目标类型.
     *
     * @param type  目标类型
     * @param value 原始值
     * @param <T>   目标类型泛型
     * @return 收窄后的值；原值为空时返回 {@code null}
     * @throws IllegalStateException 当原值类型与目标类型不兼容时抛出
     */
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
