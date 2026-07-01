package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.ReflectiveInstantiator;

/**
 * compiled 主链路下的扩展引用解析器。
 *
 * <p>
 * 当前 canonical 注解体系里，自定义条件扩展与 predicate processor 统一通过 class
 * 引用声明。该类负责处理这些扩展的实例化、类型校验、缓存复用与错误语义。
 * </p>
 *
 * <p>
 * 这样 `Cases`、集合项过滤扩展与 SPI 扩展都可以共享同一套解析行为，避免散落多份
 * 近似但并不完全一致的反射逻辑。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class CompiledReferenceResolvers {

    private static final Map<Class<?>, PredicateProcessor> CLASS_PROCESSOR_CACHE = new ConcurrentHashMap<Class<?>, PredicateProcessor>();

    private static final Map<Class<?>, Predicate<Object>> CLASS_PREDICATE_CACHE = new ConcurrentHashMap<Class<?>, Predicate<Object>>();

    private CompiledReferenceResolvers() {
    }

    /**
     * 解析并缓存一个 {@link PredicateProcessor} 类型扩展。
     */
    static PredicateProcessor processorByClass(final Class<?> processorClass) throws ReflectiveOperationException {
        validateProcessorClass(processorClass);
        PredicateProcessor cached = CLASS_PROCESSOR_CACHE.get(processorClass);
        if (null != cached) {
            return cached;
        }
        synchronized (CLASS_PROCESSOR_CACHE) {
            cached = CLASS_PROCESSOR_CACHE.get(processorClass);
            if (null != cached) {
                return cached;
            }
            PredicateProcessor processor = ReflectiveInstantiator.instantiate(processorClass, PredicateProcessor.class,
                "Failed to instantiate predicate processor: ");
            CLASS_PROCESSOR_CACHE.put(processorClass, processor);
            return processor;
        }
    }

    /**
     * 解析并缓存一个 {@link Predicate} 类型扩展。
     */
    static Predicate<Object> predicateByClass(final Class<?> predicateClass) throws ReflectiveOperationException {
        validatePredicateClass(predicateClass);
        Predicate<Object> cached = CLASS_PREDICATE_CACHE.get(predicateClass);
        if (null != cached) {
            return cached;
        }
        synchronized (CLASS_PREDICATE_CACHE) {
            cached = CLASS_PREDICATE_CACHE.get(predicateClass);
            if (null != cached) {
                return cached;
            }
            Predicate<Object> predicate = instantiatePredicate(predicateClass);
            CLASS_PREDICATE_CACHE.put(predicateClass, predicate);
            return predicate;
        }
    }

    /**
     * 校验给定类型是否实现了 {@link PredicateProcessor}。
     */
    private static void validateProcessorClass(final Class<?> clazz) {
        if (!PredicateProcessor.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                "Class " + clazz.getName() + " does not implement io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor");
        }
    }

    /**
     * 校验给定类型是否实现了 {@link Predicate}。
     */
    private static void validatePredicateClass(final Class<?> clazz) {
        if (!Predicate.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " does not implement java.util.function.Predicate");
        }
    }

    /**
     * 实例化一个自定义 predicate 扩展。
     */
    private static Predicate<Object> instantiatePredicate(final Class<?> predicateClass) throws ReflectiveOperationException {
        return instantiatePredicateTyped(predicateClass);
    }

    /**
     * 在类型校验完成后执行受控泛型收窄并实例化 predicate。
     */
    @SuppressWarnings("unchecked")
    private static <T> Predicate<T> instantiatePredicateTyped(final Class<?> predicateClass) throws ReflectiveOperationException {
        return ReflectiveInstantiator.instantiate(predicateClass, Predicate.class, "Failed to instantiate predicate extension: ");
    }

}
