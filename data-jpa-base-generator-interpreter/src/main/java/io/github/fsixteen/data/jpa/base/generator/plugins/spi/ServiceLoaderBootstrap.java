package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.BuiltInCompiledPredicateProviders;
import io.github.fsixteen.data.jpa.base.generator.plugins.registry.PredicateExpressionRegistry;
import io.github.fsixteen.data.jpa.base.generator.plugins.registry.RegisteredPredicateTemplateRegistry;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.ReflectiveInstantiator;

/**
 * SPI 自动装配引导器.
 *
 * <p>
 * 该类型负责把 built-in provider 与基于 {@link java.util.ServiceLoader} 的扩展统一装配到
 * 当前解释器运行环境中, 包括：
 * </p>
 * <ul>
 * <li>{@link CompiledPredicateProvider}</li>
 * <li>{@link PredicateExpressionTemplateProvider}</li>
 * <li>{@link RegisteredPredicateTemplateSpiProvider}</li>
 * </ul>
 *
 * <p>
 * 正常情况下优先使用标准 {@link ServiceLoader}；
 * 若运行环境或测试类路径不支持, 则回退到扫描 {@code META-INF/services/*} 资源.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class ServiceLoaderBootstrap {

    private static volatile boolean loaded;

    private ServiceLoaderBootstrap() {
    }

    /**
     * 确保 SPI 只被装载一次.
     */
    public static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (ServiceLoaderBootstrap.class) {
            if (loaded) {
                return;
            }
            // 所有 built-in 与 SPI provider 注册完成后, 再把 loaded 对外可见；
            // 避免冷启动并发请求观察到“已加载”但注册表尚未填满的中间态.
            BuiltInCompiledPredicateProviders.registerAll();
            loadCompiledPredicateProviders();
            loadPredicateExpressionTemplateProviders();
            loadRegisteredPredicateTemplateProviders();
            loaded = true;
        }
    }

    /**
     * 清空“已加载”标记并重新装配 SPI.
     *
     * <p>
     * 主要用于测试场景.
     * </p>
     */
    public static synchronized void reload() {
        loaded = false;
        ensureLoaded();
    }

    private static void loadPredicateExpressionTemplateProviders() {
        for (PredicateExpressionTemplateProvider provider : load(PredicateExpressionTemplateProvider.class)) {
            PredicateExpressionRegistry.register(provider.name(), provider.template());
        }
    }

    private static void loadCompiledPredicateProviders() {
        for (CompiledPredicateProvider provider : load(CompiledPredicateProvider.class)) {
            CompiledPredicateProviderRegistry.register(provider.annotationType(), provider);
        }
    }

    private static void loadRegisteredPredicateTemplateProviders() {
        for (RegisteredPredicateTemplateSpiProvider provider : load(RegisteredPredicateTemplateSpiProvider.class)) {
            RegisteredPredicateTemplateRegistry.register(provider.name(), provider);
        }
    }

    private static <T> Iterable<T> load(final Class<T> type) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (null == loader) {
            loader = ServiceLoaderBootstrap.class.getClassLoader();
        }
        Map<String, T> instances = new LinkedHashMap<String, T>();
        try {
            // 优先走标准 ServiceLoader；正常模块化场景下, 这里是首选路径.
            for (T provider : ServiceLoader.load(type, loader)) {
                instances.put(provider.getClass().getName(), provider);
            }
        } catch (Throwable ignore) {
            // 某些测试或非标准运行环境下, 标准 ServiceLoader 可能不可用；继续走资源扫描兜底.
        }
        // 再扫描 META-INF/services, 确保测试类路径与非模块化发布形态都能完成装配.
        for (T provider : loadFromServicesResource(type, loader)) {
            instances.put(provider.getClass().getName(), provider);
        }
        return new ArrayList<T>(instances.values());
    }

    private static <T> Iterable<T> loadFromServicesResource(final Class<T> type, final ClassLoader loader) {
        ArrayList<T> providers = new ArrayList<T>();
        String resourceName = "META-INF/services/" + type.getName();
        try {
            Enumeration<URL> resources = loader.getResources(resourceName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream inputStream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String line;
                    while (null != (line = reader.readLine())) {
                        String className = sanitize(line);
                        if (className.isEmpty()) {
                            continue;
                        }
                        providers.add(instantiate(type, className, loader));
                    }
                }
            }
        } catch (IOException ignore) {
            // ignore
        }
        return providers;
    }

    private static String sanitize(final String line) {
        int commentIndex = line.indexOf('#');
        String raw = commentIndex >= 0 ? line.substring(0, commentIndex) : line;
        return raw.trim();
    }

    private static <T> T instantiate(final Class<T> type, final String className, final ClassLoader loader) {
        Class<?> implClass = ReflectiveInstantiator.loadClass(className, loader, "Failed to load SPI provider: ");
        return ReflectiveInstantiator.instantiate(implClass, type, "Failed to instantiate SPI provider: ");
    }

}
