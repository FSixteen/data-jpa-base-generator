package io.github.fsixteen.data.jpa.base.generator.plugins.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;

/**
 * 请求参数类到 {@link AnnotationCollection} 的缓存入口.
 *
 * <p>
 * 这是解释器公开调用链中的第一层稳定入口：
 * 外部项目通常先通过请求参数类型拿到 {@link AnnotationCollection},
 * 再继续进入 {@code toComputerCollection()} 和 compiled 主链路.
 * </p>
 *
 * <p>
 * 缓存项保存的是“已经完成注解扫描和 compiled 规格编译”的结果,
 * 因此同一个 query model 类型只需要构建一次.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public final class CollectionCache {

    /** 请求参数类到编译后注解集合的缓存. */
    private static final Map<Class<?>, AnnotationCollection> ANNOTATION_COLLECTION_CACHE = new ConcurrentHashMap<>();

    private CollectionCache() {
    }

    /**
     * 获取请求参数类对应的注解集合. <br>
     * 这是外部项目最常见的稳定入口之一, 因此缓存键和值类型不应轻易改动；
     * 即便内部执行已经统一到 compiled 主路径, 这里仍返回 {@link AnnotationCollection}.
     *
     * @param clazz 参与请求计算的 query model 类型
     * @return 已编译的注解集合
     */
    public static AnnotationCollection getAnnotationCollection(Class<?> clazz) {
        AnnotationCollection cached = ANNOTATION_COLLECTION_CACHE.get(clazz);
        if (null != cached && !cached.isEmpty()) {
            return cached;
        }
        synchronized (CollectionCache.class) {
            AnnotationCollection refreshed = ANNOTATION_COLLECTION_CACHE.get(clazz);
            if (null != refreshed && !refreshed.isEmpty()) {
                return refreshed;
            }
            AnnotationCollection rebuilt = AnnotationCollection.Builder.of().with(clazz).build();
            ANNOTATION_COLLECTION_CACHE.put(clazz, rebuilt);
            return rebuilt;
        }
    }

}
