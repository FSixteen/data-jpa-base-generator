package io.github.fsixteen.data.jpa.base.generator.plugins.registry;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.plugins.spi.ServiceLoaderBootstrap;

/**
 * 表达式模板注册表.
 *
 * <p>
 * 该注册表维护“模板名 -> {@link PredicateExpressionTemplate}”映射,
 * 用于把可复用的表达式节点以名字形式暴露给 SPI 和业务扩展.
 * 注册项既可以由代码显式写入, 也可以通过 {@link ServiceLoaderBootstrap} 自动装载.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class PredicateExpressionRegistry {

    private static final Map<String, PredicateExpressionTemplate> REGISTRY = new ConcurrentHashMap<String, PredicateExpressionTemplate>();

    private PredicateExpressionRegistry() {
    }

    /**
     * 注册一个表达式模板.
     *
     * @param name     模板名
     * @param template 模板实现
     */
    public static void register(final String name, final PredicateExpressionTemplate template) {
        REGISTRY.put(name, template);
    }

    public static boolean contains(final String name) {
        ServiceLoaderBootstrap.ensureLoaded();
        return REGISTRY.containsKey(name);
    }

    /**
     * 按名称引用表达式模板；若不存在则返回 {@code null}.
     *
     * @param name 模板名
     * @return 模板实现或 {@code null}
     */
    public static PredicateExpressionTemplate reference(final String name) {
        ServiceLoaderBootstrap.ensureLoaded();
        return REGISTRY.get(name);
    }

    public static void remove(final String name) {
        REGISTRY.remove(name);
    }

    public static void clear() {
        REGISTRY.clear();
    }

    public static PredicateExpressionTemplate require(final String name) {
        ServiceLoaderBootstrap.ensureLoaded();
        PredicateExpressionTemplate template = reference(name);
        if (Objects.isNull(template)) {
            throw new IllegalArgumentException("Unknown expression template: " + name);
        }
        return template;
    }

}
