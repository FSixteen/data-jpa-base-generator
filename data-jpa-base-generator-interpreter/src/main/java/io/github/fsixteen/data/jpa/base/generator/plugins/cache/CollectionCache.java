package io.github.fsixteen.data.jpa.base.generator.plugins.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;

/**
 * 类注解描述信息集合缓存.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public final class CollectionCache {

    /** 参数计算字段内容. */
    private static final Map<Class<?>, AnnotationCollection> ANNOTATION_COLLECTION_CACHE = new ConcurrentHashMap<>();

    /**
     * 计算参数计算的内容.<br>
     *
     * @param clazz 参与请求计算的实例类
     */
    public static AnnotationCollection getAnnotationCollection(Class<?> clazz) {
        if (!ANNOTATION_COLLECTION_CACHE.containsKey(clazz) || ANNOTATION_COLLECTION_CACHE.get(clazz).isEmpty()) {
            synchronized (CollectionCache.class) {
                if (!ANNOTATION_COLLECTION_CACHE.containsKey(clazz) || ANNOTATION_COLLECTION_CACHE.get(clazz).isEmpty()) {
                    ANNOTATION_COLLECTION_CACHE.put(clazz, AnnotationCollection.Builder.of().with(clazz).build());
                }
            }
        }
        return ANNOTATION_COLLECTION_CACHE.get(clazz);
    }

}
