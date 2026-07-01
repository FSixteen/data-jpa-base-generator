package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.annotations.Constraint;
import io.github.fsixteen.data.jpa.base.generator.annotations.ProviderRef;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.ReflectiveInstantiator;

/**
 * compiled 谓词 provider 注册表。
 *
 * <p>
 * 该注册表维护“注解类型 -> {@link CompiledPredicateProvider}”映射，
 * 是当前解释器运行期路由注解到具体执行逻辑的唯一入口。
 * 内建注解、SPI 扩展注解、组合注解以及 {@code @Constraint} provider 最终都会在这里完成解析。
 * </p>
 *
 * <p>
 * 解析顺序为：
 * </p>
 * <ol>
 * <li>先命中显式注册项</li>
 * <li>再尝试解析 {@code @Constraint}</li>
 * <li>最后沿元注解递归查找可复用的 canonical provider</li>
 * </ol>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateProviderRegistry {

    /**
     * 统一记录“注解类型 -> compiled provider”映射。
     * 内建注解、测试 SPI 和未来新增注解都汇聚到这里，避免再次分裂注册中心。
     */
    private static final Map<Class<? extends Annotation>,
        CompiledPredicateProvider> REGISTRY = new ConcurrentHashMap<Class<? extends Annotation>, CompiledPredicateProvider>();

    private CompiledPredicateProviderRegistry() {
    }

    /**
     * 显式注册一条“注解类型 -> provider”映射。
     *
     * @param annotationType 注解类型
     * @param provider       provider 实现
     */
    public static void register(final Class<? extends Annotation> annotationType, final CompiledPredicateProvider provider) {
        REGISTRY.put(annotationType, provider);
    }

    public static boolean containsRegistered(final Class<? extends Annotation> annotationType) {
        return REGISTRY.containsKey(annotationType);
    }

    public static CompiledPredicateProvider reference(final Class<? extends Annotation> annotationType) {
        ServiceLoaderBootstrap.ensureLoaded();
        return reference(annotationType, new HashSet<Class<?>>());
    }

    /**
     * 按注解类型获取 provider；若无法解析则抛异常。
     *
     * @param annotationType 注解类型
     * @return provider 实现
     */
    public static CompiledPredicateProvider require(final Class<? extends Annotation> annotationType) {
        ServiceLoaderBootstrap.ensureLoaded();
        CompiledPredicateProvider provider = reference(annotationType);
        if (Objects.isNull(provider)) {
            throw new IllegalArgumentException("Unknown compiled predicate provider: " + annotationType);
        }
        return provider;
    }

    public static boolean containsKey(final Class<? extends Annotation> annotationType) {
        ServiceLoaderBootstrap.ensureLoaded();
        return Objects.nonNull(reference(annotationType));
    }

    public static void clear() {
        REGISTRY.clear();
    }

    private static CompiledPredicateProvider registerConstraintProvider(final Class<? extends Annotation> annotationType) {
        if (Objects.isNull(annotationType)) {
            return null;
        }
        synchronized (REGISTRY) {
            CompiledPredicateProvider provider = REGISTRY.get(annotationType);
            if (Objects.nonNull(provider)) {
                return provider;
            }
            Constraint constraint = annotationType.getAnnotation(Constraint.class);
            if (Objects.isNull(constraint)) {
                return null;
            }
            provider = instantiateConstraintProvider(annotationType, constraint);
            if (Objects.nonNull(provider)) {
                REGISTRY.put(annotationType, provider);
            }
            return provider;
        }
    }

    private static CompiledPredicateProvider instantiateConstraintProvider(final Class<? extends Annotation> annotationType, final Constraint constraint) {
        Class<?> implClass = resolveConstraintClass(constraint.provider());
        if (Objects.isNull(implClass)) {
            return null;
        }
        return ReflectiveInstantiator.instantiate(implClass, CompiledPredicateProvider.class,
            "Failed to instantiate @Constraint provider for " + annotationType.getName() + ": ");
    }

    private static Class<?> resolveConstraintClass(final ProviderRef provider) {
        if (Objects.isNull(provider)) {
            return null;
        }
        if (Void.class != provider.providerClass()) {
            return provider.providerClass();
        }
        return null;
    }

    private static CompiledPredicateProvider reference(final Class<? extends Annotation> annotationType, final Set<Class<?>> visited) {
        if (Objects.isNull(annotationType) || !visited.add(annotationType)) {
            return null;
        }
        CompiledPredicateProvider provider = REGISTRY.get(annotationType);
        if (Objects.nonNull(provider)) {
            return provider;
        }
        provider = registerConstraintProvider(annotationType);
        if (Objects.nonNull(provider)) {
            return provider;
        }
        provider = registerMetaProvider(annotationType, visited);
        if (Objects.nonNull(provider)) {
            REGISTRY.put(annotationType, provider);
        }
        return provider;
    }

    private static CompiledPredicateProvider registerMetaProvider(final Class<? extends Annotation> annotationType, final Set<Class<?>> visited) {
        for (Annotation metaAnnotation : annotationType.getAnnotations()) {
            Class<? extends Annotation> metaType = metaAnnotation.annotationType();
            if (isJdkMetaAnnotation(metaType)) {
                continue;
            }
            CompiledPredicateProvider provider = reference(metaType, new HashSet<Class<?>>(visited));
            if (Objects.nonNull(provider)) {
                return provider;
            }
        }
        return null;
    }

    private static boolean isJdkMetaAnnotation(final Class<? extends Annotation> annotationType) {
        return Objects.nonNull(annotationType) && Objects.nonNull(annotationType.getPackage())
            && "java.lang.annotation".equals(annotationType.getPackage().getName());
    }

}
