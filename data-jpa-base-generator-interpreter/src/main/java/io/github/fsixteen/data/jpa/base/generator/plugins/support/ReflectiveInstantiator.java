package io.github.fsixteen.data.jpa.base.generator.plugins.support;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * 统一的反射类加载与实例化工具。
 *
 * <p>
 * 当前 compiled 主链路里，SPI provider、`@Constraint` provider、自定义条件实现、
 * predicate processor 都允许通过 className 或 class 方式注册。该工具将这些场景共用的
 * `Class.forName + newInstance + 类型校验` 行为集中到一处，避免不同入口各自维护近似实现。
 * </p>
 *
 * <p>
 * 这里刻意保持能力简单且严格：只支持无参构造实例化，并在实例化前先做目标类型校验，
 * 以便在扩展声明错误时尽早给出一致的异常语义。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class ReflectiveInstantiator {

    private ReflectiveInstantiator() {
    }

    public static Class<?> loadClass(final String className, final String errorPrefix) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(errorPrefix + className, e);
        }
    }

    public static Class<?> loadClass(final String className, final ClassLoader loader, final String errorPrefix) {
        try {
            return Class.forName(className, true, loader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(errorPrefix + className, e);
        }
    }

    public static <T> T instantiate(final Class<?> implClass, final Class<T> expectedType, final String errorPrefix) {
        Objects.requireNonNull(implClass, "implClass");
        if (!expectedType.isAssignableFrom(implClass)) {
            throw new IllegalArgumentException(errorPrefix + implClass.getName());
        }
        try {
            return expectedType.cast(implClass.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(errorPrefix + implClass.getName(), e);
        }
    }

}
