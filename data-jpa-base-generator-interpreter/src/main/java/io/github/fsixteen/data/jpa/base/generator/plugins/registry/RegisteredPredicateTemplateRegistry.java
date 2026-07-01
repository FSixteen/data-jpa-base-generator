package io.github.fsixteen.data.jpa.base.generator.plugins.registry;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.plugins.spi.ServiceLoaderBootstrap;

/**
 * 注册式谓词模板注册表。
 *
 * <p>
 * 该注册表维护“模板名 -> {@link RegisteredPredicateTemplateProvider}”映射，
 * 用于把一整套已定义好的左右表达式与操作符模板按名称暴露给运行期解析器。
 * 与 {@link PredicateExpressionRegistry} 的区别在于：
 * 前者只管理单个表达式模板，这里管理完整的谓词模板。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RegisteredPredicateTemplateRegistry {

    private static final Map<String, RegisteredPredicateTemplateProvider> REGISTRY = new ConcurrentHashMap<String, RegisteredPredicateTemplateProvider>();

    private RegisteredPredicateTemplateRegistry() {
    }

    public static void register(final String name, final RegisteredPredicateTemplateProvider provider) {
        REGISTRY.put(name, provider);
    }

    public static RegisteredPredicateTemplateProvider reference(final String name) {
        ServiceLoaderBootstrap.ensureLoaded();
        return REGISTRY.get(name);
    }

    public static RegisteredPredicateTemplateProvider require(final String name) {
        ServiceLoaderBootstrap.ensureLoaded();
        RegisteredPredicateTemplateProvider provider = reference(name);
        if (Objects.isNull(provider)) {
            throw new IllegalArgumentException("Unknown predicate template: " + name);
        }
        return provider;
    }

    public static void clear() {
        REGISTRY.clear();
    }

}
